package fr.badblock.common.shoplinker.api;

import com.google.gson.Gson;

import fr.badblock.api.common.tech.rabbitmq.RabbitService;
import fr.badblock.api.common.tech.rabbitmq.packet.RabbitPacket;
import fr.badblock.api.common.tech.rabbitmq.packet.RabbitPacketMessage;
import fr.badblock.common.shoplinker.api.objects.ShopData;
import fr.badblock.common.shoplinker.api.objects.ShopDataDestination;
import fr.badblock.common.shoplinker.api.objects.ShopTrame;
import fr.badblock.common.shoplinker.api.objects.ShopType;
import fr.badblock.common.shoplinker.bukkit.listeners.rabbitmq.ReceiveCommandListener;
import lombok.Data;
import lombok.Getter;

@Data
public class ShopLinkerAPI
{

	public transient static String CURRENT_SERVER_NAME = null;

	@Getter public static Gson gson	= new Gson();
	
	private RabbitService		   	rabbitService;
	private ReceiveCommandListener  receiveCommandListener;

	// CONSTRUCTOR
	public ShopLinkerAPI(RabbitService rabbitService)
	{
		this.setRabbitService(rabbitService);
	}

	// PUBLIC
	public void sendShopData(ShopType shopType, String serverName, String playerName, String command, String displayName, int[] depends, boolean multibuy, boolean ingame, double price, boolean forceCommand)
	{
		sendShopData(serverName, buildShopData(shopType, playerName, command, displayName, depends, multibuy, ingame, price, forceCommand));
	}

	// PRIVATE
	private void sendShopData(ShopTrame shopTrame)
	{
		if (shopTrame.getShopDataDestination().getQueueName().equalsIgnoreCase(CURRENT_SERVER_NAME))
		{
			if (receiveCommandListener == null)
			{
				receiveCommandListener = new ReceiveCommandListener(rabbitService, CURRENT_SERVER_NAME);
			}
			receiveCommandListener.onPacketReceiving(buildPacketData(shopTrame));
			return;
		}
		RabbitPacketMessage rabbitMessage = new RabbitPacketMessage(ShopLinkerSettings.TTL, buildPacketData(shopTrame));
		RabbitPacket rabbitPacket = new RabbitPacket(rabbitMessage, buildQueueName(shopTrame), ShopLinkerSettings.DEBUG, ShopLinkerSettings.PACKET_ENCODAGE, ShopLinkerSettings.PACKET_TYPE);
		rabbitService.sendPacket(rabbitPacket);
	}

	private void sendShopData(String serverName, ShopData shopData) {
		sendShopData(buildDataDestination(serverName), shopData);
	}

	private String buildQueueName(ShopTrame shopTrame) {
		return ShopLinkerSettings.QUEUE_PREFIX + shopTrame.getShopDataDestination().getQueueName();
	}

	private String buildPacketData(ShopTrame shopTrame) {
		return gson.toJson(shopTrame.getShopData());
	}
	private ShopDataDestination buildDataDestination(String serverName) {
		return new ShopDataDestination(serverName);
	}

	private ShopData buildShopData(ShopType shopType, String playerName, String command, String displayName, int[] depends, boolean multibuy, boolean ingame, double price, boolean forceCommand) {
		return new ShopData(shopType, playerName, command, displayName, depends, multibuy, ingame, price, forceCommand);
	}

	private ShopTrame buildShopTrame(ShopDataDestination shopDataDestination, ShopData shopData) {
		return new ShopTrame(shopData, shopDataDestination);
	}

	private void sendShopData(ShopDataDestination shopDataDestination, ShopData shopData) {
		sendShopData(buildShopTrame(shopDataDestination, shopData));
	}

}
