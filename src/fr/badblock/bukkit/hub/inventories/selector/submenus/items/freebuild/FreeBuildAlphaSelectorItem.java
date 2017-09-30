package fr.badblock.bukkit.hub.inventories.selector.submenus.items.freebuild;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.inventories.selector.submenus.items.SubGameSelectorItem;
import fr.badblock.bukkit.hub.utils.TimeUtils;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.threading.TaskManager;

public class FreeBuildAlphaSelectorItem extends SubGameSelectorItem {

	@SuppressWarnings("deprecation")
	public FreeBuildAlphaSelectorItem() {
		super("hub.items.freebuildselectoritem.alpha", Material.BANNER, DyeColor.RED.getDyeData(),
				"hub.items.freebuildselectoritem.alpha.lore");
		this.setFakeEnchantment(true);
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public String getGame() {
		return "fb";
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		player.sendMessage("§b▶ §7Entrée §bsolitaire §7dans la file §b(FreeBuild Alpha)§7...");
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				player.sendPlayer("fb");
				/*if (!player.hasPermission("others.mod.connect")) {
					SEntryInfosListener.tempPlayers.put(player.getName(), System.currentTimeMillis() + SEntryInfosListener.tempTime);
					SEntryInfosListener.tempPlayersRank.put(player.getName(), player.getMainGroup());
					SEntryInfosListener.tempPlayersUUID.put(player.getName(), player.getUniqueId());
					SEntryInfosListener.tempPlayersPropertyMap.put(player.getName(), ((CraftPlayer)player).getHandle().getProfile().getProperties());
				}*/
			}
		};
		if (player.hasPermission("matchmaking.priority")) {
			player.sendMessage("§b➤ §7Téléportation §bsolitaire §7en jeu §b(FreeBuild Alpha)§7...");
			runnable.run();
		}
		else {
			player.sendMessage("§b▶ §7Entrée §bsolitaire §7dans la file §b(FreeBuild Alpha)§7...");
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
