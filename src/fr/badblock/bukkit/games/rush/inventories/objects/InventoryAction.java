package fr.badblock.bukkit.games.rush.inventories.objects;

import lombok.Getter;

@Getter public class InventoryAction {

	private InventoryActionType actionType;
	private CustomItemAction	action;
	private String			   	actionData;

}
