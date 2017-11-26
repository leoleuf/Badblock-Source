
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.defaults.MountItem;

public class EndermanMountItem extends MountItem {

	@SuppressWarnings("deprecation")
	public EndermanMountItem() {
		super("enderman", Material.getMaterial(383), (byte) 58);
	}

	@Override
	protected EntityType getMountEntityType() {
		return EntityType.ENDERMAN;
	}

}
