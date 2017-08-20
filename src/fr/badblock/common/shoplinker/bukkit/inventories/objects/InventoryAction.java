package fr.badblock.common.shoplinker.bukkit.inventories.objects;

import lombok.Getter;

@Getter public class InventoryAction {

	private InventoryActionType actionType;
	private CustomItemAction	action;
	private String			   	actionData;
	private InventoryShopObject shopData;

}
