package fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.pearlswar;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.gameapi.players.BadblockPlayer;

public class PearlsWarDescSelectorItem extends CustomItem {

	public PearlsWarDescSelectorItem() {
		super("hub.items.pearlswarselectoritem.desc", Material.ENDER_PEARL,
				"hub.items.pearlswarselectoritem.desc.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {

	}

}
