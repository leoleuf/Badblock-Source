package fr.badblock.bukkit.hub.v1.rabbitmq.listeners;

import java.util.HashMap;
import java.util.Map;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.rabbitconnector.RabbitListener;
import fr.badblock.rabbitconnector.RabbitListenerType;
import lombok.Getter;

public class ReconnectionInvitationsListener extends RabbitListener {
	
	@Getter private static Map<String, String> reconnections = new HashMap<>();
	
	public ReconnectionInvitationsListener() {
		super(BadBlockHub.getInstance().getRabbitService(), "reconnectionInvitations", false, RabbitListenerType.SUBSCRIBER);
	}
	
	@Override
	public void onPacketReceiving(String body) {
		body = body.toLowerCase();
		
		String[] splitter = body.split(";");
		
		String playerName = splitter[0];
		String serverName = splitter[1];
		reconnections.put(playerName.toLowerCase(), serverName);
	}
	
	public static void remove(String playerName)
	{
		playerName = playerName.toLowerCase();
		reconnections.remove(playerName);
	}
	
}

