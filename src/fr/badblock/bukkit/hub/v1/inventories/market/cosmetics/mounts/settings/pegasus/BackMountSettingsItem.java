
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.settings.pegasus;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomPlayerInventory;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.settings.defaults.MountSettingsInventory;
import fr.badblock.gameapi.players.BadblockPlayer;

public class BackMountSettingsItem extends CustomItem {

	public BackMountSettingsItem() {
		super("hub.items.backmountsettingsitem", Material.BARRIER);
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		CustomPlayerInventory.get(MountSettingsInventory.class, player);
	}

}
