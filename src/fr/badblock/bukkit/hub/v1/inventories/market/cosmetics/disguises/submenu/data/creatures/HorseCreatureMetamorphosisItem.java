
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.creatures;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.abstracts.MetamorphosisCreatureItem;

public class HorseCreatureMetamorphosisItem extends MetamorphosisCreatureItem {

	@SuppressWarnings("deprecation")
	public HorseCreatureMetamorphosisItem() {
		super("horse", Material.getMaterial(383), (byte) 100);
	}

	@Override
	protected EntityType getMetamorphosisEntityType() {
		return EntityType.HORSE;
	}

}
