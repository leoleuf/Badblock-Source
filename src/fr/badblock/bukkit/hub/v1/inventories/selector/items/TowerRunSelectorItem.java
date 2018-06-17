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

public class TowerRunSelectorItem extends GameSelectorItem {

	public TowerRunSelectorItem() {
		// super("§bTower", Material.NETHER_FENCE);
		super("hub.items.towerrunselectoritem", Material.NETHER_FENCE, "hub.items.towerrunselectoritem.lore");
		this.setFakeEnchantment(true);
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public List<String> getGames() {
		return Arrays.asList("towerE");
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		Location location = ConfigUtils.getLocation(BadBlockHub.getInstance(), "towerrun");
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
		return "towerE";
	}

	@Override
	public String getBoosterPrefix() {
		return "tower";
	}

}
