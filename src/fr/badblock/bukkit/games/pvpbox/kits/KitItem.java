package fr.badblock.bukkit.games.pvpbox.kits;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class KitItem
{

	private ItemStack						item;
	
	private Material							material;
	private int									amount;
	private short								dataShort;
	private List<KitEnchantment> enchantments;
	
	public KitItem(Material material, int amount, short dataShort, List<KitEnchantment> enchantments)
	{
		this.setMaterial(material);
		this.setAmount(amount);;
		this.setDataShort(dataShort);
		this.setEnchantments(enchantments);
		
		this.buildKit();
	}
	
	public void buildKit()
	{
		ItemStack itemStack = new ItemStack(this.getMaterial(), this.getAmount(), this.getDataShort());
		enchantments.stream().forEach(enchantment -> itemStack.addUnsafeEnchantment(enchantment.getEnchantment(), enchantment.getLevel()));
		
		this.setItem(itemStack);
	}
	
}