package fr.badblock.bukkit.hub.v1.rabbitmq.listeners;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.inventories.selector.dev.DevSelectorInventory;
import fr.badblock.bukkit.hub.v1.rabbitmq.factories.DevAliveFactory;
import fr.badblock.rabbitconnector.RabbitListener;
import fr.badblock.rabbitconnector.RabbitListenerType;

public class DevPacketListener extends RabbitListener {
	public DevPacketListener() {
		super(BadBlockHub.getInstance().getRabbitService(), "dev", false, RabbitListenerType.SUBSCRIBER);
	}

	@Override
	public void onPacketReceiving(String body) {
		if (body == null)
			return;
		
		DevAliveFactory devAliveFactory = BadBlockHub.getInstance().getGson().fromJson(body, DevAliveFactory.class);
		
		if (devAliveFactory == null)
			return;
		DevSelectorInventory.Apply(devAliveFactory);
	}
}
