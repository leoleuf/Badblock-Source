
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.creatures;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.abstracts.MetamorphosisCreatureItem;

public class OcelotCreatureMetamorphosisItem extends MetamorphosisCreatureItem {

	@SuppressWarnings("deprecation")
	public OcelotCreatureMetamorphosisItem() {
		super("ocelot", Material.getMaterial(383), (byte) 98);
	}

	@Override
	protected EntityType getMetamorphosisEntityType() {
		return EntityType.OCELOT;
	}

}
