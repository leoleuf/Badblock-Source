package fr.badblock.common.shoplinker.api.objects;

import org.bukkit.inventory.ItemStack;

import fr.badblock.common.shoplinker.bukkit.inventories.objects.CustomItemAction;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryItemObject;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryShopObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Data
public class TempBuyObject
{
	
	private CustomItemAction	action;
	private	InventoryShopObject	shopObject;
	private InventoryItemObject	inventoryItemObject;
	private ItemStack			itemOffer;
	
}
