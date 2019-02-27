package fr.badblock.bukkit.games.pvpbox.inventories.objects;

import lombok.Getter;

@Getter public class InventoryAction {

	private InventoryActionType actionType;
	private CustomItemAction	action;
	private String			   	actionData;

}
