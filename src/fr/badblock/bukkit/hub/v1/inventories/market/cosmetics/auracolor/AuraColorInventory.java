package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.BlueAuraColorItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.CyanAuraColorItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.GreenAuraColorItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.OrangeAuraColorItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.PinkAuraColorItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.PurpleAuraColorItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.RedAuraColorItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items.YellowAuraColorItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.inventoryitems.BackCosmeticsItem;

public class AuraColorInventory extends CustomInventory
{

	public AuraColorInventory()
	{
		super("hub.items.auracolorinventory", 1);
		this.addItem(new RedAuraColorItem());
		this.addItem(new OrangeAuraColorItem());
		this.addItem(new YellowAuraColorItem());
		this.addItem(new GreenAuraColorItem());
		this.addItem(new CyanAuraColorItem());
		this.addItem(new BlueAuraColorItem());
		this.addItem(new PurpleAuraColorItem());
		this.addItem(new PinkAuraColorItem());
		this.setItem(8, new BackCosmeticsItem());
	}

}
