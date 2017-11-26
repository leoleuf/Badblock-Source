
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.parent.executors;

import org.bukkit.Material;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.simples.CustomInventoryOpenerItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.parent.inventories.parent.MetamorphosisInventory;

public class BackMetamorphosisChoiceCosmeticsItem extends CustomInventoryOpenerItem {

	public BackMetamorphosisChoiceCosmeticsItem() {
		super(MetamorphosisInventory.class, "BackMetamorphosisChoiceCosmeticsItem", Material.BARRIER);
	}

}
