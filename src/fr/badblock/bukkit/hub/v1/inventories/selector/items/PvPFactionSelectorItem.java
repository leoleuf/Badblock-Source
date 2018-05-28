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
		{
			player.sendTranslatedMessage("hub.gameunavailable");
		}
		else
		{
			if (itemAction.equals(ItemAction.INVENTORY_LEFT_CLICK))
			{
				player.sendPlayer("faction");
				player.sendMessage("§b➤ §7Téléportation §7en §bFaction§7...");
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
