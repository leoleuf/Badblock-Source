
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.creatures;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.abstracts.MetamorphosisCreatureItem;

public class GuardianCreatureMetamorphosisItem extends MetamorphosisCreatureItem {

	@SuppressWarnings("deprecation")
	public GuardianCreatureMetamorphosisItem() {
		super("guardian", Material.getMaterial(383), (byte) 68);
	}

	@Override
	protected EntityType getMetamorphosisEntityType() {
		return EntityType.GUARDIAN;
	}

}
