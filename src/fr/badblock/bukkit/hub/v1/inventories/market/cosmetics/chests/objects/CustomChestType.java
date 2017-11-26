package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.chests.objects;

import java.util.Map;

import org.bukkit.inventory.ItemStack;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class CustomChestType {
	
	public int					id;
	public ItemStack 			itemStack;
	public long					giveEachSeconds;
	public Map<String, Long>	winRates;

}
