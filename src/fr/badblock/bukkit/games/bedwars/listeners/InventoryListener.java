package fr.badblock.bukkit.games.bedwars.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.games.bedwars.inventories.InventoriesLoader;
import fr.badblock.bukkit.games.bedwars.inventories.objects.InventoryActionManager;
import fr.badblock.bukkit.games.bedwars.inventories.objects.InventoryActionType;
import fr.badblock.bukkit.games.bedwars.inventories.objects.InventoryItemObject;
import fr.badblock.bukkit.games.bedwars.inventories.objects.InventoryObject;
import fr.badblock.bukkit.games.bedwars.inventories.objects.ItemAction;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;

public class InventoryListener extends BadListener
{
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
		BadblockPlayer player = (BadblockPlayer) event.getWhoClicked();
		// default inventory
		Inventory bukkitInventory = event.getClickedInventory();
		ItemStack itemStack = event.getCurrentItem();
		ItemAction itemAction = ItemAction.get(event.getAction());
		InventoryActionType actionType = InventoryActionType.get(itemAction);
		// Hub player
		if (itemStack != null) {
			String inventoryName = InventoryActionManager.getInventory(player);
			if (inventoryName != null && !inventoryName.isEmpty() && bukkitInventory.getType().equals(InventoryType.CHEST)) {
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
						InventoryActionManager.handle(player, inventoryName, itemObject, actionType, itemStack);
					}
				}
			}
		}
	}

}
