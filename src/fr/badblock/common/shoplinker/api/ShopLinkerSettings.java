package fr.badblock.common.shoplinker.api;

import fr.badblock.rabbitconnector.RabbitListenerType;
import fr.badblock.rabbitconnector.RabbitPacketType;
import fr.badblock.utils.Encodage;

public class ShopLinkerSettings {
	
	public final static String 				QUEUE_PREFIX 		= "shopLinker.";
	public final static Encodage			PACKET_ENCODAGE 	= Encodage.UTF8;
	public final static RabbitPacketType  	PACKET_TYPE 		= RabbitPacketType.PUBLISHER;
	public final static RabbitListenerType  LISTENER_TYPE 		= RabbitListenerType.SUBSCRIBER;
	public final static long				TTL					= -1;
	public final static boolean				DEBUG				= false;
	
}
