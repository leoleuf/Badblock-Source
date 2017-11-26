package fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CustomInventory {

	protected static Map<String, CustomInventory> customInventories = new HashMap<>();

	public static CustomInventory get(Class<? extends CustomInventory> clazz) {
		try {
			return !customInventories.containsKey(clazz.getSimpleName()) ? clazz.newInstance()
					: customInventories.get(clazz.getSimpleName());
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected Map<Integer, CustomItem> items;
	protected Inventory inventory;
	private int lines;

	private String name;

	public CustomInventory(String name) {
		this(name, 6);
	}

	public CustomInventory(String name, int lines) {
		this.setName(name);
		this.setLines(lines);
		this.setItems(new ConcurrentHashMap<>());
		customInventories.put(this.getClass().getSimpleName(), this);
	}

	public void open(BadblockPlayer player) {
		HubPlayer.get(player).setCurrentInventory(this);
		
		Inventory inventory = Bukkit.createInventory(null, this.getLines() * 9, player.getTranslatedMessage(this.getName())[0]);
		
		for (Entry<Integer, CustomItem> entry : items.entrySet())
			inventory.setItem(entry.getKey(), entry.getValue().toItemStack(player));
		
		player.openInventory(inventory);
	}

	public void setItem(CustomItem customItem, int... slots) {
		for (int slot : slots)
			this.getItems().put(slot, customItem);
	}

	public void addItem(CustomItem customItem) {
		for (int i = 0; i < lines * 9; i++)
			if (items.get(i) == null) {
				this.getItems().put(i, customItem);
				return;
			}
	}

	public void setItem(int slot, CustomItem customItem) {
		this.getItems().put(slot, customItem);
	}

	public void setAsLastItem(CustomItem customItem) {
		this.setAsLastItem(customItem, 0);
	}

	public void setAsLastItem(CustomItem customItem, int i) {
		this.getItems().put(9 * this.getLines() - (i + 1), customItem);
	}

	public void setNoFilledItem(CustomItem customItem) {
		for (int i = 0; i < lines * 9; i++)
			if (items.get(i) == null)
				this.getItems().put(i, customItem);
	}

	public static BadBlockHub main() {
		return BadBlockHub.getInstance();
	}

}
