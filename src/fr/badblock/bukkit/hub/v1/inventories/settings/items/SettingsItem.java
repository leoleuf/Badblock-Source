package fr.badblock.bukkit.hub.v1.inventories.settings.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.settings.SettingsSettingsInventory;
import fr.badblock.gameapi.players.BadblockPlayer;

public class SettingsItem extends CustomItem {

	public SettingsItem() {
		/*
		 * super("§cParamètres", Material.NETHER_BRICK_ITEM, "",
		 * "§bFonctionnalités :",
		 * "§7- §aActiver§7/§cDésactiver§7 les demandes d'amis",
		 * "§7- §bGérer §7vos messages privés",
		 * "§7- §bGérer §7vos demandes de groupe",
		 * "§7- §bGérer §7le chat du hub");
		 */
		super("hub.items.settingsitem", Material.NETHER_BRICK_ITEM, "hub.items.settingsitem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		CustomInventory.get(SettingsSettingsInventory.class).open(player);
	}

}
