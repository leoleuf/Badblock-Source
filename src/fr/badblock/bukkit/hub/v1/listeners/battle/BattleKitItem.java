package fr.badblock.bukkit.hub.v1.listeners.battle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import lombok.Data;
import net.md_5.bungee.api.ChatColor;

@Data public class BattleKitItem {
	
	public int				 		 slot;
	public int						 amount;
	public boolean					 splash;
	public Material			 		 material;
	public short 			 		 data;
	public String			 		 name;
	public List<String>		 		 lore;
	public Map<Enchantment, Integer> enchantments;
	public ItemStack		 		 itemStack;
	
	public BattleKitItem(ConfigurationSection item) {
		slot = item.getInt("slot");
		splash = item.getBoolean("splash", false);
		amount = item.getInt("amount", 1);
		material = Material.getMaterial(item.getString("material"));
		data = (short) item.getInt("data");
		if (name != null && !"".equalsIgnoreCase(name)) {
			name = (String) item.getString("name");
			name = ChatColor.translateAlternateColorCodes('&', name);
		}
		lore = (List<String>) item.getStringList("lore");
		lore = toColorList(lore);
		enchantments = new HashMap<>();
		item.getStringList("enchantments").forEach(string -> {
			String[] splitter = string.split(":");
			Enchantment enchantment = Enchantment.getByName(splitter[0]);
			int booster = Integer.parseInt(splitter[1]);
			enchantments.put(enchantment, booster);
		}); 
		itemStack = new ItemStack(material, amount, data);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(name);
		itemMeta.setLore(lore);
		enchantments.entrySet().forEach(enchantment -> itemStack.addUnsafeEnchantment(enchantment.getKey(), enchantment.getValue()));
	}
	
	@SuppressWarnings("deprecation")
	public BattleKitItem(int slot, ItemStack itemStack) {
		this.slot = slot;
		this.amount = itemStack.getAmount();
		this.material = itemStack.getType();
		this.data = itemStack.getData().getData();
		this.name = itemStack.getItemMeta().getDisplayName();
		this.lore = itemStack.getItemMeta().getLore();
		this.enchantments = itemStack.getEnchantments();
	}
	
	public void set(ConfigurationSection item) {
		item.set("slot", slot);
		item.set("amount", amount);
		item.set("splash", splash);
		item.set("material", material.name());
		item.set("data", data);
		item.set("name", name);
		item.set("lore", lore);
		List<String> enchantmentsString = new ArrayList<>();
		enchantments.entrySet().forEach(enchantment -> enchantmentsString.add(enchantment.getKey().getName() + ":" + enchantment.getValue()));
		item.set("enchantments", enchantmentsString);
	}
	
	private List<String> toColorList(List<String> noColoredList) {
		List<String> coloredList = new ArrayList<>();
		noColoredList.forEach(string -> coloredList.add(ChatColor.translateAlternateColorCodes('&', string)));
		return coloredList;
	}
	
	public ItemStack asItemStack() {
		return this.itemStack;
	}
	
	public void give(Player player) {
		player.getInventory().addItem(this.asItemStack());
	}
	
}
