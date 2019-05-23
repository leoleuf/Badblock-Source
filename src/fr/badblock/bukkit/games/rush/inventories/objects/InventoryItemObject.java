package fr.badblock.bukkit.games.rush.inventories.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data public class InventoryItemObject {

	private String				name;
	private String[]			lore;
	private int    			 	place;
	private int				 	amount = 1;
	private String 			 	type;
	private InventoryAction[]	actions;
	private boolean				fakeEnchant;
	
}
