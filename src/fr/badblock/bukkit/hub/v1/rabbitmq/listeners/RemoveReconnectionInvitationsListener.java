package fr.badblock.bukkit.hub.v1.rabbitmq.listeners;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.rabbitconnector.RabbitListener;
import fr.badblock.rabbitconnector.RabbitListenerType;

public class RemoveReconnectionInvitationsListener extends RabbitListener {
	
	public RemoveReconnectionInvitationsListener() {
		super(BadBlockHub.getInstance().getRabbitService(), "removeReconnectionInvitations", false, RabbitListenerType.SUBSCRIBER);
	}
	
	@Override
	public void onPacketReceiving(String body) {
		body = body.toLowerCase();
		ReconnectionInvitationsListener.remove(body);
	}
	
}

