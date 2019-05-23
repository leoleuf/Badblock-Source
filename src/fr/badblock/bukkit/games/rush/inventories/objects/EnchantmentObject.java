package fr.badblock.bukkit.games.rush.inventories.objects;

import org.bukkit.enchantments.Enchantment;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EnchantmentObject
{

	private Enchantment enchantment;
	private	int			level;
	
}
