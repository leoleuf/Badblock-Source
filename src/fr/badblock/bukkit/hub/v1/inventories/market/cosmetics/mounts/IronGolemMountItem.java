
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.defaults.MountItem;

public class IronGolemMountItem extends MountItem {

	@SuppressWarnings("deprecation")
	public IronGolemMountItem() {
		super("irongolem", Material.getMaterial(267), (byte) 0);
	}

	@Override
	protected EntityType getMountEntityType() {
		return EntityType.IRON_GOLEM;
	}

}
