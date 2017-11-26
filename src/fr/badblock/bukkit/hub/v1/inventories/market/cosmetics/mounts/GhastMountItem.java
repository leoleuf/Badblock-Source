
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.defaults.MountItem;

public class GhastMountItem extends MountItem {

	@SuppressWarnings("deprecation")
	public GhastMountItem() {
		super("ghast", Material.getMaterial(383), (byte) 56);
	}

	@Override
	protected EntityType getMountEntityType() {
		return EntityType.GHAST;
	}

}
