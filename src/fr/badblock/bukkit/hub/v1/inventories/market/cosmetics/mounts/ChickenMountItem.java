
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.defaults.MountItem;

public class ChickenMountItem extends MountItem {

	@SuppressWarnings("deprecation")
	public ChickenMountItem() {
		super("chicken", Material.getMaterial(383), (byte) 93);
	}

	@Override
	protected EntityType getMountEntityType() {
		return EntityType.CHICKEN;
	}

}
