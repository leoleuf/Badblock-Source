package fr.badblock.bukkit.hub.v1.inventories.selector.items;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.utils.TimeUtils;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.run.BadblockGame;
import fr.badblock.gameapi.utils.ConfigUtils;
import fr.badblock.gameapi.utils.threading.TaskManager;

public class PvPFactionSelectorItem extends GameSelectorItem {

	public PvPFactionSelectorItem() {
		// super("§bPvPFaction", Material.DIAMOND_SWORD);
		super("hub.items.pvpfactionselectoritem", Material.DIAMOND_SWORD, "hub.items.pvpfactionselectoritem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public List<String> getGames() {
		return Arrays.asList("faction");
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		Location location = ConfigUtils.getLocation(BadBlockHub.getInstance(), "pvpfaction");
		if (location == null)
			player.sendTranslatedMessage("hub.gameunavailable");
		else {
			if (itemAction.equals(ItemAction.INVENTORY_LEFT_CLICK)) {
				//CustomInventory.get(FreeBuildChooserInventory.class).open(player);
				Runnable runnable = new Runnable() {
					@Override
					public void run() {
						player.sendPlayer("faction");
						/*if (!player.hasPermission("others.mod.connect")) {
							SEntryInfosListener.tempPlayers.put(player.getName(), System.currentTimeMillis() + SEntryInfosListener.tempTime);
							SEntryInfosListener.tempPlayersRank.put(player.getName(), player.getMainGroup());
							SEntryInfosListener.tempPlayersUUID.put(player.getName(), player.getUniqueId());
							SEntryInfosListener.tempPlayersPropertyMap.put(player.getName(), ((CraftPlayer)player).getHandle().getProfile().getProperties());
						}*/
					}
				};
				if (player.hasPermission("matchmaking.priority")) {
					player.sendMessage("§b➤ §7Téléportation §bsolitaire §7en jeu §b(Faction)§7...");
					runnable.run();
				}
				else {
					player.sendMessage("§b▶ §7Entrée §bsolitaire §7dans la file §b(Faction)§7...");
					int time = new Random().nextInt(20 * 16) + (20 * 8);
					player.sendMessage("§bAccès: §3Standard §b| Estimé: §b" + TimeUtils.getStringTime(time / 20));
					TaskManager.runAsyncTaskLater(runnable, time);
				}
				player.closeInventory();
				return;
			}
			player.teleport(location);
		}
	}

	@Override
	public BadblockGame getGame() {
		return BadblockGame.DAYZ;
	}

	@Override
	public boolean isMiniGame() {
		return false;
	}

	@Override
	public String getGamePrefix() {
		return "faction";
	}
	
}
