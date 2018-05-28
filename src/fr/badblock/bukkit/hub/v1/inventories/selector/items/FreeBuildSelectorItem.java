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

public class FreeBuildSelectorItem extends GameSelectorItem {

	public FreeBuildSelectorItem() {
		// super("§bFreeBuild", Material.BRICK);
		super("hub.items.freebuildselectoritem", Material.BRICK, "hub.items.freebuildselectoritem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public List<String> getGames() {
		return Arrays.asList("fb", "fb2");
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		Location location = ConfigUtils.getLocation(BadBlockHub.getInstance(), "freebuild");
		if (location == null)
			player.sendTranslatedMessage("hub.gameunavailable");
		else {
			if (itemAction.equals(ItemAction.INVENTORY_LEFT_CLICK)) {
				player.sendPlayer("fb");
				player.sendMessage("§b➤ §7Téléportation  §7en § FreeBuild§7...");
	            player.closeInventory();
	     	}
		    else
		    {
		        player.teleport(location);
			}
		}
	}

	@Override
	public BadblockGame getGame() {
		return BadblockGame.FREEBUILD;
	}

	@Override
	public boolean isMiniGame() {
		return false;
	}

	@Override
	public String getGamePrefix() {
		return "fb";
	}

}
