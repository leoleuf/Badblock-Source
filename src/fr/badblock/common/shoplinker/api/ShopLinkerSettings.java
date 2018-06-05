package fr.badblock.common.shoplinker.api;

import fr.badblock.api.common.tech.rabbitmq.listener.RabbitListenerType;
import fr.badblock.api.common.tech.rabbitmq.packet.RabbitPacketEncoder;
import fr.badblock.api.common.tech.rabbitmq.packet.RabbitPacketType;

public class ShopLinkerSettings
{
	
	public final static String 				QUEUE_PREFIX 		= "shopLinker.";
	public final static RabbitPacketEncoder	PACKET_ENCODAGE 	= RabbitPacketEncoder.UTF8;
	public final static RabbitPacketType  	PACKET_TYPE 		= RabbitPacketType.PUBLISHER;
	public final static RabbitListenerType  LISTENER_TYPE 		= RabbitListenerType.SUBSCRIBER;
	public final static long				TTL					= 86400_000 * 365;
	public final static boolean				DEBUG				= true;
	
}
