package fr.badblock.bukkit.hub.v1.inventories.selector.submenus.inventories;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.QuitSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.skyblock.SkyBlockNewSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.skyblock.SkyBlockOldSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.settings.LightBlueStainedGlassPaneItem;

public class SkyBlockChooserInventory extends CustomInventory {

	public SkyBlockChooserInventory() {
		super("hub.items.skyblockchooserinventory.submenutitle", 5);
		this.setItem(new BlueStainedGlassPaneItem(), 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44);
		this.setItem(36, new SkyBlockOldSelectorItem());
		this.setItem(22, new SkyBlockNewSelectorItem());
		this.setItem(44, new QuitSelectorItem());
		this.setNoFilledItem(new LightBlueStainedGlassPaneItem());
	}

}
