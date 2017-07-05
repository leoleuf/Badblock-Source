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

public class SkyBlockSelectorItem extends GameSelectorItem {

	public SkyBlockSelectorItem() {
		// super("§bSkyBlock", Material.GRASS);
		super("hub.items.skyblockselectoritem", Material.GRASS, "hub.items.skyblockselectoritem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public List<String> getGames() {
		return Arrays.asList("skyb");
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		Location location = ConfigUtils.getLocation(BadBlockHub.getInstance(), "skyblock");
		if (location == null) {
			player.sendTranslatedMessage("hub.gameunavailable");
			return;
		}
		if (itemAction.equals(ItemAction.INVENTORY_RIGHT_CLICK)) {
			player.teleport(location);
		} else if (itemAction.equals(ItemAction.INVENTORY_LEFT_CLICK))
			player.sendPlayer("skyb");
	}

	@Override
	public BadblockGame getGame() {
		return null;
	}

	@Override
	public boolean isMiniGame() {
		return false;
	}
	
	@Override
	public String getGamePrefix() {
		return "skyb";
	}

}