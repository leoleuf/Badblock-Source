package fr.badblock.bukkit.hub.v1.inventories.connect;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.settings.LightBlueStainedGlassPaneItem;

public class ConnectInventory extends CustomInventory {

	public ConnectInventory() {
		super("hub.items.connectinventory.name", 5);
		LightBlueStainedGlassPaneItem lightBlueStainedGlassPaneItem = new LightBlueStainedGlassPaneItem();
		this.setItem(lightBlueStainedGlassPaneItem, 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34);
		this.setItem(21, new CrackConnectItem());
		this.setItem(23, new PremiumConnectItem());
		this.setNoFilledItem(new BlueStainedGlassPaneItem());
	}

}
