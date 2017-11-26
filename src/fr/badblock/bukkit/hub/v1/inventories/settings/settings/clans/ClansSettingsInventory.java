package fr.badblock.bukkit.hub.v1.inventories.settings.settings.clans;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.CyanStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.settings.BackSettingsSettingsItem;

public class ClansSettingsInventory extends CustomInventory {

	public ClansSettingsInventory() {
		super("hub.items.clanssettingsinventory", 3);
		BlueStainedGlassPaneItem blueStainedGlassPaneItem = new BlueStainedGlassPaneItem();
		this.setItem(blueStainedGlassPaneItem, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25);
		this.setItem(10, new ClansDescriptionSettingsItem());
		this.setItem(14, new ClansForAllSettingsItem());
		this.setItem(15, new ClansForFriendsSettingsItem());
		this.setItem(16, new ClansForNobodySettingsItem());
		this.setItem(26, new BackSettingsSettingsItem());
		this.setNoFilledItem(new CyanStainedGlassPaneItem());
	}

}
