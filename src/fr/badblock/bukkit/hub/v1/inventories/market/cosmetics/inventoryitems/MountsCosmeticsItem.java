
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.inventoryitems;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.defaults.MountsInventory;
import fr.badblock.gameapi.players.BadblockPlayer;

public class MountsCosmeticsItem extends CustomItem {

	@SuppressWarnings("deprecation")
	public MountsCosmeticsItem() {
		super("hub.items.mountscosmeticsitem", Material.getMaterial(383), (byte) 90,
				"hub.items.mountscosmeticsitem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		player.closeInventory();
		/*if (!player.hasPermission("hub.soonbypass")) {
			player.sendTranslatedMessage("hub.items.functionsoon");
			return;
		}*/
		CustomInventory.get(MountsInventory.class).open(player);
	}

}
