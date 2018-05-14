package fr.badblock.common.shoplinker.api;

import fr.badblock.common.shoplinker.api.objects.ShopData;
import fr.badblock.common.shoplinker.api.objects.ShopDataDestination;
import fr.badblock.common.shoplinker.api.objects.ShopTrame;
import fr.badblock.common.shoplinker.api.objects.ShopType;
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
	public void sendShopData(ShopType shopType, String serverName, String playerName, String command, String displayName, int[] depends, boolean multibuy, boolean ingame, double price) {
		sendShopData(serverName, buildShopData(shopType, playerName, command, displayName, depends, multibuy, ingame, price));
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
	
	private ShopData buildShopData(ShopType shopType, String playerName, String command, String displayName, int[] depends, boolean multibuy, boolean ingame, double price) {
		return new ShopData(shopType, playerName, command, displayName, depends, multibuy, ingame, price);
	}
	
	private ShopTrame buildShopTrame(ShopDataDestination shopDataDestination, ShopData shopData) {
		return new ShopTrame(shopData, shopDataDestination);
	}
	
	private void sendShopData(ShopDataDestination shopDataDestination, ShopData shopData) {
		sendShopData(buildShopTrame(shopDataDestination, shopData));
	}

}
