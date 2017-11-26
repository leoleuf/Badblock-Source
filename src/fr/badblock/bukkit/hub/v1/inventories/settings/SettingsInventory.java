package fr.badblock.bukkit.hub.v1.inventories.settings;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.CyanStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.QuitSettingsItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.SettingsItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.SkullSettingsItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.StatisticsSettingsItem;

public class SettingsInventory extends CustomInventory {

	public SettingsInventory() {
		// super("§cParamètres/Statistiques", 5);
		super("hub.items.settingsinventory", 5);
		BlueStainedGlassPaneItem blueStainedGlassPaneItem = new BlueStainedGlassPaneItem();
		this.setItem(blueStainedGlassPaneItem, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41,
				42, 43);
		this.setItem(20, new SettingsItem());
		this.setItem(22, new SkullSettingsItem());
		this.setItem(24, new StatisticsSettingsItem());
		this.setItem(44, new QuitSettingsItem());
		this.setNoFilledItem(new CyanStainedGlassPaneItem());
	}

}
