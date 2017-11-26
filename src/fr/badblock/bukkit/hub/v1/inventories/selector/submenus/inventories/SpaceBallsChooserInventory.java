package fr.badblock.bukkit.hub.v1.inventories.selector.submenus.inventories;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.hubchanger.HubChangerBackItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.spaceballs.SpaceBalls4v4SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.spaceballs.SpaceBallsBookSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.spaceballs.SpaceBallsDescSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.settings.LightBlueStainedGlassPaneItem;

public class SpaceBallsChooserInventory extends CustomInventory {

	public SpaceBallsChooserInventory() {
		// super("§cParamètres/Statistiques", 5);
		super("hub.items.spaceballsselectoritem.submenutitle", 4);
		this.setItem(new BlueStainedGlassPaneItem(), 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 28, 29, 30, 31, 32, 33, 34);
		this.setItem(13, new SpaceBallsDescSelectorItem());
		this.setItem(22, new SpaceBalls4v4SelectorItem());
		this.setItem(27, new SpaceBallsBookSelectorItem());
		this.setItem(35, new HubChangerBackItem());
		this.setNoFilledItem(new LightBlueStainedGlassPaneItem());
	}

}
