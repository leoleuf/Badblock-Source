package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.select;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.AuraColor1Inventory;
import fr.badblock.gameapi.players.BadblockPlayer;

public class AuraColor1SelectorItem extends CustomItem {

	public AuraColor1SelectorItem() {
		super("hub.items.auracolor1selectoritem", Material.WATER_BUCKET, "hub.items.auracolor1selectoritem.lore");
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
		CustomInventory.get(AuraColor1Inventory.class).open(player);
	}

}
