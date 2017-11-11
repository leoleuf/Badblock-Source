package fr.badblock.bukkit.hub.listeners.players;

import org.bukkit.event.EventHandler;

import fr.badblock.bukkit.hub.inventories.market.properties.CustomProperty;
import fr.badblock.bukkit.hub.listeners._HubListener;
import fr.badblock.common.shoplinker.api.objects.ShopData;
import fr.badblock.common.shoplinker.bukkit.events.ReceivedRemoteCommandEvent;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;

public class PlayerBuyListener extends _HubListener {

	private final static String SEPARATOR = "_";
	
	@EventHandler (ignoreCancelled = false)
	public void onPlayerBuyListener(ReceivedRemoteCommandEvent event) {
		ShopData shopData = event.getShopData();
		String objectName = shopData.getObjectName();
		// No separator
		if (!objectName.contains(SEPARATOR)) return;
		String propertyData = objectName.split(SEPARATOR)[1];
		if (!CustomProperty.isACustomProperty(objectName)) {
			System.out.println("[HUB] " + objectName + " isn't a property.");
			return;
		}
		CustomProperty customProperty = CustomProperty.getPropertyTypeByName(objectName);
		BadblockPlayer player = BukkitUtils.getPlayer(shopData.getPlayerName());
		if (player == null) return;
		customProperty.run(player, propertyData);
	}

}
