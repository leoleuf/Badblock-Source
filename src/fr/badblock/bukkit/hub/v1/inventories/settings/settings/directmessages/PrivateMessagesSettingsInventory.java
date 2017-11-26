package fr.badblock.bukkit.hub.v1.inventories.settings.settings.directmessages;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.CyanStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.settings.BackSettingsSettingsItem;

public class PrivateMessagesSettingsInventory extends CustomInventory {

	public PrivateMessagesSettingsInventory() {
		super("hub.items.privatemessagessettingsinventory", 3);
		BlueStainedGlassPaneItem blueStainedGlassPaneItem = new BlueStainedGlassPaneItem();
		this.setItem(blueStainedGlassPaneItem, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25);
		this.setItem(10, new PrivateMessagesDescriptionSettingsItem());
		this.setItem(14, new PrivateMessagesForAllSettingsItem());
		this.setItem(15, new PrivateMessagesForFriendsSettingsItem());
		this.setItem(16, new PrivateMessagesForNobodySettingsItem());
		this.setItem(26, new BackSettingsSettingsItem());
		this.setNoFilledItem(new CyanStainedGlassPaneItem());
	}

}
