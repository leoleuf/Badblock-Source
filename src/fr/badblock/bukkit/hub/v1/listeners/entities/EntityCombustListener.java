package fr.badblock.bukkit.hub.v1.listeners.entities;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityCombustByBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;

import fr.badblock.bukkit.hub.v1.listeners._HubListener;

public class EntityCombustListener extends _HubListener {

	@EventHandler
	public void onEntityCombustByBlockEvent(EntityCombustByBlockEvent event) {
		event.setCancelled(true);
		event.setDuration(0);
		event.getEntity().setFireTicks(0);
	}

	@EventHandler
	public void onEntityCombustEvent(EntityCombustEvent event) {
		event.setCancelled(true);
		event.setDuration(0);
		event.getEntity().setFireTicks(0);
	}

}
