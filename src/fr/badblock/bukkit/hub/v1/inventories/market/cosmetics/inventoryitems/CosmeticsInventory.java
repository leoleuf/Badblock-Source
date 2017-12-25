package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.inventoryitems;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.CyanStainedGlassPaneItem;

public class CosmeticsInventory extends CustomInventory {

	public CosmeticsInventory() {
		super("hub.items.cosmeticsinventory", 5);
		BlueStainedGlassPaneItem blueStainedGlassPaneItem = new BlueStainedGlassPaneItem();
		this.setItem(blueStainedGlassPaneItem, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41,
				42, 43);
		this.setItem(13, new BoosterCosmeticsItem());
		this.setItem(20, new EffectsCosmeticsItem());
		this.setItem(21, new MountsCosmeticsItem());
		this.setItem(23, new MetamorphosisCosmeticsItem());
		this.setItem(24, new GadgetsCosmeticsItem());
		this.setItem(31, new AuraColorCosmeticsItem());
		this.setItem(44, new QuitCosmeticsItem());
		this.setNoFilledItem(new CyanStainedGlassPaneItem());
	}

}
