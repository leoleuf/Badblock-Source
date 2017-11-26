package fr.badblock.bukkit.hub.v1.rabbitmq.listeners;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.tasks.RequestBoosterTask;
import fr.badblock.rabbitconnector.RabbitListener;
import fr.badblock.rabbitconnector.RabbitListenerType;

public class BoosterUpdateListener extends RabbitListener {
	
	public BoosterUpdateListener() {
		super(BadBlockHub.getInstance().getRabbitService(), "boosterRefresh", false, RabbitListenerType.SUBSCRIBER);
	}
	
	@Override
	public void onPacketReceiving(String body) {
		RequestBoosterTask.work();
	}
	
}

