package fr.badblock.common.shoplinker.bukkit.inventories;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import fr.badblock.common.shoplinker.bukkit.ShopLinker;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.CustomItemAction;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryAction;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryActionType;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryItemObject;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryObject;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryShopObject;
import fr.badblock.common.shoplinker.bukkit.inventories.server.CategoryShop;
import fr.badblock.common.shoplinker.bukkit.inventories.server.ServerShop;
import fr.badblock.common.shoplinker.bukkit.inventories.server.ShopProduct;
import fr.badblock.common.shoplinker.bukkit.inventories.utils.ChatColorUtils;
import fr.badblock.common.shoplinker.bukkit.inventories.utils.JsonFile;
import fr.badblock.common.shoplinker.mongodb.MongoService;
import lombok.Getter;

public class InventoriesLoader {

	@Getter private static Map<String, InventoryObject> inventories = new HashMap<>();

	public static void loadInventories(Plugin plugin)
	{
		new Thread("shoplinkerLoader")
		{
			@Override
			public void run()
			{
				List<ServerShop> servers = new ArrayList<>();

				MongoService mongo = ShopLinker.getInstance().getMongoService();
				DB db = mongo.getDb();
				DBCollection collection = db.getCollection("server_list");

				BasicDBObject query = new BasicDBObject();

				DBCursor cursor = collection.find(query);
				while (cursor.hasNext())
				{
					servers.add(new ServerShop((BasicDBObject) cursor.next()));
				}

				System.out.println(servers.size());

				Collections.sort(servers, new Comparator<ServerShop>() {
					@Override
					public int compare(ServerShop o1, ServerShop o2) {
						return Integer.compare(o1.getPower(), o2.getPower());
					}
				});

				for (ServerShop server : servers)
				{
					String name = ChatColor.YELLOW + server.getName();
					List<InventoryItemObject> itemList = new ArrayList<>();
					List<InventoryItemObject> baseItemList = new ArrayList<>();

					int[] i = new int[] { 0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53};
					for (int o : i)
					{
						InventoryAction[] inventoryAction = new InventoryAction[2];
						inventoryAction[0] = new InventoryAction(InventoryActionType.LEFT_CLICK, CustomItemAction.NOTHING, "_", null);
						inventoryAction[1] = new InventoryAction(InventoryActionType.RIGHT_CLICK, CustomItemAction.NOTHING, "_", null);
						InventoryItemObject glass = new InventoryItemObject(" ", new String[] {" "}, o, 1, "160:3", inventoryAction, false);
						itemList.add(glass);
						baseItemList.add(glass);
					}
					
					int[] bv = new int[] { 7 };
					for (int o : bv)
					{
						InventoryAction[] inventoryAction = new InventoryAction[2];
						inventoryAction[0] = new InventoryAction(InventoryActionType.LEFT_CLICK, CustomItemAction.NOTHING, "_", null);
						inventoryAction[1] = new InventoryAction(InventoryActionType.RIGHT_CLICK, CustomItemAction.NOTHING, "_", null);
						InventoryItemObject glass = new InventoryItemObject(" ", new String[] {" "}, o, 1, "160:8", inventoryAction, false);
						itemList.add(glass);
						baseItemList.add(glass);
					}
					
					int[] tv = new int[] { 16 };
					for (int o : tv)
					{
						InventoryAction[] inventoryAction = new InventoryAction[2];
						inventoryAction[0] = new InventoryAction(InventoryActionType.LEFT_CLICK, CustomItemAction.NOTHING, "_", null);
						inventoryAction[1] = new InventoryAction(InventoryActionType.RIGHT_CLICK, CustomItemAction.NOTHING, "_", null);
						InventoryItemObject glass = new InventoryItemObject(" ", new String[] {" "}, o, 1, "160:7", inventoryAction, false);
						itemList.add(glass);
						baseItemList.add(glass);
					}

					String[] lore = new String[11];
					
					lore[0] = "&7Points Boutiques : &b%2";
					lore[1] = "";
					lore[2] = "&6Pour recharger ton compte";
					lore[3] = "&6envoie par SMS §bCODE §aau §d83303";
					lore[4] = "";
					lore[5] = "&d1250 points boutique (4,5 €)";
					lore[6] = "&aSaisis le code reçu par SMS en faisant";
					lore[7] = "&d/code &asuivi du code reçu !";
					lore[8] = "";
					lore[9] = "&cPour recharger +, recharge sur le";
					lore[10] = "&csite Internet &d(https://store.badblock.fr)";

					InventoryAction[] iva = new InventoryAction[2];
					iva[0] = new InventoryAction(InventoryActionType.LEFT_CLICK, CustomItemAction.NOTHING, "_", null);
					iva[1] = new InventoryAction(InventoryActionType.RIGHT_CLICK, CustomItemAction.NOTHING, "_", null);
					InventoryItemObject info = new InventoryItemObject("%1%0", lore, 1, 1, "386:0", iva, false);
					itemList.add(info);
					baseItemList.add(info);
					
					InventoryAction[] ivpa = new InventoryAction[2];
					ivpa[0] = new InventoryAction(InventoryActionType.LEFT_CLICK, CustomItemAction.NOTHING, "_", null);
					ivpa[1] = new InventoryAction(InventoryActionType.RIGHT_CLICK, CustomItemAction.NOTHING, "_", null);
					InventoryItemObject glass2 = new InventoryItemObject("", new String[] {}, 10, 1, "160:7", ivpa, false);
					itemList.add(glass2);
					baseItemList.add(glass2);
					
					int index = 1;
					for (ServerShop s : servers)
					{
						index++;
						String[] stockArr = new String[s.toItemStack().getItemMeta().getLore().size()];
						stockArr = s.toItemStack().getItemMeta().getLore().toArray(stockArr);
						@SuppressWarnings("deprecation")
						String type = s.toItemStack().getType().getId() + ":" + s.toItemStack().getData().getData();
						InventoryAction[] inventoryAction = new InventoryAction[2];
						inventoryAction[0] = new InventoryAction(InventoryActionType.LEFT_CLICK, CustomItemAction.OPEN_INV, s.get_id(), null);
						inventoryAction[1] = new InventoryAction(InventoryActionType.RIGHT_CLICK, CustomItemAction.OPEN_INV, s.get_id(), null);

						boolean same = s.equals(server); // same server

						InventoryItemObject serverItem = new InventoryItemObject(ChatColor.YELLOW + s.getName(), stockArr, index, 1, type, inventoryAction, same);
						itemList.add(serverItem);
						baseItemList.add(serverItem);

						int p = index + 9;

						InventoryAction[] ivp = new InventoryAction[2];
						ivp[0] = new InventoryAction(InventoryActionType.LEFT_CLICK, CustomItemAction.NOTHING, "_", null);
						ivp[1] = new InventoryAction(InventoryActionType.RIGHT_CLICK, CustomItemAction.NOTHING, "_", null);
						InventoryItemObject glass = new InventoryItemObject("", new String[] {}, p, 1, "160:" + (same ? "14" : "7"), ivp, false);
						itemList.add(glass);
						baseItemList.add(glass);
					}

					index = 18;
					for (CategoryShop category : server.getCategories())
					{
						index++;
						if (index > 25)
						{
							break;
						}
						
						List<InventoryItemObject> catItemList = new ArrayList<>(baseItemList);
						String[] stockArr = new String[category.toItemStack().getItemMeta().getLore().size()];
						stockArr = category.toItemStack().getItemMeta().getLore().toArray(stockArr);
						@SuppressWarnings("deprecation")
						String type = category.toItemStack().getType().getId() + ":" + category.toItemStack().getData().getData();
						InventoryAction[] inventoryAction = new InventoryAction[2];
						inventoryAction[0] = new InventoryAction(InventoryActionType.LEFT_CLICK, CustomItemAction.OPEN_INV, category.getId(), null);
						inventoryAction[1] = new InventoryAction(InventoryActionType.RIGHT_CLICK, CustomItemAction.OPEN_INV, category.getId(), null);

						InventoryItemObject cat = new InventoryItemObject(ChatColor.GREEN + category.getName(), stockArr, index, 1, type, inventoryAction, false);

						itemList.add(cat);
						
						List<Integer> availableSlots = new ArrayList<>();
						for (int b = 0; b < 54; b++)
						{
							final int fb = b;
							long c = catItemList.stream().filter(it -> it.getPlace() == fb).count();
							
							if (c > 0)
							{
								continue;
							}
							
							availableSlots.add(b);
						}
						
						for (ShopProduct product : category.getProducts())
						{
							Iterator<Integer> iterator = availableSlots.iterator();
							if (!iterator.hasNext())
							{
								continue;
							}
							
							int slot = iterator.next();
							iterator.remove();

							String[] loreArr = new String[product.toItemStack().getItemMeta().getLore().size()];
							loreArr = product.toItemStack().getItemMeta().getLore().toArray(loreArr);
							@SuppressWarnings("deprecation")
							String productType = product.toItemStack().getType().getId() + ":" + product.toItemStack().getData().getData();
							InventoryAction[] productActions = new InventoryAction[2];
							productActions[0] = new InventoryAction(InventoryActionType.LEFT_CLICK, CustomItemAction.BUY_COMMAND, "_", new InventoryShopObject(product.getId()));
							productActions[1] = new InventoryAction(InventoryActionType.RIGHT_CLICK, CustomItemAction.BUY_COMMAND, "_", new InventoryShopObject(product.getId()));

							InventoryItemObject productItem = new InventoryItemObject(ChatColor.GOLD + product.getName(), loreArr, slot, 1, productType, productActions, false);
							catItemList.add(productItem);
						}
						
						ShopLinker.getConsole().sendMessage("Loaded inventory configuration: '" + category.getId() + "'");

						InventoryItemObject[] itemArr = new InventoryItemObject[catItemList.size()];
						itemArr = catItemList.toArray(itemArr);
						InventoryObject categoryObject = new InventoryObject(category.getName(), "", 6, itemArr);
						inventories.put(category.getId(), categoryObject);
					}

					InventoryItemObject[] itemArr = new InventoryItemObject[itemList.size()];
					itemArr = itemList.toArray(itemArr);

					InventoryObject object = new InventoryObject(name, "", 6, itemArr);
					inventories.put(server.get_id(), object);
					ShopLinker.getConsole().sendMessage("Loaded inventory configuration: '" + server.get_id() + "'");
				}

				ShopLinker.getConsole().sendMessage("Everything is loaded!");
			}
		}.start();

		File pluginFolder = plugin.getDataFolder();
		// Gestion des dossiers d'inventaires
		File inventoriesFolder = new File(pluginFolder, "inventories");
		if (!inventoriesFolder.exists())
			inventoriesFolder.mkdirs();
		File[] inventoryFiles = inventoriesFolder.listFiles(File::isFile);
		if (inventoryFiles == null) return;
		Arrays.asList(inventoryFiles).forEach(file -> {
			String name = FilenameUtils.removeExtension(file.getName());
			InventoryObject inventoryObject = JsonFile.getFile(file, InventoryObject.class);
			for (InventoryItemObject item : inventoryObject.getItems()) {
				if (item.getName() == null || item.getName().isEmpty()) continue;
				item.setName(ChatColorUtils.translate(item.getName()));
			}
			inventories.put(name, inventoryObject);
			ShopLinker.getConsole().sendMessage("Loaded inventory configuration: '" + name + "'");
		});
	}
	
	private static final Pattern REMOVE_TAGS = Pattern.compile("<.+?>");

	public static String removeTags(String string) {
	    if (string == null || string.length() == 0) {
	        return string;
	    }

	    Matcher m = REMOVE_TAGS.matcher(string);
	    return m.replaceAll("");
	}
	
	public static String[] splitByNumber(String s, int size) {
	    if(s == null || size <= 0)
	        return null;
	    int chunks = s.length() / size + ((s.length() % size > 0) ? 1 : 0);
	    String[] arr = new String[chunks];
	    for(int i = 0, j = 0, l = s.length(); i < l; i += size, j++)
	        arr[j] = s.substring(i, Math.min(l, i + size));
	    return arr;
	}
	
	public static Material getFrom(String raw)
	{
		if (raw == null || raw.isEmpty())
		{
			return Material.STONE;
		}

		for (Material material : Material.values())
		{
			if (material.name().equalsIgnoreCase(raw))
			{
				return material;
			}
		}

		return Material.STONE;
	}

	public static void reloadInventories(Plugin plugin) {
		getInventories().clear();
		loadInventories(plugin);
	}

	public static InventoryObject getInventory(String name) {
		return inventories.get(name);
	}

}
