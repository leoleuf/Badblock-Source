package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.parent.inventories.parent;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.parent.items.MetamorphosisCreatureSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.parent.items.MetamorphosisMaterialSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.inventoryitems.BackCosmeticsItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.CyanStainedGlassPaneItem;

public class MetamorphosisInventory extends CustomInventory {

	public MetamorphosisInventory() {
		super("hub.items.disguisesinventory", 3);
		BlueStainedGlassPaneItem blueStainedGlassPaneItem = new BlueStainedGlassPaneItem();
		this.setItem(blueStainedGlassPaneItem, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25);
		this.setItem(11, new MetamorphosisCreatureSelectorItem());
		this.setItem(15, new MetamorphosisMaterialSelectorItem());
		this.setItem(26, new BackCosmeticsItem());
		this.setNoFilledItem(new CyanStainedGlassPaneItem());
	}

}
