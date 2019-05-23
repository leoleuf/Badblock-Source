package fr.badblock.bukkit.games.rush.inventories.config;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemLoader {

	public ItemStack loadItem(String displayName, List<String> lore, Material material, int amount, boolean fakeEnchant) {
		ItemStack itemStack = new ItemStack(material, amount);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(displayName);
		itemMeta.setLore(lore);
		if (fakeEnchant) itemStack = fakeEnchant(itemStack);
		return itemStack;
	}
	
	public static ItemStack fakeEnchant(ItemStack itemStack) {
		itemStack.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 1);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
	
}
