package fr.badblock.common.shoplinker.bukkit.inventories.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.badblock.common.shoplinker.api.ShopLinkerAPI;
import fr.badblock.common.shoplinker.bukkit.ShopLinker;
import fr.badblock.common.shoplinker.bukkit.inventories.BukkitInventories;
import fr.badblock.common.shoplinker.bukkit.players.ShopPlayer;
import fr.badblock.common.shoplinker.bukkit.utils.Callback;

public class InventoryActionManager {

	protected static ShopLinkerAPI	 linkerAPI		   = new ShopLinkerAPI(ShopLinker.getInstance().getRabbitService());
	private static Map<UUID, String> playerInventories = new HashMap<>();

	public static void handle(Player player, String inventoryName, InventoryItemObject object, InventoryActionType type, ItemStack offerStack) {
		// No defined type
		if (type == null) return;
		for (InventoryAction inventoryAction : object.getActions()) {
			if (!inventoryAction.getActionType().equals(type)) continue;
			CustomItemAction action = inventoryAction.getAction();
			if (action == null) {
				ShopLinker.getConsole().sendMessage(ChatColor.RED + "Unknown action set on this object (Position: " + object.getPlace() + " / InventoryName: " + inventoryName + ").");
				continue;
			}
			if (action.equals(CustomItemAction.NOTHING)) continue;
			// TODO: do antispam
			String actionData = inventoryAction.getActionData();
			InventoryShopObject shopObject = inventoryAction.getShopData();
			switch (action) {
			case EXECUTE_COMMAND:
				executeCommand(player, action, actionData);
				break;
			case OPEN_INV:
				openInventory(player, action, actionData);
				break;
			case CLOSE_INV:
				closeInventory(player, action, actionData);
				break;
			case BUY_COMMAND:
				buyCommand(player, action, shopObject, object, offerStack);
				break;
			default:
				ShopLinker.getConsole().sendMessage(ChatColor.RED + "No action set on this object. (Position: " + object.getPlace() + " / InventoryName: " + inventoryName + ")");
				break;
			}
			break;
		}
	}

	public static void openInventory(Player player, TempInventoryObject tempInventoryObject)
	{
		openInventory(player, tempInventoryObject.getAction(), tempInventoryObject.getActionData());
	}

	public static void openInventory(Player player, CustomItemAction action, String actionData)
	{
		// Shop player
		ShopPlayer shopPlayer = ShopPlayer.get(player);

		// Save last inventory
		shopPlayer.setLastInventory(new TempInventoryObject(action, actionData));

		// Inventory open
		String inventoryName = actionData;
		BukkitInventories.getInventory(player, inventoryName, new Callback<Inventory>()
		{

			@Override
			public void done(Inventory inventory, Throwable error) {
				if (inventory == null) {
					closeInventory(player, action, null);
					return;
				}
				Bukkit.getScheduler().runTask(ShopLinker.getInstance(), new Runnable()
				{
					@Override
					public void run()
					{
						player.closeInventory(); // standby
						player.openInventory(inventory);
						setInventory(player, inventoryName);
					}
				});
			}

		});
	}

	private static void closeInventory(Player player, CustomItemAction action, String actionData) {
		if (actionData != null && !actionData.isEmpty()) {
			// TODO: do another action ?
		}
		setInventory(player, null);
		player.closeInventory();
	}

	private static void executeCommand(Player player, CustomItemAction action, String actionData) {
		player.performCommand(actionData);
	}

	private static void buyCommand(Player player, CustomItemAction action, InventoryShopObject shopObject, InventoryItemObject inventoryItemObject, ItemStack offerStack) {
		BukkitInventories.createConfirmInventory(player, action, shopObject, inventoryItemObject, offerStack);
	}

	public static void setInventory(Player player, String inventoryName) {
		UUID uniqueId = player.getUniqueId();
		playerInventories.put(uniqueId, inventoryName);
	}

	public static String getInventory(Player player) {
		UUID uniqueId = player.getUniqueId();
		return playerInventories.get(uniqueId);
	}

}
