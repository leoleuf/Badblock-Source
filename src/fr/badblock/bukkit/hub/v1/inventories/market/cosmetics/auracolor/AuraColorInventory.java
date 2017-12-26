package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.select.AuraColor1SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.select.AuraColor2SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.inventoryitems.BackCosmeticsItem;

public class AuraColorInventory extends CustomInventory
{

	public AuraColorInventory()
	{
		super("hub.items.auracolorinventory", 1);
		this.setItem(2, new AuraColor1SelectorItem());
		this.setItem(5, new AuraColor2SelectorItem());
		this.setItem(8, new BackCosmeticsItem());
	}

}
