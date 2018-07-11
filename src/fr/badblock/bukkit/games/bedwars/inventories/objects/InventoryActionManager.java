package fr.badblock.bukkit.games.bedwars.inventories.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.bukkit.games.bedwars.inventories.BukkitInventories;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.general.Callback;

public class InventoryActionManager
{

	private static Map<UUID, String> playerInventories = new HashMap<>();
	private static Map<UUID, TempInventoryObject> playerInv = new HashMap<>();

	public static void handle(BadblockPlayer player, String inventoryName, InventoryItemObject object, InventoryActionType type, ItemStack offerStack) {
		// No defined type
		if (type == null) return;
		for (InventoryAction inventoryAction : object.getActions()) {
			if (!inventoryAction.getActionType().equals(type)) continue;
			CustomItemAction action = inventoryAction.getAction();
			if (action == null) {
				continue;
			}
			if (action.equals(CustomItemAction.NOTHING)) continue;
			// TODO: do antispam
			String actionData = inventoryAction.getActionData();
			switch (action) {
			case CUSTOM_EFFECT:
				customEffect(player, action, actionData);
				break;
			case OPEN_INV:
				openInventory(player, action, actionData);
				break;
			case CLOSE_INV:
				closeInventory(player, action, actionData);
				break;
			case EXCHANGE:
				exchange(player, action, actionData);
				break;
			default:
				break;
			}
			break;
		}
	}

	public static void openInventory(BadblockPlayer player, TempInventoryObject tempInventoryObject)
	{
		openInventory(player, tempInventoryObject.getAction(), tempInventoryObject.getActionData());
	}

	public static void openInventory(BadblockPlayer player, CustomItemAction action, String actionData)
	{
		// Save last inventory
		playerInv.put(player.getUniqueId(), new TempInventoryObject(action, actionData));

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
				Bukkit.getScheduler().runTask(PluginBedWars.getInstance(), new Runnable()
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

	private static void closeInventory(BadblockPlayer player, CustomItemAction action, String actionData) {
		if (actionData != null && !actionData.isEmpty()) {
			// TODO: do another action ?
		}
		setInventory(player, null);
		player.closeInventory();
	}

	private static void exchange(BadblockPlayer player, CustomItemAction action, String actionData) {
		// TODO
	}

	private static void customEffect(BadblockPlayer player, CustomItemAction action, String actionData) {
		// TODO
	}

	public static void setInventory(BadblockPlayer player, String inventoryName) {
		UUID uniqueId = player.getUniqueId();
		playerInventories.put(uniqueId, inventoryName);
	}

	public static String getInventory(Player player) {
		UUID uniqueId = player.getUniqueId();
		return playerInventories.get(uniqueId);
	}

}
