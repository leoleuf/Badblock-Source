package fr.badblock.bukkit.hub.inventories.selector.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.BadBlockHub;
import fr.badblock.bukkit.hub.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.inventories.selector.submenus.inventories.TowerChooserInventory;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.run.BadblockGame;
import fr.badblock.gameapi.utils.ConfigUtils;

public class TowerSelectorItem extends GameSelectorItem {

	public TowerSelectorItem() {
		// super("§bTower", Material.NETHER_FENCE);
		super("hub.items.towerselectoritem", Material.NETHER_FENCE, "hub.items.towerselectoritem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public List<String> getGames() {
		return Arrays.asList("tower", "tower2v2", "tower4v4");
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		if (itemAction.equals(ItemAction.INVENTORY_LEFT_CLICK)) {
			CustomInventory.get(TowerChooserInventory.class).open(player);
			return;
		}
		Location location = ConfigUtils.getLocation(BadBlockHub.getInstance(), "tower");
		if (location == null)
			player.sendTranslatedMessage("hub.gameunavailable");
		else{
			player.teleport(location);
		}
	}

	@Override
	public BadblockGame getGame() {
		return BadblockGame.TOWER;
	}

	@Override
	public boolean isMiniGame() {
		return true;
	}
	
	@Override
	public String getGamePrefix() {
		return "tower";
	}

}
