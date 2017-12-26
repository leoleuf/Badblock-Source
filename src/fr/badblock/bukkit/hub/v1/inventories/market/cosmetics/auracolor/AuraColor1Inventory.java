package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.one.BlueAuraColor1Item;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.one.CyanAuraColor1Item;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.one.GreenAuraColor1Item;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.one.OrangeAuraColor1Item;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.one.PinkAuraColor1Item;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.one.PurpleAuraColor1Item;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.one.RedAuraColor1Item;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.one.YellowAuraColor1Item;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.inventoryitems.BackCosmeticsItem;

public class AuraColor1Inventory extends CustomInventory
{

	public AuraColor1Inventory()
	{
		super("hub.items.auracolor1inventory", 1);
		this.addItem(new RedAuraColor1Item());
		this.addItem(new OrangeAuraColor1Item());
		this.addItem(new YellowAuraColor1Item());
		this.addItem(new GreenAuraColor1Item());
		this.addItem(new CyanAuraColor1Item());
		this.addItem(new BlueAuraColor1Item());
		this.addItem(new PurpleAuraColor1Item());
		this.addItem(new PinkAuraColor1Item());
		this.setItem(8, new BackCosmeticsItem());
	}

}
