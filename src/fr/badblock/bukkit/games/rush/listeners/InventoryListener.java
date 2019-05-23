package fr.badblock.bukkit.games.rush.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.games.rush.inventories.InventoriesLoader;
import fr.badblock.bukkit.games.rush.inventories.objects.InventoryActionManager;
import fr.badblock.bukkit.games.rush.inventories.objects.InventoryActionType;
import fr.badblock.bukkit.games.rush.inventories.objects.InventoryItemObject;
import fr.badblock.bukkit.games.rush.inventories.objects.InventoryObject;
import fr.badblock.bukkit.games.rush.inventories.objects.ItemAction;
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
		if (!inGame())
		{
			return;
		}

		if 	(itemStack == null || itemStack.getType().equals(Material.AIR))
		{
			return;
		}

		if (itemStack != null && bukkitInventory != null && bukkitInventory.getName() != null && !bukkitInventory.getName().isEmpty()) {
			String inventoryName = InventoryActionManager.getInventory(player);
			if (inventoryName != null && !inventoryName.isEmpty() && bukkitInventory.getType().equals(InventoryType.CHEST)) {
				InventoryObject inventory = InventoriesLoader.getInventory(inventoryName);
				if (inventory != null && inventory.getName() != null && !inventory.getName().isEmpty() && bukkitInventory.getName().length() >= 4
						&& (bukkitInventory.getName().contains("§") || bukkitInventory.getName().contains("&"))) {
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
