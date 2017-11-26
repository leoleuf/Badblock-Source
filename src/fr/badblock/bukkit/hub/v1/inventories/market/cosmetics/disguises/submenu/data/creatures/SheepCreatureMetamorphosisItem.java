
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.creatures;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.abstracts.MetamorphosisCreatureItem;

public class SheepCreatureMetamorphosisItem extends MetamorphosisCreatureItem {

	@SuppressWarnings("deprecation")
	public SheepCreatureMetamorphosisItem() {
		super("sheep", Material.getMaterial(383), (byte) 91);
	}

	@Override
	protected EntityType getMetamorphosisEntityType() {
		return EntityType.SHEEP;
	}

}
