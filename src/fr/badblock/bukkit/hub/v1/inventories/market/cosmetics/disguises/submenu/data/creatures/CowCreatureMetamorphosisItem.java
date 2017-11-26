
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.creatures;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.abstracts.MetamorphosisCreatureItem;

public class CowCreatureMetamorphosisItem extends MetamorphosisCreatureItem {

	@SuppressWarnings("deprecation")
	public CowCreatureMetamorphosisItem() {
		super("cow", Material.getMaterial(383), (byte) 92);
	}

	@Override
	protected EntityType getMetamorphosisEntityType() {
		return EntityType.COW;
	}

}
