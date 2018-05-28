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

public class PvPBoxSelectorItem extends GameSelectorItem {

	public PvPBoxSelectorItem() {
		// super("§bPvPBox", Material.DIAMOND_CHESTPLATE);
		super("hub.items.pvpboxselectoritem", Material.DIAMOND_CHESTPLATE, "hub.items.pvpboxselectoritem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public List<String> getGames() {
		return Arrays.asList("box");
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		Location location = ConfigUtils.getLocation(BadBlockHub.getInstance(), "pvpbox");
		if (location == null)
		{
			player.sendTranslatedMessage("hub.gameunavailable");
		}
		else
		{
			if (itemAction.equals(ItemAction.INVENTORY_LEFT_CLICK))
			{
				player.sendPlayer("box");
				player.sendMessage("§b➤ §7Téléportation §7en §bPvPBox§7...");
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
		return BadblockGame.PVPBOX;
	}

	@Override
	public boolean isMiniGame() {
		return false;
	}
	
	@Override
	public String getGamePrefix() {
		return "box";
	}

}
