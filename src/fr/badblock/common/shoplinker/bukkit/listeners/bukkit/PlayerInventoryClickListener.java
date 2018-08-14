package fr.badblock.common.shoplinker.bukkit.listeners.bukkit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.badblock.common.shoplinker.api.objects.TempBuyObject;
import fr.badblock.common.shoplinker.bukkit.CrystalsBuyManager;
import fr.badblock.common.shoplinker.bukkit.ShopLinker;
import fr.badblock.common.shoplinker.bukkit.inventories.InventoriesLoader;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryActionManager;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryActionType;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryItemObject;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryObject;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.ItemAction;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.TempInventoryObject;
import fr.badblock.common.shoplinker.bukkit.players.ShopPlayer;

public class PlayerInventoryClickListener implements Listener {

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
		Player player = (Player) event.getWhoClicked();
		// default inventory
		ShopLinker shopLinker = ShopLinker.getInstance();
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
			else if (bukkitInventory.getName().startsWith(shopLinker.getConfirmInventoryName().replace("%0", "")))
			{
				event.setCancelled(true);
				ShopPlayer shopPlayer = ShopPlayer.get(player);
				// Retour
				if (itemStack.getType().equals(Material.WOOD_DOOR))
				{
					TempInventoryObject lastInventoryObject = shopPlayer.getLastInventory();
					if (lastInventoryObject != null)
					{
						InventoryActionManager.openInventory(player, lastInventoryObject);
					}
					else
					{
						ShopLinker.getConsole().sendMessage(ChatColor.RED + "Someting gone wrong. The last inventory object of the player is null.");
						player.sendMessage(shopLinker.getErrorMessage());
					}
				}
				else if (itemStack.getType().equals(Material.EMERALD_BLOCK))
				{
					TempBuyObject buyObject = shopPlayer.getBuy();
					if (buyObject != null)
					{
						player.closeInventory();
						CrystalsBuyManager.buy(player, buyObject);
					}
					else
					{
						ShopLinker.getConsole().sendMessage(ChatColor.RED + "Someting gone wrong. The buy object of the player is null.");
						player.sendMessage(shopLinker.getErrorMessage());
					}
				}
				else if (itemStack.getType().equals(Material.REDSTONE_BLOCK))
				{
					player.closeInventory();
					player.sendMessage(shopLinker.getCancelledMessage());
				}
			}
		}
	}

}