package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.two.BlueAuraColor2Item;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.two.CyanAuraColor2Item;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.two.GreenAuraColor2Item;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.two.OrangeAuraColor2Item;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.two.PinkAuraColor2Item;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.two.PurpleAuraColor2Item;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.two.RedAuraColor2Item;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.two.YellowAuraColor2Item;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.inventoryitems.BackCosmeticsItem;

public class AuraColor2Inventory extends CustomInventory
{

	public AuraColor2Inventory()
	{
		super("hub.items.auracolor2inventory", 1);
		this.addItem(new RedAuraColor2Item());
		this.addItem(new OrangeAuraColor2Item());
		this.addItem(new YellowAuraColor2Item());
		this.addItem(new GreenAuraColor2Item());
		this.addItem(new CyanAuraColor2Item());
		this.addItem(new BlueAuraColor2Item());
		this.addItem(new PurpleAuraColor2Item());
		this.addItem(new PinkAuraColor2Item());
		this.setItem(8, new BackCosmeticsItem());
	}

}
