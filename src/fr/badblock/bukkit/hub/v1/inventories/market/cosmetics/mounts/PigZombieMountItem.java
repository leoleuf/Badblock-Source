
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.defaults.MountItem;

public class PigZombieMountItem extends MountItem {

	@SuppressWarnings("deprecation")
	public PigZombieMountItem() {
		super("pigzombie", Material.getMaterial(383), (byte) 57);
	}

	@Override
	protected EntityType getMountEntityType() {
		return EntityType.PIG_ZOMBIE;
	}

}
