package fr.badblock.bukkit.hub.v1.inventories.abstracts.items;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.i18n.Locale;
import fr.badblock.gameapi.utils.itemstack.ItemStackUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CustomItem {

	@Getter
	@Setter
	private static ConcurrentMap<CustomItem, List<ItemStack>> items = new ConcurrentHashMap<>();

	private int amount;
	private byte data;
	private String errorNeededPermission = "hub.items.permission";// "§cVous
	// n'avez
	// pas la
	// permission
	// d'utiliser
	// cet
	// item.";
	protected String lore;
	protected Material material;
	protected String name;
	protected String neededPermission;
	protected boolean fakeEnchantment;
	protected CustomItem noPermissionItem;
	protected Map<Locale, ItemStack> staticItem = new HashMap<>();

	public CustomItem(String name, Material material) {
		this(name, material, (byte) 0, 1, "", false);
	}
	
	public CustomItem(String name, Material material, boolean fakeEnchantment) {
		this(name, material, (byte) 0, 1, "", fakeEnchantment);
	}

	public CustomItem(String name, Material material, byte data, int amount, String lore, boolean fakeEnchantment) {
		this.setName(name);
		this.setMaterial(material);
		this.setData(data);
		this.setAmount(amount);
		this.setLore(lore);
		this.setFakeEnchantment(fakeEnchantment);
		for (Locale locale : Locale.values()) {
			ItemStack itemStack = this.toItemStack(locale);
			this.getStaticItem().put(locale, itemStack);
			List<ItemStack> list = new ArrayList<>();
			if (items.containsKey(this))
				list = items.get(this);
			list.add(itemStack);
			items.put(this, list);
		}
	}

	public CustomItem(String name, Material material, byte data, int amount, String lore) {
		this(name, material, data, amount, lore, false);
	}

	public CustomItem(String name, Material material, byte data, String lore) {
		this(name, material, data, 1, lore, false);
	}

	public CustomItem(String name, Material material, byte data, String lore, boolean fakeEnchantment) {
		this(name, material, data, 1, lore, fakeEnchantment);
	}

	public CustomItem(String name, Material material, byte data, boolean fakeEnchantment) {
		this(name, material, data, 1, "", fakeEnchantment);
	}
	
	public CustomItem(String name, Material material, byte data) {
		this(name, material, data, 1, "", false);
	}

	public CustomItem(String name, Material material, int amount, String lore, boolean fakeEnchantment) {
		this(name, material, (byte) 0, amount, lore, fakeEnchantment);
	}

	public CustomItem(String name, Material material, int amount, String lore) {
		this(name, material, (byte) 0, amount, lore, false);
	}

	public CustomItem(String name, Material material, String lore, boolean fakeEnchantment) {
		this(name, material, (byte) 0, 1, lore, fakeEnchantment);
	}

	public CustomItem(String name, Material material, String lore) {
		this(name, material, (byte) 0, 1, lore, false);
	}

	public ItemStack build(Material material, int amount, byte data, String name, String... lore) {
		ItemStack itemStack = new ItemStack(material, amount, data);
		if (this.isFakeEnchantment()) {
			itemStack = ItemStackUtils.fakeEnchant(itemStack);
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(name);
		if (lore != null && lore.length > 0)
			itemMeta.setLore(Arrays.asList(lore));
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	public abstract List<ItemAction> getActions();

	public boolean isSame(BadblockPlayer player, ItemStack itemStack) {
		return itemStack.isSimilar(this.getStaticItem().get(player.getPlayerData().getLocale()));
	}

	public abstract void onClick(BadblockPlayer player, ItemAction action, Block clickedBlock);

	public ItemStack toItemStack(BadblockPlayer player) {
		if (this.getNeededPermission() != null && !player.hasPermission(this.getNeededPermission()))
			if (this.getNoPermissionItem() != null)
				return this.getNoPermissionItem().getStaticItem().get(player.getPlayerData().getLocale());
		return this.getStaticItem().get(player.getPlayerData().getLocale());
	}

	public ItemStack toItemStack(Locale locale) {
		if (locale == null)
			return null;
		if (material == null || material == Material.AIR)
			material = Material.STONE;
		ItemStack itemStack = new ItemStack(material, amount, data);
		if (this.isFakeEnchantment()) {
			itemStack = ItemStackUtils.fakeEnchant(itemStack);
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (itemMeta == null)
			return null;
		itemMeta.setDisplayName(GameAPI.i18n().get(locale, name)[0]);
		if (lore != null && !lore.isEmpty())
			itemMeta.setLore(Arrays.asList(GameAPI.i18n().get(locale, lore)));
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	public static BadBlockHub main() {
		return BadBlockHub.getInstance();
	}

}
