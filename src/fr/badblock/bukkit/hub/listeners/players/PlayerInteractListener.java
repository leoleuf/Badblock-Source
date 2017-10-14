package fr.badblock.bukkit.hub.listeners.players;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.google.gson.Gson;

import fr.badblock.bukkit.hub.BadBlockHub;
import fr.badblock.bukkit.hub.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.inventories.market.cosmetics.chests.objects.ChestLoader;
import fr.badblock.bukkit.hub.inventories.market.cosmetics.chests.objects.ChestOpener;
import fr.badblock.bukkit.hub.listeners._HubListener;
import fr.badblock.bukkit.hub.listeners.vipzone.RaceListener;
import fr.badblock.bukkit.hub.objects.HubPlayer;
import fr.badblock.bukkit.hub.signs.GameSign;
import fr.badblock.bukkit.hub.signs.GameSignManager;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.ConfigUtils;
import fr.badblock.rabbitconnector.RabbitPacketType;
import fr.badblock.rabbitconnector.RabbitService;
import fr.badblock.sentry.SEntry;
import fr.badblock.utils.Encodage;

public class PlayerInteractListener extends _HubListener {

	@EventHandler (ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent event) {
		BadblockPlayer player = (BadblockPlayer) event.getPlayer();
		Action action = event.getAction();
		HubPlayer lobbyPlayer = HubPlayer.get(player);
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
					System.out.println(block.getLocation().toString() + " / " + loc);
					if (loc.getX() == location.getX() && loc.getY() == location.getY() && loc.getZ() == location.getZ()) {
						{
							player.sendTranslatedMessage("hub.gameteleport");
							int time = 15 + new Random().nextInt(15);
							int addedTime = (time + 10) * 50;
							if (gameSign.getTempMap() == null) gameSign.setTempMap(new HashMap<>());
							gameSign.getTempMap().put(player.getName(), System.currentTimeMillis() + addedTime);
							String internalName = gameSign.getInternalName();
							Bukkit.getScheduler().runTaskLater(BadBlockHub.getInstance(), new Runnable()
							{
								@Override
								public void run() {
									BadBlockHub instance = BadBlockHub.getInstance();
									RabbitService service = instance.getRabbitService();
									Gson gson = instance.getGson();
									service.sendAsyncPacket("networkdocker.sentry.join", gson.toJson(new SEntry(HubPlayer.getRealName(player), internalName, false)), Encodage.UTF8, RabbitPacketType.PUBLISHER, 5000, false);
								}
							}, time);
							break;
						}
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
