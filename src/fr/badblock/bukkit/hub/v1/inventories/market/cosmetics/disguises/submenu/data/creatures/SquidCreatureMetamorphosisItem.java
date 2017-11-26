
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.creatures;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.abstracts.MetamorphosisCreatureItem;

public class SquidCreatureMetamorphosisItem extends MetamorphosisCreatureItem {

	@SuppressWarnings("deprecation")
	public SquidCreatureMetamorphosisItem() {
		super("squid", Material.getMaterial(383), (byte) 94);
	}

	@Override
	protected EntityType getMetamorphosisEntityType() {
		return EntityType.SQUID;
	}

}
