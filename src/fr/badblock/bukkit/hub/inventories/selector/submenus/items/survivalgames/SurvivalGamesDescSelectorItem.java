package fr.badblock.bukkit.hub.inventories.selector.submenus.items.survivalgames;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.inventories.abstracts.items.CustomItem;
import fr.badblock.gameapi.players.BadblockPlayer;

public class SurvivalGamesDescSelectorItem extends CustomItem {

	public SurvivalGamesDescSelectorItem() {
		super("hub.items.survivalgamesselectoritem.desc", Material.IRON_SWORD,
				"hub.items.survivalgamesselectoritem.desc.lore");
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