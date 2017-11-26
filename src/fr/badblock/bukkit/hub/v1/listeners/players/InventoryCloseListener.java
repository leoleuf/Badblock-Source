package fr.badblock.bukkit.hub.v1.listeners.players;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.hubchanger.HubChangerInventory;
import fr.badblock.bukkit.hub.v1.inventories.join.PlayerCustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.selector.SelectorInventory;
import fr.badblock.bukkit.hub.v1.listeners._HubListener;
import fr.badblock.bukkit.hub.v1.listeners.vipzone.RaceListener;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.bukkit.hub.v1.rabbitmq.Hub;
import fr.badblock.gameapi.players.BadblockPlayer;

public class InventoryCloseListener extends _HubListener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;
		BadblockPlayer player = (BadblockPlayer) event.getWhoClicked();
		
		HubPlayer lobbyPlayer = HubPlayer.get(player);
		if (lobbyPlayer.isChestFreeze()) {
			event.setCancelled(true);
			return;
		}
		lobbyPlayer.lastMove = System.currentTimeMillis() + 300_000L;
		
		// Bypass en gm
		if (player.getGameMode().equals(GameMode.CREATIVE))
			return;

		// Cancel
		event.setCancelled(!player.hasAdminMode());

		ItemStack itemStack = event.getCurrentItem();
		if (itemStack == null)
			return;
		InventoryAction inventoryAction = event.getAction();
		ItemAction itemAction = ItemAction.get(inventoryAction);
		if (lobbyPlayer.getCurrentInventory() != null) {
			for (CustomItem customItem : lobbyPlayer.getCurrentInventory().getItems().values()) {
				if (customItem == null)
					continue;
				if (!customItem.getActions().contains(itemAction))
					continue;
				if (!isSimilar(customItem.toItemStack(player), itemStack))
					continue;
				if (lobbyPlayer.hasSpam(player))
					return;
				if (RaceListener.racePlayers.containsKey(player)) {
					player.sendTranslatedMessage("hub.race.youcannotdothatinrace");
					return;
				}
				if (customItem.getNeededPermission() != null
						&& !player.hasPermission(customItem.getNeededPermission())) {
					player.sendTranslatedMessage(customItem.getErrorNeededPermission());
					return;
				}
				customItem.onClick(player, itemAction, null);
			}
		}
		for (PlayerCustomInventory playerCustomInventory : Arrays.asList(PlayerCustomInventory.values())) {
			CustomItem customItem = playerCustomInventory.getCustomItem();
			if (customItem == null)
				continue;
			if (!customItem.getActions().contains(itemAction))
				continue;
			if (!isSimilar(customItem.toItemStack(player), itemStack))
				continue;
			if (lobbyPlayer.hasSpam(player))
				return;
			if (RaceListener.racePlayers.containsKey(player)) {
				player.sendTranslatedMessage("hub.race.youcannotdothatinrace");
				return;
			}
			if (customItem.getNeededPermission() != null && !player.hasPermission(customItem.getNeededPermission())) {
				player.sendTranslatedMessage(customItem.getErrorNeededPermission());
				return;
			}
			customItem.onClick(player, itemAction, null);
		}
		if (event.getInventory().getName()
				.equals(player.getTranslatedMessage(CustomInventory.get(HubChangerInventory.class).getName())[0])) {
			if (itemStack.getType().equals(Material.BARRIER)) {
				CustomInventory.get(SelectorInventory.class).open(player);
				return;
			}
			if (itemStack.getType().equals(Material.REDSTONE_BLOCK)) {
				// player.sendMessage("§cCe hub est indisponible.");
				player.sendTranslatedMessage("hub.changer.unavailable");
				return;
			}
			ItemMeta itemMeta = itemStack.getItemMeta();
			if (itemMeta == null)
				return;
			if (itemMeta.getDisplayName().contains("connexion")) return;
			if (itemStack.getType().equals(Material.STAINED_CLAY)) {
				for (Hub hub : Hub.getHubs()) {
					if (hub.getItemStack().getItemMeta() != null && hub.getItemStack().getItemMeta().getDisplayName()
							.equals(itemStack.getItemMeta().getDisplayName())) {
						if (hub.getHubName().equals(Bukkit.getServerName())) {
							player.sendTranslatedMessage("hub.changer.alreadyconnected");
							// player.sendMessage("§cVous êtes déjà connecté sur
							// ce hub.");
							return;
						}
						if (!hub.isOnline()) {
							// player.sendMessage("§cCe hub est indisponible.");
							player.sendTranslatedMessage("hub.changer.unavailable");
							return;
						}
						if (hub.getPlayers() >= hub.getSlots()) {
							// player.sendMessage("§cCe hub est complet.");
							player.sendTranslatedMessage("hub.changer.full");
							return;
						}
						player.sendTranslatedMessage("hub.changer.teleporting", hub.getId());
						// player.sendMessage("§7Téléportation au hub n°§b" +
						// hub.getId() + "§7...");
						System.out.println("[HUB] Sending " + player.getName() + " to " + hub.getHubName() + ".");
						player.sendPlayer(hub.getHubName());
						return;
					}
				}
				player.sendTranslatedMessage("hub.changer.unknown");
				// player.sendMessage("§cHub inexistant.");
				return;
			}
		}

	}
	
	/**
	 * Check if an itemstack is similar to another one
	 * Except lore and amount
	 * @param itemStack
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public boolean isSimilar(ItemStack a, ItemStack b) {
		return a.getType().equals(b.getType()) &&
				a.getData().getData() == b.getData().getData() && 
				a.getDurability() == b.getDurability() &&
				((a.getItemMeta() != null && b.getItemMeta() != null &&
				((a.getItemMeta().getDisplayName() != null && b.getItemMeta().getDisplayName() != null
				&& a.getItemMeta().getDisplayName().equals(b.getItemMeta().getDisplayName())) 
						|| (a.getItemMeta().getDisplayName() == null && b.getItemMeta().getDisplayName() == null)))
						|| (b.getItemMeta() == null && a.getItemMeta() == null));
	}

}
