package fr.badblock.common.shoplinker.bukkit.inventories;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import fr.badblock.common.shoplinker.bukkit.ShopLinker;
import fr.badblock.common.shoplinker.bukkit.inventories.config.ItemLoader;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryItemObject;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryObject;
import fr.badblock.common.shoplinker.bukkit.inventories.utils.ChatColorUtils;
import fr.badblock.common.shoplinker.bukkit.permissions.AbstractPermissions;	

public class BukkitInventories {

	//private static Map<InventoryObject, Map<Locale, Inventory>> staticInventories = new HashMap<>();
	
	public static Inventory getInventory(Player player, String inventoryName) {
		InventoryObject inventoryObject = InventoriesLoader.getInventory(inventoryName);
		if (inventoryObject == null) {
			ShopLinker.getConsole().sendMessage(ChatColor.RED + "Unknown inventory '" + inventoryName + "'.");
			return null;
		}
		return getInventory(player, inventoryObject);
	}
	
	public static Inventory getInventory(Player player, InventoryObject inventoryObject) {
		return createInventory(player, inventoryObject);
	}
	
	@SuppressWarnings("deprecation")
	private static Inventory createInventory(Player player, InventoryObject inventoryObject) {
		if (inventoryObject == null) return null;
		if (player == null) return null;
		String name = ChatColorUtils.translate(inventoryObject.getName());
		Inventory inventory = Bukkit.createInventory(null, 9 * inventoryObject.getLines(), name);
		for (InventoryItemObject inventoryItemObject : inventoryObject.getItems()) {
			String[] splitter = inventoryItemObject.getType().split(":");
			String material = splitter[0];
			byte data = 0;
			if (splitter.length >= 2) data = Byte.parseByte(splitter[1]);
			Material type = null;
			try {
				int o = Integer.parseInt(material);
				type = Material.getMaterial(o);
			}catch(Exception error) {
				type = Material.getMaterial(material);
			}
			ItemStack itemStack = new ItemStack(type, inventoryItemObject.getAmount(), data);
			if (inventoryItemObject.isFakeEnchant()) itemStack = ItemLoader.fakeEnchant(itemStack);
			ItemMeta itemMeta = itemStack.getItemMeta();
			if (itemStack.getType().equals(Material.SKULL_ITEM)) {
				SkullMeta skullMeta = (SkullMeta) itemMeta;
				skullMeta.setOwner(player.getName());
			}
			Map<String, String> replace = new HashMap<>();
			replace.put("%0", player.getName());
			replace.put("%1", AbstractPermissions.getPermissions().getPrefix(player.getName()));
			replace.put("%2", "Quatre vingt douze milliards");
			if (inventoryItemObject.getName() != null && !inventoryItemObject.getName().isEmpty())
				itemMeta.setDisplayName(ChatColorUtils.translate(inventoryItemObject.getName().replace("%0", replace.get("%0")).replace("%1", replace.get("%1")).replace("%2", replace.get("%2"))));
			if (inventoryItemObject.getLore() != null && inventoryItemObject.getLore().length != 0)
				itemMeta.setLore(ChatColorUtils.getTranslatedMessages(inventoryItemObject.getLore(), replace));
			itemStack.setItemMeta(itemMeta);
			inventory.setItem(inventoryItemObject.getPlace(), itemStack);
		}
		return inventory;
	}
	
}
