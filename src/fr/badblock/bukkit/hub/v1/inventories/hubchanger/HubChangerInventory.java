package fr.badblock.bukkit.hub.v1.inventories.hubchanger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomUniqueInventory;
import fr.badblock.bukkit.hub.v1.rabbitmq.Hub;
import fr.badblock.bukkit.hub.v1.rabbitmq.HubPacketThread;
import fr.badblock.bukkit.hub.v1.rabbitmq.listeners.BungeeWorkerListener;
import fr.badblock.bukkit.hub.v1.rabbitmq.listeners.SEntryInfosListener;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.itemstack.ItemStackUtils;
import fr.badblock.gameapi.utils.threading.TaskManager;
import fr.badblock.permissions.PermissionManager;
import fr.badblock.sentry.FullSEntry;

public class HubChangerInventory extends CustomUniqueInventory {

	private HubChangerBackItem backItem;
	public static ConcurrentMap<BadblockPlayer, Inventory> inventoryPlayers = new ConcurrentHashMap<>();
	ItemStack air = new ItemStack(Material.AIR);

	private int i;
	private int nb;
	private long time;
	
	public HubChangerInventory() {
		// super("§6Changer de hub", 1);
		super("hub.items.hubchangerinventory", 1);

		backItem = new HubChangerBackItem();
		this.setItem(1, backItem);
		TaskManager.scheduleSyncRepeatingTask("hubChanger", new Runnable() {
			@Override
			public void run() {
				Iterator<Entry<BadblockPlayer, Inventory>> badblockPlayer = inventoryPlayers.entrySet().iterator();
				while (badblockPlayer.hasNext()) {
					Entry<BadblockPlayer, Inventory> entry = badblockPlayer.next();
					if (entry.getKey() == null || !entry.getKey().isOnline()) {
						badblockPlayer.remove();
						continue;
					}
					Inventory inventory = fill(entry.getKey(), entry.getValue());
					if (!inventory.equals(entry.getValue())) {
						inventoryPlayers.put(entry.getKey(), inventory);
						if (entry.getKey().getOpenInventory() != null && entry.getKey().getOpenInventory().getTopInventory() != null && entry.getKey().getOpenInventory().getTopInventory().getName().equals(inventory.getName()))
							entry.getKey().openInventory(inventory);
					}
				}
			}
		}, 20, 20);
	}

	@Override
	public void open(BadblockPlayer player) {
		Inventory inventory = Bukkit.createInventory(null, 9, player.getTranslatedMessage(this.getName())[0]);
		inventory = fill(player, inventory);
		player.openInventory(inventory);
		inventoryPlayers.put(player, inventory);
	}

	public Inventory fill(BadblockPlayer player, Inventory inventory) {
		ArrayList<Hub> choosenHubs = new ArrayList<>();
		Hub.getHubs().parallelStream().filter(hub -> hub != null && hub.isOnline()).forEach(hub -> choosenHubs.add(hub));
		Collections.sort(choosenHubs, new Comparator<Hub>() {
			@Override
			public int compare(Hub arg0, Hub arg1) {
				return (arg0 == null || arg1 == null) ? 0 : arg0.compare(arg0, arg1);
			}
		});
		int linesNeeded = 1 + (choosenHubs.size() / 9);
		setLines(linesNeeded);
		if (linesNeeded != inventory.getSize() / 9) {
			inventory = Bukkit.createInventory(null, linesNeeded * 9, player.getTranslatedMessage(this.getName())[0]);
		}
		int id = -1;
		FullSEntry sentry = SEntryInfosListener.sentries.get("login");
		if (sentry != null) {
			id++;
			System.out.println("Login > " + sentry.getIngamePLayers());
			if (time < System.currentTimeMillis()) {
				if (nb > sentry.getIngamePLayers() / BungeeWorkerListener.bungeeWorkers) {
					nb--;
					time = System.currentTimeMillis() + (500 + new Random().nextInt(6000));
				}else if (nb < sentry.getIngamePLayers() / BungeeWorkerListener.bungeeWorkers) {
					nb++;
					time = System.currentTimeMillis() + (500 + new Random().nextInt(6000));
				}
			}
			generate(inventory, player, true, nb, -1, id, null, "§6Serveur de connexion", null);
		}
		for (Hub hub : choosenHubs) {
			if (hub == null) continue;
			if (!hub.isOnline()) continue;
			id++;
			generate(inventory, player, hub.isOnline(), hub.getPlayers(), hub.getSlots(), id, hub, null, hub.getRanks());
		}
		for (int o = id + 1; o < (getLines() * 9) - 1; o++)
			if (inventory.getItem(o) == null || (inventory.getItem(o) != null && inventory.getItem(o).getType().equals(Material.AIR)))
				inventory.setItem(o, air);
		if (inventory.getItem((getLines() * 9) - 1) == null || (inventory.getItem((getLines() * 9) - 1) != null && !inventory.getItem((getLines() * 9) - 1).isSimilar(backItem.getStaticItem().get(player.getPlayerData().getLocale()))))
			inventory.setItem((getLines() * 9) - 1, backItem.getStaticItem().get(player.getPlayerData().getLocale()));
		return inventory;
	}
	
