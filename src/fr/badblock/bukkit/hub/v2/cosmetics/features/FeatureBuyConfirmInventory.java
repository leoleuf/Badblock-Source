package fr.badblock.bukkit.hub.v2.cosmetics.features;

import java.util.Arrays;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.badblock.bukkit.hub.v2.inventories.custom.CustomInventoryBuyConfirm;
import fr.badblock.bukkit.hub.v2.players.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;


public class FeatureBuyConfirmInventory
{

	public static void confirm(BadblockPlayer player, String featureRawName)
	{
		// Set temporary feature
		HubPlayer hubPlayer = HubPlayer.get(player);
		hubPlayer.setBuyFeature(featureRawName);
		
		// Generate inventory
		Inventory inventory = Bukkit.createInventory(null, 9);
		int confirmItem = new Random().nextInt(9);
		for (int i = 0; i < 9; i++)
		{
			if (i == confirmItem)
			{
				continue;
			}
			inventory.setItem(i, getCancelItem(player));
		}
		inventory.setItem(confirmItem, getConfirmItem(player));
		
		// Open it
		player.openInventory(inventory);
		hubPlayer.setCustomInventory(CustomInventoryBuyConfirm.getInstance());
	}
	
	public static ItemStack getCancelItem(BadblockPlayer player)
	{
		ItemStack itemStack = new ItemStack(Material.REDSTONE_BLOCK, 1);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(player.getTranslatedMessage("hub.buy.cancelname")[0]);
		itemMeta.setLore(Arrays.asList(player.getTranslatedMessage("hub.buy.cancellore")));
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
	
	public static ItemStack getConfirmItem(BadblockPlayer player)
	{
		ItemStack itemStack = new ItemStack(Material.EMERALD_BLOCK, 1);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(player.getTranslatedMessage("hub.buy.confirmname")[0]);
		itemMeta.setLore(Arrays.asList(player.getTranslatedMessage("hub.buy.confirmlore")));
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
	
}
