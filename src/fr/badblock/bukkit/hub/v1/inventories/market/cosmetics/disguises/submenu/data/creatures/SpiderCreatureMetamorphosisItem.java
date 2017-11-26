
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.creatures;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.abstracts.MetamorphosisCreatureItem;

public class SpiderCreatureMetamorphosisItem extends MetamorphosisCreatureItem {

	@SuppressWarnings("deprecation")
	public SpiderCreatureMetamorphosisItem() {
		super("spider", Material.getMaterial(383), (byte) 52);
	}

	@Override
	protected EntityType getMetamorphosisEntityType() {
		return EntityType.SPIDER;
	}

}
