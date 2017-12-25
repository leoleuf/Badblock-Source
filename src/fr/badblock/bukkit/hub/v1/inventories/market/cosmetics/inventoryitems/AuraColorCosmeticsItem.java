
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.inventoryitems;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.AuraColorInventory;
import fr.badblock.gameapi.players.BadblockPlayer;

public class AuraColorCosmeticsItem extends CustomItem {

	public AuraColorCosmeticsItem() {
		super("hub.items.auracolorcosmeticsitem", Material.REDSTONE, "hub.items.auracolorcosmeticsitem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		player.closeInventory();
		if (!player.hasPermission("aura.color"))
		{
			player.sendTranslatedMessage("hub.items.auracolorcosmeticsitem.permission");
			return;
		}
		CustomInventory.get(AuraColorInventory.class).open(player);
	}

}
