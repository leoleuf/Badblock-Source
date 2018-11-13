package fr.badblock.bukkit.hub.v1.inventories.selector.submenus.inventories;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.hubchanger.HubChangerBackItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.bedwars.BedWars1x8SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.bedwars.BedWars2x4SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.bedwars.BedWarsDescSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.settings.LightBlueStainedGlassPaneItem;

public class BedWarsChooserInventory extends CustomInventory {

	public BedWarsChooserInventory() {
		// super("§cParamètres/Statistiques", 5);
		super("hub.items.bedwarsselectoritem.submenutitle", 4);
		this.setItem(new BlueStainedGlassPaneItem(), 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 28, 29, 30, 31, 32, 33, 34);
		this.setItem(13, new BedWarsDescSelectorItem());
		this.setItem(21, new BedWars1x8SelectorItem());
		this.setItem(23, new BedWars2x4SelectorItem());
		this.setItem(35, new HubChangerBackItem());
		this.setNoFilledItem(new LightBlueStainedGlassPaneItem());
	}

}
