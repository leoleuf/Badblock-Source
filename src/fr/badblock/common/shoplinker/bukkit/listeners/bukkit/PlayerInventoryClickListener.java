package fr.badblock.common.shoplinker.bukkit.listeners.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import fr.badblock.common.shoplinker.bukkit.inventories.InventoriesLoader;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryActionManager;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryActionType;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryItemObject;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryObject;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.ItemAction;

public class PlayerInventoryClickListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
		Player player = (Player) event.getWhoClicked();
		// default inventory
		ItemStack itemStack = event.getCurrentItem();
		ItemAction itemAction = ItemAction.get(event.getAction());
		InventoryActionType actionType = InventoryActionType.get(itemAction);
		// Hub player
		if (itemStack != null) {
			String inventoryName = InventoryActionManager.getInventory(player);
			if (inventoryName != null && !inventoryName.isEmpty() && event.getClickedInventory().getType().equals(InventoryType.CHEST)) {
				InventoryObject inventory = InventoriesLoader.getInventory(inventoryName);
				if (inventory != null) {
					InventoryItemObject itemObject = null;
					for (InventoryItemObject item : inventory.getItems()) {
						if (event.getSlot() == item.getPlace()) {
							itemObject = item;
							break;
						}
					}
					if (itemObject != null) {
						event.setCancelled(true);
						InventoryActionManager.handle(player, inventoryName, itemObject, actionType);
					}
				}
			}
		}
	}

}