	@SuppressWarnings("deprecation")
	public void generate(Inventory inventory, BadblockPlayer player, boolean isOnline, int players, int slots, int id, Hub hudb, String display, Map<String, Integer> ranks) {
		Material material = Material.REDSTONE_BLOCK;
		byte data = 0;
		int amount = 1;
		ChatColor chatColor = ChatColor.DARK_RED;
		if (isOnline) {
			material = Material.STAINED_CLAY;
			if (players >= slots) {
				data = DyeColor.RED.getWoolData();
				chatColor = ChatColor.RED;
			} else if (players >= 80) {
				data = DyeColor.ORANGE.getWoolData();
				chatColor = ChatColor.GOLD;
			} else if (players >= 60) {
				data = DyeColor.YELLOW.getWoolData();
				chatColor = ChatColor.YELLOW;
			} else if (players >= 50) {
				data = DyeColor.BLUE.getWoolData();
				chatColor = ChatColor.BLUE;
			} else if (players >= 40) {
				data = DyeColor.CYAN.getWoolData();
				chatColor = ChatColor.AQUA;
			} else {
				data = DyeColor.LIME.getWoolData();
				chatColor = ChatColor.GREEN;
			}
			amount = id >= 64 ? 64 : id;
		}
		ItemStack itemStack = new ItemStack(material, amount, data);
		setMaxStackSize(itemStack, 70);
		//if (hub.getId() > 64)
		itemStack.setAmount(69);
		if (id == HubPacketThread.hubId) itemStack = ItemStackUtils.fakeEnchant(itemStack);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(chatColor + (hudb != null ? "Hub n°" + hudb.getId() : display));
		List<String> lore = new ArrayList<>();
		lore.add("");
		if (id == HubPacketThread.hubId) {
			lore.add(player.getTranslatedMessage("hub.changer.onlineonthis")[0]);
		}
		// §7Connectés:
		lore.add(player.getTranslatedMessage("hub.changer.onlines")[0] + "§b" + players + (slots >= 0 ? "/" + slots : ""));
		i = 0;
		Map<String, String> order = new HashMap<>();
		PermissionManager.getInstance().getGroups().stream().sorted((a, b) -> { return Integer.compare(b.getPower(), a.getPower()); }).forEach(group -> {
			String d = generateForId(i) + "";
			order.put(d, group.getName());
			i++;
		});
		if (ranks != null && !ranks.isEmpty()) {
			lore.add("");
			for (Entry<String, Integer> entry : ranks.entrySet()) {
				lore.add(player.getTranslatedMessage("permissions.tab." + order.get(entry.getKey()))[0].replace("null", "default") + "§f» §7" + entry.getValue());
			}
		}
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);
		if (hudb != null) hudb.setItemStack(itemStack);
		if (inventory.getItem(id) == null || (inventory.getItem(id) != null && !inventory.getItem(id).isSimilar(itemStack)))
			inventory.setItem(id, itemStack);
	}

	private char generateForId(int id){
		if (id == 0) return 'Z';
		int A = 'A';

		if(id > 26){
			A   = 'a';
			id -= 26;

			return (char) (A + id);
		} else {
			return (char) (A + id);
		}
	}

	public static ItemStack setMaxStackSize(ItemStack is, int amount){
		try {
			net.minecraft.server.v1_8_R3.ItemStack nmsIS = CraftItemStack.asNMSCopy(is);
			nmsIS.getItem().c(amount);
			return CraftItemStack.asBukkitCopy(nmsIS);
		} catch (Throwable t) { }

		return null;
	}

}
