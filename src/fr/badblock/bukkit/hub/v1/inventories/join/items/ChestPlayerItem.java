package fr.badblock.bukkit.hub.v1.inventories.join.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.animhost.AnimHostInventory;
import fr.badblock.gameapi.players.BadblockPlayer;

public class ChestPlayerItem extends CustomItem {

	public ChestPlayerItem() {
		super("hub.items.chestplayeritem", Material.ENDER_CHEST, "hub.items.chestplayeritem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK, ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK, ItemAction.RIGHT_CLICK_AIR, ItemAction.RIGHT_CLICK_BLOCK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		CustomInventory.get(AnimHostInventory.class).open(player);
	}

}
