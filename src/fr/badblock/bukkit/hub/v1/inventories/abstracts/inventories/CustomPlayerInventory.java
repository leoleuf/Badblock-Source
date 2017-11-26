package fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories;

import java.util.Map;
import java.util.Map.Entry;

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
public abstract class CustomPlayerInventory extends CustomInventory {

	protected Inventory inventory;
	protected Map<Integer, CustomItem> items;
	private int lines;
	private BadblockPlayer player;

	private String name;

	public CustomPlayerInventory(String name) {
		this(name, 6);
	}

	public CustomPlayerInventory(String name, int lines) {
		super(name, lines);
	}

	public void run(BadblockPlayer player) {
		this.setPlayer(player);
		init(player);
	}

	public abstract void init(BadblockPlayer player);

	public void open() {
		player.closeInventory();
		HubPlayer.get(player).setCurrentInventory(this);
		Inventory inventory = Bukkit.createInventory(null, this.getLines() * 9,
				player.getTranslatedMessage(this.getName())[0]);
		for (Entry<Integer, CustomItem> entry : items.entrySet())
			inventory.setItem(entry.getKey(), entry.getValue().toItemStack(player));
		player.openInventory(inventory);
	}

	@Override
	public void setItem(CustomItem customItem, int... slots) {
		for (int slot : slots)
			this.getItems().put(slot, customItem);
	}

	@Override
	public void addItem(CustomItem customItem) {
		for (int i = 0; i < lines * 9; i++)
			if (items.get(i) == null) {
				this.getItems().put(i, customItem);
				return;
			}
	}

	@Override
	public void setItem(int slot, CustomItem customItem) {
		this.getItems().put(slot, customItem);
	}

	@Override
	public void setAsLastItem(CustomItem customItem) {
		this.getItems().put(9 * this.getLines() - 1, customItem);
	}

	@Override
	public void setNoFilledItem(CustomItem customItem) {
		for (int i = 0; i < lines * 9; i++)
			if (items.get(i) == null)
				this.getItems().put(i, customItem);
	}

	public static CustomPlayerInventory get(Class<? extends CustomPlayerInventory> clazz, BadblockPlayer player) {
		try {
			CustomPlayerInventory inventory = clazz.newInstance();
			inventory.run(player);
			return inventory;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static BadBlockHub main() {
		return BadBlockHub.getInstance();
	}

}
