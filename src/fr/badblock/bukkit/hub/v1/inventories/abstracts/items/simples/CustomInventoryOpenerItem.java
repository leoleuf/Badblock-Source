
package fr.badblock.bukkit.hub.v1.inventories.abstracts.items.simples;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomPlayerInventory;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.gameapi.players.BadblockPlayer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@SuppressWarnings("rawtypes")
public class CustomInventoryOpenerItem extends CustomItem {

	private Class clazz;
	private boolean playerInventory;

	public CustomInventoryOpenerItem(Class clazz, String className, Material material) {
		this(clazz, className, material, (byte) 0);
		this.setClazz(clazz);
	}

	public CustomInventoryOpenerItem(Class clazz, String className, Material material, byte data) {
		super("hub.items." + className.toLowerCase(), material, data, "hub.items." + className.toLowerCase() + ".lore");
		this.setClazz(clazz);
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		if (this.isPlayerInventory()) {
			CustomPlayerInventory.get(clazz, player).open();
			return;
		}
		CustomInventory.get(this.getClazz()).open(player);
	}

}
