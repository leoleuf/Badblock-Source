package fr.badblock.common.shoplinker.bukkit.inventories.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter public class InventoryAction {

	private InventoryActionType actionType;
	private CustomItemAction	action;
	private String			   	actionData;
	private InventoryShopObject shopData;

}
