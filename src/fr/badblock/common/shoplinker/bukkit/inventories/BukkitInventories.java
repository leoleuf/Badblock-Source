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

import fr.badblock.api.common.utils.general.MathUtils;
import fr.badblock.common.shoplinker.api.objects.TempBuyObject;
import fr.badblock.common.shoplinker.bukkit.BuyManager;
import fr.badblock.common.shoplinker.bukkit.ShopLinkWorker;
import fr.badblock.common.shoplinker.bukkit.ShopLinker;
import fr.badblock.common.shoplinker.bukkit.inventories.config.ItemLoader;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.CustomItemAction;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryAction;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryActionManager;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryItemObject;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryObject;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryShopObject;
import fr.badblock.common.shoplinker.bukkit.inventories.utils.ChatColorUtils;
import fr.badblock.common.shoplinker.bukkit.permissions.AbstractPermissions;
import fr.badblock.common.shoplinker.bukkit.players.ShopPlayer;
import fr.badblock.common.shoplinker.bukkit.utils.Callback;	

public class BukkitInventories {

	//private static Map<InventoryObject, Map<Locale, Inventory>> staticInventories = new HashMap<>();

	public static void getInventory(Player player, String inventoryName, Callback<Inventory> callback) {
		InventoryObject inventoryObject = InventoriesLoader.getInventory(inventoryName);
		if (inventoryObject == null) {
			ShopLinker.getConsole().sendMessage(ChatColor.RED + "Unknown inventory '" + inventoryName + "'.");
			callback.done(null, null);
			return;
		}
		getInventory(player, inventoryObject, callback);
	}

	public static void getInventory(Player player, InventoryObject inventoryObject, Callback<Inventory> callback) {
		createInventory(player, inventoryObject, callback);
	}

	public static void createConfirmInventory(Player player, CustomItemAction action, InventoryShopObject shopObject, InventoryItemObject inventoryItemObject,
			ItemStack itemOffer)
	{
		ShopPlayer shopPlayer = ShopPlayer.get(player);
		shopPlayer.setBuy(new TempBuyObject(action, shopObject, inventoryItemObject, itemOffer));
		ShopLinker shopLinker = ShopLinker.getInstance();
		Inventory inventory = Bukkit.createInventory(null, 9, shopLinker.getConfirmInventoryName().replace("%0", inventoryItemObject.getName()));
		BuyManager.getAsyncNeededCoins(shopObject.getOfferId(), new Callback<Double>()
		{
			@Override
			public void done(Double integer, Throwable throwable)
			{
				// 0 Retour
				ItemStack itemStack = new ItemStack(Material.WOOD_DOOR);
				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.setDisplayName(ChatColorUtils.translate(shopLinker.getBackName()));
				itemMeta.setLore(ChatColorUtils.getTranslatedMessages(shopLinker.getBackLore()));
				itemStack.setItemMeta(itemMeta);
				inventory.setItem(0, itemStack);

				// 4 : item offer
				inventory.setItem(4, itemOffer);

				// Format

				// Replace
				Map<String, String> replace = new HashMap<>();
				replace.put("%0", ChatColorUtils.translate(inventoryItemObject.getName()));
				replace.put("%1", Double.toString(integer.doubleValue()));

				// 7 : redstone no
				itemStack = new ItemStack(Material.REDSTONE_BLOCK);
				itemMeta = itemStack.getItemMeta();
				itemMeta.setDisplayName(ChatColorUtils.getTranslatedMessages(new String[] { shopLinker.getCancelName() }, replace).get(0));

				String[] stockArr = shopLinker.getCancelLore().toArray(new String[shopLinker.getCancelLore().size()]);
				itemMeta.setLore(ChatColorUtils.getTranslatedMessages(stockArr, replace));

				itemStack.setItemMeta(itemMeta);
				inventory.setItem(7, itemStack);

				// 7 : emerald yes
				itemStack = new ItemStack(Material.EMERALD_BLOCK);
				itemMeta = itemStack.getItemMeta();
				itemMeta.setDisplayName(ChatColorUtils.getTranslatedMessages(new String[] { shopLinker.getConfirmName() }, replace).get(0));

				// lore
				stockArr = shopLinker.getConfirmLore().toArray(new String[shopLinker.getConfirmLore().size()]);
				itemMeta.setLore(ChatColorUtils.getTranslatedMessages(stockArr, replace));

				itemStack.setItemMeta(itemMeta);
				inventory.setItem(8, itemStack);

				// Open inventory
				Bukkit.getScheduler().runTask(ShopLinker.getInstance(), new Runnable()
				{
					@Override
					public void run()
					{
						player.openInventory(inventory);
					}
				});
			}
		});
	}

	@SuppressWarnings("deprecation")
	private static void createInventory(Player player, InventoryObject inventoryObject, Callback<Inventory> callback) {
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
		String name = ChatColorUtils.translate(inventoryObject.getName());
		Inventory inventory = Bukkit.createInventory(null, 9 * inventoryObject.getLines(), name);
		Map<String, String> replace = new HashMap<>();
		replace.put("%0", player.getName());
		replace.put("%1", AbstractPermissions.getPermissions().getPrefix(player.getName()));

		ShopLinkWorker.getShopPoints(player.getUniqueId(), new Callback<Double>()
		{
			@Override
			public void done(Double shopPoints, Throwable throwable)
			{
				if (shopPoints != null && shopPoints > 0)
				{
					replace.put("%2", Double.toString(MathUtils.round(shopPoints, 2)));
				}
				else
				{
					replace.put("%2", "0 points");
				}
				for (InventoryItemObject inventoryItemObject : inventoryObject.getItems()) {
					for (InventoryAction inventoryAction : inventoryItemObject.getActions())
					{
						InventoryShopObject shopData = inventoryAction.getShopData();
						double price = shopData != null ? BuyManager.getNeededCoins(shopData.getOfferId()) : -1;
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
						replace.put("%3", Double.toString(price));
						if (inventoryItemObject.getName() != null && !inventoryItemObject.getName().isEmpty())
							itemMeta.setDisplayName(ChatColorUtils.translate(inventoryItemObject.getName().replace("%0", replace.get("%0")).replace("%1", replace.get("%1")).replace("%2", replace.get("%2"))));
						if (inventoryItemObject.getLore() != null && inventoryItemObject.getLore().length != 0)
							itemMeta.setLore(ChatColorUtils.getTranslatedMessages(inventoryItemObject.getLore(), replace));
						itemStack.setItemMeta(itemMeta);
						inventory.setItem(inventoryItemObject.getPlace(), itemStack);
					}
				}
				callback.done(inventory, null);
			}
		});
	}

	public static void openInventory(Player player, String inventoryName) {
		InventoryObject inventoryObject = InventoriesLoader.getInventory(inventoryName);
		if (inventoryObject == null) {
			player.sendMessage(ChatColor.RED + "[ShopLinker] Unknown inventory with name '" + inventoryName + "'.");
			return;
		}
		String permission = inventoryObject.getPermission();
		if (permission != null && !permission.isEmpty()) {
			if (!player.hasPermission(permission)) {
				String messageKey = "messages.nopermission." + inventoryName; 
				String message = ChatColorUtils.translate(ShopLinker.getInstance().getConfig().getString(messageKey));
				if (message == null || message.isEmpty()) player.sendMessage(ChatColor.RED + messageKey);
				return;
			}
		}
		InventoryActionManager.openInventory(player, CustomItemAction.OPEN_INV, inventoryName);
	}

}
