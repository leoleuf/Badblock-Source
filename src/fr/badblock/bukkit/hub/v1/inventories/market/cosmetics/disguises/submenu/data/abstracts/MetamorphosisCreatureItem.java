
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.abstracts;

import org.bukkit.Material;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.parent.MetamorphosisItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class MetamorphosisCreatureItem extends MetamorphosisItem {

	public MetamorphosisCreatureItem(String metamorphosisName, Material material, byte data) {
		super(metamorphosisName, material, data);
	}

}
