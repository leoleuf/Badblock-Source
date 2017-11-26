
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.boosters.items;

import org.bukkit.Material;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.simples.CustomInventoryOpenerItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.boosters.inventories.PlayerBoostersSelectorInventory;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerBoostersSelectorItem extends CustomInventoryOpenerItem {

	public PlayerBoostersSelectorItem() {
		super(PlayerBoostersSelectorInventory.class, "PlayerBoostersSelectorItem", Material.REDSTONE);
		this.setPlayerInventory(true);
	}

}
