
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.defaults.MountItem;

public class SnowmanMountItem extends MountItem {

	@SuppressWarnings("deprecation")
	public SnowmanMountItem() {
		super("snowman", Material.getMaterial(80), (byte) 0);
	}

	@Override
	protected EntityType getMountEntityType() {
		return EntityType.SNOWMAN;
	}

}
