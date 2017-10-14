package fr.badblock.bukkit.hub.inventories.selector.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.BadBlockHub;
import fr.badblock.bukkit.hub.inventories.abstracts.actions.ItemAction;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.run.BadblockGame;
import fr.badblock.gameapi.utils.ConfigUtils;

public class PointOutSelectorItem extends GameSelectorItem {

	public PointOutSelectorItem() {
		// super("§bRush", Material.BED);
		super("hub.items.pointoutselectoritem", Material.BLAZE_POWDER, "hub.items.pointoutselectoritem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public List<String> getGames() {
		return Arrays.asList("pointout8v8");
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		Location location = ConfigUtils.getLocation(BadBlockHub.getInstance(), "pointout");
		if (location == null) // player.sendMessage("§cCe jeu est
								// indisponible.");
			player.sendTranslatedMessage("hub.gameunavailable");
		else
			player.teleport(location);
	}

	@Override
	public BadblockGame getGame() {
		return BadblockGame.POINTOUT;
	}

	@Override
	public boolean isMiniGame() {
		return true;
	}
	
	@Override
	public String getGamePrefix() {
		return "pointout";
	}

}
