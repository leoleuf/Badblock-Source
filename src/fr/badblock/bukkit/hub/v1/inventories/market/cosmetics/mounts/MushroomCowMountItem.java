
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.defaults.MountItem;

public class MushroomCowMountItem extends MountItem {

	@SuppressWarnings("deprecation")
	public MushroomCowMountItem() {
		super("mushroomcow", Material.getMaterial(40), (byte) 0);
	}

	@Override
	protected EntityType getMountEntityType() {
		return EntityType.MUSHROOM_COW;
	}

}
