
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.defaults.MountItem;

public class SkeletonMountItem extends MountItem {

	@SuppressWarnings("deprecation")
	public SkeletonMountItem() {
		super("skeleton", Material.getMaterial(383), (byte) 51);
	}

	@Override
	protected EntityType getMountEntityType() {
		return EntityType.SKELETON;
	}

}
