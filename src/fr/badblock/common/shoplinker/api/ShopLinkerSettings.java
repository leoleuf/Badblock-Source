package fr.badblock.common.shoplinker.api;

import fr.badblock.api.common.tech.rabbitmq.listener.RabbitListenerType;
import fr.badblock.api.common.tech.rabbitmq.packet.RabbitPacketEncoder;
import fr.badblock.api.common.tech.rabbitmq.packet.RabbitPacketType;

public class ShopLinkerSettings
{
	
	public static String 				QUEUE_PREFIX 		= "shopLinker.";
	public static RabbitPacketEncoder	PACKET_ENCODAGE 	= RabbitPacketEncoder.UTF8;
	public static RabbitPacketType  	PACKET_TYPE 		= RabbitPacketType.PUBLISHER;
	public static RabbitListenerType  	LISTENER_TYPE 		= RabbitListenerType.SUBSCRIBER;
	public static long					TTL					= 86400_000 * 365;
	public static boolean				DEBUG				= true;
	
}
