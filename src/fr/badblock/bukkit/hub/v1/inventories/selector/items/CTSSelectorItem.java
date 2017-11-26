package fr.badblock.bukkit.hub.v1.inventories.selector.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.run.BadblockGame;
import fr.badblock.gameapi.utils.ConfigUtils;

public class CTSSelectorItem extends GameSelectorItem {

	public CTSSelectorItem() {
		super("hub.items.ctsselectoritem", Material.SHEARS, "hub.items.ctsselectoritem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK, ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public List<String> getGames() {
		return Arrays.asList("cts8v8");
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		Location location = ConfigUtils.getLocation(BadBlockHub.getInstance(), "cts");
		if (location == null)
			player.sendTranslatedMessage("hub.gameunavailable");
		else
			player.teleport(location);
	}

	@Override
	public BadblockGame getGame() {
		return BadblockGame.CTS;
	}

	@Override
	public boolean isMiniGame() {
		return true;
	}

	@Override
	public String getGamePrefix() {
		return "cts";
	}

}
