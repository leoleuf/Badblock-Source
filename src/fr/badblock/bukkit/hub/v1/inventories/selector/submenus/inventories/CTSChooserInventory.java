package fr.badblock.bukkit.hub.v1.inventories.selector.submenus.inventories;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.hubchanger.HubChangerBackItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.cts.CTS8v8SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.cts.CTSBookSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.cts.CTSDescSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.settings.LightBlueStainedGlassPaneItem;

public class CTSChooserInventory extends CustomInventory {

	public CTSChooserInventory() {
		super("hub.items.ctsselectoritem.submenutitle", 4);
		this.setItem(new BlueStainedGlassPaneItem(), 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 28, 29, 30, 31, 32, 33, 34);
		this.setItem(13, new CTSDescSelectorItem());
		this.setItem(22, new CTS8v8SelectorItem());
		this.setItem(27, new CTSBookSelectorItem());
		this.setItem(35, new HubChangerBackItem());
		this.setNoFilledItem(new LightBlueStainedGlassPaneItem());
	}

}
