package fr.badblock.bukkit.hub.v1.listeners.world;

import org.bukkit.event.EventHandler;
import org.github.paperspigot.exception.ServerSchedulerException;

import fr.badblock.bukkit.hub.v1.listeners._HubListener;

public class ServerSchedulerListener extends _HubListener {

	@EventHandler
	public void onServerSchedulerException(ServerSchedulerException event) {
		event.printStackTrace();
	}
	
}
