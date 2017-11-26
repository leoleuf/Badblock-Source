
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.creatures;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.abstracts.MetamorphosisCreatureItem;

public class CreeperCreatureMetamorphosisItem extends MetamorphosisCreatureItem {

	@SuppressWarnings("deprecation")
	public CreeperCreatureMetamorphosisItem() {
		super("creeper", Material.getMaterial(383), (byte) 50);
	}

	@Override
	protected EntityType getMetamorphosisEntityType() {
		return EntityType.CREEPER;
	}

}
