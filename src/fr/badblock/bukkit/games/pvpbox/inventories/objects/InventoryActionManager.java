package fr.badblock.bukkit.games.pvpbox.inventories.objects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import fr.badblock.bukkit.games.pvpbox.PvPBox;
import fr.badblock.bukkit.games.pvpbox.inventories.BukkitInventories;
import fr.badblock.bukkit.games.pvpbox.kits.Kit;
import fr.badblock.bukkit.games.pvpbox.kits.KitManager;
import fr.badblock.bukkit.games.pvpbox.players.BoxPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.ConfigUtils;

public class InventoryActionManager
{

	private static Map<UUID, String> playerInventories = new HashMap<>();
	private static Map<UUID, TempInventoryObject> playerInv = new HashMap<>();

	public static void handle(BadblockPlayer player, String inventoryName, InventoryItemObject object, InventoryActionType type) {
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
			case TELEPORT:
				teleport(player, action, actionData);
				break;
			case OPEN_INV:
				openInventory(player, action, actionData);
				break;
			case CLOSE_INV:
				closeInventory(player, action, actionData);
				break;
			case SELECT_KIT:
				selectKit(player, action, actionData);
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
		Inventory inventory = BukkitInventories.getInventory(player, inventoryName);

		if (inventory == null)
		{
			closeInventory(player, action, null);
			return;
		}

		Bukkit.getScheduler().runTask(PvPBox.getInstance(), new Runnable()
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

	private static void closeInventory(BadblockPlayer player, CustomItemAction action, String actionData) {
		if (actionData != null && !actionData.isEmpty()) {
			// TODO: do another action ?
		}
		setInventory(player, null);
		player.closeInventory();
	}

	private static void teleport(BadblockPlayer player, CustomItemAction action, String actionData)
	{
		Location location = ConfigUtils.convertStringToLocation(actionData);
		player.teleport(location);
	}	

	private static void selectKit(BadblockPlayer player, CustomItemAction action, String kitName)
	{
		KitManager kitManager = KitManager.getInstance();

		BoxPlayer boxPlayer = BoxPlayer.get(player);

		if (boxPlayer == null)
		{
			return;
		}

		if (!kitManager.exists(kitName))
		{
			player.sendTranslatedMessage("pvpbox.unknownkit", kitName);
			player.closeInventory();
			return;
		}

		Kit kit = kitManager.getKit(kitName);

		if (!kit.canUse(player))
		{
			player.sendTranslatedMessage("pvpbox.cantuse", player.getTranslatedMessage("pvpbox.kits." + kitName)[0]);
			player.closeInventory();
			return;
		}

		boxPlayer.setKit(kit);
		player.sendTranslatedMessage("pvpbox.selected", player.getTranslatedMessage("pvpbox.kits." + kitName)[0]);
		
		player.closeInventory();
	}

	public static void setInventory(BadblockPlayer player, String inventoryName) {
		UUID uniqueId = player.getUniqueId();
		playerInventories.put(uniqueId, inventoryName);

		BoxPlayer boxPlayer = BoxPlayer.get(player);

		if (boxPlayer == null)
		{
			return;
		}

		boxPlayer.setInventory(inventoryName);
	}

	public static String getInventory(Player player) {
		UUID uniqueId = player.getUniqueId();
		return playerInventories.get(uniqueId);
	}

}
