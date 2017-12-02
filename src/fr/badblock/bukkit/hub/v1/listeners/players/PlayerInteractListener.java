package fr.badblock.bukkit.hub.v1.listeners.players;

import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.chests.objects.ChestLoader;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.chests.objects.ChestOpener;
import fr.badblock.bukkit.hub.v1.listeners._HubListener;
import fr.badblock.bukkit.hub.v1.listeners.vipzone.RaceListener;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.bukkit.hub.v1.signs.GameSign;
import fr.badblock.bukkit.hub.v1.signs.GameSignManager;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.ConfigUtils;

public class PlayerInteractListener extends _HubListener {

	@EventHandler (ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent event) {
		BadblockPlayer player = (BadblockPlayer) event.getPlayer();
		Action action = event.getAction();
		HubPlayer lobbyPlayer = HubPlayer.get(player);
		if (player.getItemInHand().getType().equals(Material.SNOW_BALL))
		{
			return;
		}
		if (lobbyPlayer.isChestFreeze()) {
			event.setCancelled(true);
			return;
		}
		if (!player.hasAdminMode())
			event.setCancelled(true);
		if (event.getClickedBlock() != null) {
			// Chest
			Block block = event.getClickedBlock();
			Location location = block.getLocation();
			for (ChestOpener chestOpener : ChestLoader.getInstance().getOpeners()) {
				Location opener = chestOpener.getOpenerChestLocation();
				if (opener.getX() == location.getX() && opener.getY() == location.getY() && opener.getZ() == location.getZ() && location.getWorld().getName().equals(opener.getWorld().getName())) {
					ChestLoader.getInstance().open(player, chestOpener);
					return;
				}
			}
			// Sign
			if (block.getType().name().contains("SIGN"))
			{
				for (GameSign gameSign : GameSignManager.stockage.values())
				{
					Location loc = ConfigUtils.convertStringToLocation(gameSign.getLocation());
					if (loc.getX() == location.getX() && loc.getY() == location.getY() && loc.getZ() == location.getZ())
					{
						gameSign.click(player);
						break;
					}
				}
			}
		}
		// Right click on a basic item
		if (player.getItemInHand() == null || player.getItemInHand().getType().equals(Material.AIR))
			return;
		ItemStack itemStack = player.getItemInHand();
		ItemAction itemAction = ItemAction.get(action);
		for (Entry<CustomItem, List<ItemStack>> entry : CustomItem.getItems().entrySet()) {
			if (!(entry.getKey() instanceof CustomItem))
				continue;
			CustomItem customItem = entry.getKey();
			if (itemAction == null)
				continue;
			if (!customItem.getActions().contains(itemAction))
				continue;
			if (!customItem.toItemStack(player).isSimilar(itemStack))
				continue;
			if (lobbyPlayer.hasSpam(player))
				return;
			if (RaceListener.racePlayers.containsKey(player)) {
				player.sendTranslatedMessage("hub.race.youcannotdothatinrace");
				return;
			}
			if (customItem.getNeededPermission() != null && !player.hasPermission(customItem.getNeededPermission())) {
				player.sendMessage(customItem.getErrorNeededPermission());
				return;
			}
			customItem.onClick(player, itemAction, event.getClickedBlock());
		}
	}

}
