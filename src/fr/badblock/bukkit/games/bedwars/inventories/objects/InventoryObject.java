package fr.badblock.bukkit.games.bedwars.inventories.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter@Setter public class InventoryObject {

	private String 								name;
	private String 								permission;
	private int	  									lines;
	private InventoryItemObject[]	items;
	
}
