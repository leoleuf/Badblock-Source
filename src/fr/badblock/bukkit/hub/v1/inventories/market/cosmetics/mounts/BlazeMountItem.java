
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.defaults.MountItem;

public class BlazeMountItem extends MountItem {

	@SuppressWarnings("deprecation")
	public BlazeMountItem() {
		super("blaze", Material.getMaterial(383), (byte) 61);
	}

	@Override
	protected EntityType getMountEntityType() {
		return EntityType.BLAZE;
	}

}
