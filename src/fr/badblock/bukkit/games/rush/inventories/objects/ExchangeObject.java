package fr.badblock.bukkit.games.rush.inventories.objects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ExchangeObject
{

	private Material				material;
	private int		 				amount;
	private short					data;
	private List<EnchantmentObject>	enchantments;
	
	public ExchangeObject(String m, int amount, short data, List<EnchantmentObject> enchantments)
	{
		this(getMaterial(m), amount, data, enchantments);
	}

	private static Material getMaterial(String material)
	{
		for (Material m : Material.values())
		{
			if (m.name().equalsIgnoreCase(material))
			{
				return m;
			}
		}
		
		return null;
	}
	
	public static ExchangeObject toExchange(String rawExchange)
	{
		String[] sRaw = rawExchange.split("\\|");
		
		String firstData = sRaw[0];
		
		String[] splitter = firstData.split("-");
		String rawAmount = splitter[0];
		String rawMaterial = splitter[1];
		short data = 0;
		
		if (rawMaterial.contains("="))
		{
			String[] sD = rawMaterial.split("=");
			rawMaterial = sD[0];
			data = (short) Integer.parseInt(sD[1]);
		}
		
		int amount = 1;
		
		try
		{
			amount = Integer.parseInt(rawAmount);
		}
		catch (Exception error)
		{
		}
		
		List<EnchantmentObject> enchantments = new ArrayList<>();
		
		for (int i = 0; i < sRaw.length; i++)
		{
			if (i == 0)
			{
				continue;
			}
			String rawEnchantment = sRaw[i];
			String[] splt = rawEnchantment.split("-");
			int level = Integer.parseInt(splt[0]);
			String enchantmentName = splt[1];
			Enchantment enchantment = getEnchantment(enchantmentName);
			EnchantmentObject enchantmentObject = new EnchantmentObject(enchantment, level);
			enchantments.add(enchantmentObject);
		}
		
		return new ExchangeObject(rawMaterial, amount, data, enchantments);
	}
	
	public static Enchantment getEnchantment(String rawEnchantment)
	{
		for (Enchantment enchantment : Enchantment.values())
		{
			if (enchantment.getName().equalsIgnoreCase(rawEnchantment))
			{
				return enchantment;
			}
		}
		return null;
	}
	
}
