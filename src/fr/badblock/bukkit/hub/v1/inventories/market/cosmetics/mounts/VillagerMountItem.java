
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.defaults.MountItem;

public class VillagerMountItem extends MountItem {

	@SuppressWarnings("deprecation")
	public VillagerMountItem() {
		super("villager", Material.getMaterial(383), (byte) 120);
	}

	@Override
	protected EntityType getMountEntityType() {
		return EntityType.VILLAGER;
	}

}
