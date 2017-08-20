package fr.badblock.common.shoplinker.api;

import fr.badblock.common.shoplinker.api.objects.*;
import fr.badblock.common.shoplinker.bukkit.listeners.rabbitmq.ReceiveCommandListener;
import fr.badblock.rabbitconnector.RabbitService;
import lombok.Data;

@Data
public class ShopLinkerAPI {
	
	public transient static String CURRENT_SERVER_NAME = null;
	
	private RabbitService		   	rabbitService;
	private ReceiveCommandListener  receiveCommandListener;
	
	// CONSTRUCTOR
	public ShopLinkerAPI(RabbitService rabbitService) {
		this.setRabbitService(rabbitService);
	}
	
	// PUBLIC
	public void sendShopData(ShopType shopType, String serverName, String playerName, String objectName, String displayName, int[] depends, boolean multibuy) {
		sendShopData(serverName, buildShopData(shopType, playerName, objectName, displayName, depends, multibuy));
	}

	// PRIVATE
	private void sendShopData(ShopTrame shopTrame) {
		if (shopTrame.getShopDataDestination().getQueueName().equalsIgnoreCase(CURRENT_SERVER_NAME)) {
			if (receiveCommandListener == null) receiveCommandListener = new ReceiveCommandListener(CURRENT_SERVER_NAME);
			receiveCommandListener.onPacketReceiving(buildPacketData(shopTrame));
			return;
		}
		rabbitService.sendAsyncPacket(buildQueueName(shopTrame), buildPacketData(shopTrame), ShopLinkerSettings.PACKET_ENCODAGE, ShopLinkerSettings.PACKET_TYPE, ShopLinkerSettings.TTL, ShopLinkerSettings.DEBUG);
	}

	private void sendShopData(String serverName, ShopData shopData) {
		sendShopData(buildDataDestination(serverName), shopData);
	}
	
	private String buildQueueName(ShopTrame shopTrame) {
		return ShopLinkerSettings.QUEUE_PREFIX + shopTrame.getShopDataDestination().getQueueName();
	}
	
	private String buildPacketData(ShopTrame shopTrame) {
		return ReceiveCommandListener.getGson().toJson(shopTrame.getShopData());
	}
	private ShopDataDestination buildDataDestination(String serverName) {
		return new ShopDataDestination(serverName);
	}
	
	private ShopData buildShopData(ShopType shopType, String playerName, String rankName, String displayName, int[] depends, boolean multibuy) {
		return new ShopData(shopType, playerName, rankName, displayName, depends, multibuy);
	}
	
	private ShopTrame buildShopTrame(ShopDataDestination shopDataDestination, ShopData shopData) {
		return new ShopTrame(shopData, shopDataDestination);
	}
	
	private void sendShopData(ShopDataDestination shopDataDestination, ShopData shopData) {
		sendShopData(buildShopTrame(shopDataDestination, shopData));
	}

}
