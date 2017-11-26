
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.creatures;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.abstracts.MetamorphosisCreatureItem;

public class MagmaCubeCreatureMetamorphosisItem extends MetamorphosisCreatureItem {

	@SuppressWarnings("deprecation")
	public MagmaCubeCreatureMetamorphosisItem() {
		super("magmacube", Material.getMaterial(383), (byte) 62);
	}

	@Override
	protected EntityType getMetamorphosisEntityType() {
		return EntityType.MAGMA_CUBE;
	}

}
