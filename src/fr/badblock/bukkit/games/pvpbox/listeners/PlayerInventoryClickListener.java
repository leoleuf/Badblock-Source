package fr.badblock.bukkit.games.pvpbox.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.games.pvpbox.inventories.InventoriesLoader;
import fr.badblock.bukkit.games.pvpbox.inventories.objects.InventoryActionManager;
import fr.badblock.bukkit.games.pvpbox.inventories.objects.InventoryActionType;
import fr.badblock.bukkit.games.pvpbox.inventories.objects.InventoryItemObject;
import fr.badblock.bukkit.games.pvpbox.inventories.objects.InventoryObject;
import fr.badblock.bukkit.games.pvpbox.inventories.objects.ItemAction;
import fr.badblock.bukkit.games.pvpbox.players.BoxPlayer;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;

public class PlayerInventoryClickListener extends BadListener
{

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event)
	{
		if (!(event.getWhoClicked() instanceof Player))
		{
			return;
		}
		
		BadblockPlayer player = (BadblockPlayer) event.getWhoClicked();
		
		// Get basic objects
		ItemStack itemStack = event.getCurrentItem();
		
		// Work with basic inventories
		workBasicInventories(player, itemStack, event);
	}
	
	private boolean workBasicInventories(BadblockPlayer player, ItemStack itemStack, InventoryClickEvent event)
	{
		ItemAction itemAction = ItemAction.get(event.getAction());
		InventoryActionType actionType = InventoryActionType.get(itemAction);
		
		if (itemStack != null)
		{
			BoxPlayer boxPlayer = BoxPlayer.get(player);
			
			if (boxPlayer == null)
			{
				return false;
			}
			
			String inventoryName = boxPlayer.getInventory();
			
			// Handling custom inventories
			if (inventoryName != null && !inventoryName.isEmpty() && event.getClickedInventory().getType().equals(InventoryType.CHEST))
			{
				return handleInventoryManager(event, player, inventoryName, actionType, InventoriesLoader.getInventory(inventoryName));
			}
			
			// Default join inventory handling
			return handleInventoryManager(event, player, "default", actionType, InventoriesLoader.getInventory("default"));
		}
		return false;
	}

	private boolean handleInventoryManager(InventoryClickEvent event, BadblockPlayer player, String inventoryName, InventoryActionType actionType, InventoryObject inventoryObject)
	{
		boolean done = false;
		
		if (inventoryObject == null)
		{
			return false;
		}
		
		InventoryItemObject itemObject = null;
		
		for (InventoryItemObject item : inventoryObject.getItems())
		{
			if (event.getSlot() == item.getPlace())
			{
				done = true;
				itemObject = item;
				break;
			}
		}

		if (itemObject == null)
		{
			return done;
		}
		
		event.setCancelled(true);
		
		if (itemObject != null && ((event.getCurrentItem() != null && event.getCurrentItem().getItemMeta() != null
				&& itemObject.getName().equalsIgnoreCase(event.getCurrentItem().getItemMeta().getDisplayName()) || itemObject.getName().isEmpty())))
		{
			// Set cancelled
			event.setCancelled(true);
			
			InventoryActionManager.handle(player, inventoryName, itemObject, actionType);
		}
		
		return done;
	}

}