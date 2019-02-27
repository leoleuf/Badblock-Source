package fr.badblock.bukkit.games.pvpbox.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.games.pvpbox.inventories.InventoriesLoader;
import fr.badblock.bukkit.games.pvpbox.inventories.objects.InventoryActionManager;
import fr.badblock.bukkit.games.pvpbox.inventories.objects.InventoryActionType;
import fr.badblock.bukkit.games.pvpbox.inventories.objects.InventoryItemObject;
import fr.badblock.bukkit.games.pvpbox.inventories.objects.InventoryObject;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;

public class PlayerInteractListener extends BadListener
{

	@EventHandler (ignoreCancelled = false)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		BadblockPlayer player = (BadblockPlayer) event.getPlayer();
		// default inventory
		ItemStack handItem = player.getInventory().getItemInHand();
		InventoryActionType actionType = InventoryActionType.get(event.getAction());
		if (handItem != null)
		{
			InventoryObject defaultInventory = InventoriesLoader.getInventory("default");
			if (defaultInventory != null)
			{
				InventoryItemObject itemObject = null;
				for (InventoryItemObject item : defaultInventory.getItems())
				{
					if (player.getInventory().getHeldItemSlot() == item.getPlace())
					{
						itemObject = item;
						break;
					}
				}
				if (itemObject != null && handItem.getItemMeta() != null &&
						handItem.getItemMeta().getDisplayName().equalsIgnoreCase(itemObject.getName())) 
				{
					event.setCancelled(true);
					InventoryActionManager.handle(player, defaultInventory.getName(), itemObject, actionType);
				}
			}
		}
	}

}
