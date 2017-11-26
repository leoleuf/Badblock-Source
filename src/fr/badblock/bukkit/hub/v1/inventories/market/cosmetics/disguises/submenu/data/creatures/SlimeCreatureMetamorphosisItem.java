
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.creatures;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.abstracts.MetamorphosisCreatureItem;

public class SlimeCreatureMetamorphosisItem extends MetamorphosisCreatureItem {

	@SuppressWarnings("deprecation")
	public SlimeCreatureMetamorphosisItem() {
		super("slime", Material.getMaterial(383), (byte) 55);
	}

	@Override
	protected EntityType getMetamorphosisEntityType() {
		return EntityType.SLIME;
	}

}
