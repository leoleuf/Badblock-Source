package fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.freebuild;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.SubGameSelectorItem;
import fr.badblock.bukkit.hub.v1.utils.TimeUtils;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.threading.TaskManager;

public class FreeBuildBravoSelectorItem extends SubGameSelectorItem {

	@SuppressWarnings("deprecation")
	public FreeBuildBravoSelectorItem() {
		super("hub.items.freebuildselectoritem.bravo", Material.BANNER, DyeColor.CYAN.getDyeData(),
				"hub.items.freebuildselectoritem.bravo.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public String getGame() {
		return "fb2";
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		player.sendMessage("§b▶ §7Entrée §bsolitaire §7dans la file §b(FreeBuild Bravo)§7...");
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				player.sendPlayer("fb2");
				/*if (!player.hasPermission("others.mod.connect")) {
					SEntryInfosListener.tempPlayers.put(player.getName(), System.currentTimeMillis() + SEntryInfosListener.tempTime);
					SEntryInfosListener.tempPlayersRank.put(player.getName(), player.getMainGroup());
					SEntryInfosListener.tempPlayersUUID.put(player.getName(), player.getUniqueId());
					SEntryInfosListener.tempPlayersPropertyMap.put(player.getName(), ((CraftPlayer)player).getHandle().getProfile().getProperties());
				}*/
			}
		};
		if (player.hasPermission("matchmaking.priority")) {
			player.sendMessage("§b➤ §7Téléportation §bsolitaire §7en jeu §b(FreeBuild Bravo)§7...");
			runnable.run();
		}
		else {
			player.sendMessage("§b▶ §7Entrée §bsolitaire §7dans la file §b(FreeBuild Bravo)§7...");
			int time = new Random().nextInt(20 * 9) + (20 * 3);
			player.sendMessage("§bAccès: §3Standard §b| Estimé: §b" + TimeUtils.getStringTime(time / 20));
			TaskManager.runAsyncTaskLater(runnable, time);
		}
		player.closeInventory();
	}

	@Override
	public boolean isMiniGame() {
		return false;
	}

}
