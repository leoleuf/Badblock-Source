package fr.badblock.bukkit.hub.v1.inventories.join.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.SelectorInventory;
import fr.badblock.gameapi.players.BadblockPlayer;

public class GameSelectorPlayerItem extends CustomItem {

	public GameSelectorPlayerItem() {
		// super("§6Sélecteur §7(clic droit)", Material.COMPASS, "", "§c> §7Joue
		// à différents §bjeux§7 en", "§7utilisant le §bsélecteur de jeux§7 !");
		super("hub.items.gameselectorplayeritem", Material.COMPASS, "hub.items.gameselectorplayeritem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK, ItemAction.RIGHT_CLICK_AIR,
				ItemAction.RIGHT_CLICK_BLOCK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		CustomInventory.get(SelectorInventory.class).open(player);
	}

}
