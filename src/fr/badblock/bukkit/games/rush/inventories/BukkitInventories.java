package fr.badblock.bukkit.games.rush.inventories;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.badblock.bukkit.games.rush.PluginRush;
import fr.badblock.bukkit.games.rush.inventories.config.ItemLoader;
import fr.badblock.bukkit.games.rush.inventories.objects.CustomItemAction;
import fr.badblock.bukkit.games.rush.inventories.objects.InventoryActionManager;
import fr.badblock.bukkit.games.rush.inventories.objects.InventoryItemObject;
import fr.badblock.bukkit.games.rush.inventories.objects.InventoryObject;
import fr.badblock.bukkit.games.rush.inventories.utils.ChatColorUtils;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.general.Callback;	

public class BukkitInventories {

	//private static Map<InventoryObject, Map<Locale, Inventory>> staticInventories = new HashMap<>();

	public static void getInventory(BadblockPlayer player, String inventoryName, Callback<Inventory> callback) {
		InventoryObject inventoryObject = InventoriesLoader.getInventory(inventoryName);
		if (inventoryObject == null) {
			PluginRush.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unknown inventory '" + inventoryName + "'.");
			callback.done(null, null);
			return;
		}
		getInventory(player, inventoryObject, callback);
	}

	public static void getInventory(BadblockPlayer player, InventoryObject inventoryObject, Callback<Inventory> callback) {
		createInventory(player, inventoryObject, callback);
	}

	@SuppressWarnings("deprecation")
	private static void createInventory(BadblockPlayer player, InventoryObject inventoryObject, Callback<Inventory> callback)
	{
		if (inventoryObject == null)
		{
			callback.done(null, null);
			return;
		}

		if (player == null) 
		{
			callback.done(null, null);
			return;
		}

		int diamonds = 0;

		for (ItemStack is : player.getInventory().getContents())
		{
			if (is != null && Material.DIAMOND.equals(is.getType()))
			{
				diamonds += is.getAmount();
			}
		}

		int emeralds = 0;

		for (ItemStack is : player.getInventory().getContents())
		{
			if (is != null && Material.EMERALD.equals(is.getType()))
			{
				emeralds += is.getAmount();
			}
		}

		String name = ChatColorUtils.translate(inventoryObject.getName());
		Inventory inventory = Bukkit.createInventory(null, 9 * inventoryObject.getLines(), name);
		Map<String, String> replace = new HashMap<>();
		replace.put("%0", player.getName());
		replace.put("%1", Integer.toString(diamonds));
		replace.put("%2", Integer.toString(emeralds));
		for (InventoryItemObject inventoryItemObject : inventoryObject.getItems()) {
			String[] splitter = inventoryItemObject.getType().split(":");
			String material = splitter[0];
			short data = 0;
			if (splitter.length >= 2) data = Short.parseShort(splitter[1]);
			Material type = null;
			try {
				int o = Integer.parseInt(material);
				type = Material.getMaterial(o);
			}catch(Exception error) {
				type = Material.getMaterial(material);
			}
			ItemStack itemStack = new ItemStack(type, inventoryItemObject.getAmount(), data);

			if (!PluginRush.getInstance().getMapConfiguration().getAllowBows() && (itemStack.getType().equals(Material.BOW) || itemStack.getType().equals(Material.ARROW)))
			{
				continue;
			}

			BadblockTeam team = player.getTeam();
			if (team != null)
			{
				if (itemStack.getType().equals(Material.WOOL))
				{
					itemStack = new ItemStack(type, inventoryItemObject.getAmount(), team.getDyeColor().getWoolData());
				}
			}

			if (inventoryItemObject.isFakeEnchant()) itemStack = ItemLoader.fakeEnchant(itemStack);
			ItemMeta itemMeta = itemStack.getItemMeta();

			if (itemStack.getType().equals(Material.SKULL_ITEM)) {
				SkullMeta skullMeta = (SkullMeta) itemMeta;
				skullMeta.setOwner(player.getName());
			}
			if (inventoryItemObject.getName() != null && !inventoryItemObject.getName().isEmpty())
				itemMeta.setDisplayName(ChatColorUtils.translate(inventoryItemObject.getName().replace("%0", replace.get("%0")).replace("%1", replace.get("%1")).replace("%2", replace.get("%2"))));
			if (inventoryItemObject.getLore() != null && inventoryItemObject.getLore().length != 0)
				itemMeta.setLore(ChatColorUtils.getTranslatedMessages(inventoryItemObject.getLore(), replace));
			itemStack.setItemMeta(itemMeta);
			inventory.setItem(inventoryItemObject.getPlace(), itemStack);
		}

		callback.done(inventory, null);
	}

	public static void openInventory(BadblockPlayer player, String inventoryName) {
		InventoryObject inventoryObject = InventoriesLoader.getInventory(inventoryName);
		if (inventoryObject == null) {
			player.sendMessage(ChatColor.RED + "Unknown inventory with name '" + inventoryName + "'.");
			return;
		}

		InventoryActionManager.openInventory(player, CustomItemAction.OPEN_INV, inventoryName);
	}

}
