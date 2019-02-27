package fr.badblock.bukkit.games.pvpbox.inventories;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.badblock.bukkit.games.pvpbox.PvPBox;
import fr.badblock.bukkit.games.pvpbox.inventories.config.ItemLoader;
import fr.badblock.bukkit.games.pvpbox.inventories.objects.CustomItemAction;
import fr.badblock.bukkit.games.pvpbox.inventories.objects.InventoryActionManager;
import fr.badblock.bukkit.games.pvpbox.inventories.objects.InventoryItemObject;
import fr.badblock.bukkit.games.pvpbox.inventories.objects.InventoryObject;
import fr.badblock.bukkit.games.pvpbox.inventories.utils.ChatColorUtils;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;	

public class BukkitInventories {

	//private static Map<InventoryObject, Map<Locale, Inventory>> staticInventories = new HashMap<>();

	public static Inventory getInventory(BadblockPlayer player, String inventoryName) {
		InventoryObject inventoryObject = InventoriesLoader.getInventory(inventoryName);
		
		if (inventoryObject == null)
		{
			PvPBox.getInstance().getServer().getConsoleSender().sendMessage(ChatColor.RED + "Unknown inventory '" + inventoryName + "'.");
			return null;
		}
		
		return getInventory(player, inventoryObject);
	}

	@SuppressWarnings("deprecation")
	private static Inventory createInventory(BadblockPlayer player, InventoryObject inventoryObject) {
		if (inventoryObject == null)
		{
			return null;
		}
		
		if (player == null) 
		{
			return null;
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

		return inventory;
	}

	public static void openInventory(BadblockPlayer player, String inventoryName) {
		InventoryObject inventoryObject = InventoriesLoader.getInventory(inventoryName);
		if (inventoryObject == null) {
			player.sendMessage(ChatColor.RED + "Unknown inventory with name '" + inventoryName + "'.");
			return;
		}

		InventoryActionManager.openInventory(player, CustomItemAction.OPEN_INV, inventoryName);
	}


	public static Inventory getInventory(BadblockPlayer player, InventoryObject inventoryObject)
	{
		return createInventory(player, inventoryObject);
	}

	public static Inventory getDefaultInventory(BadblockPlayer player)
	{
		return getInventory(player, "default");
	}

	public static void giveDefaultInventory(BadblockPlayer player)
	{
		player.clearInventory();
		Inventory defaultInventory = getDefaultInventory(player);
		if (defaultInventory == null)
		{
			GameAPI.logError("§cUnknown default inventory.");
			return;
		}
		int i = 0;
		for (ItemStack itemStack : defaultInventory.getContents())
		{
			if (itemStack != null && itemStack.getType() != Material.AIR && itemStack.getType() != null)
			{
				player.getInventory().setItem(i, itemStack);
			}
			i++;
		}
	}
	
}
