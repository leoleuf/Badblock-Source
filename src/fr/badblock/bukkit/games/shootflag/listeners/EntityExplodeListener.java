package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityExplodeEvent;

import fr.badblock.gameapi.BadListener;

public class EntityExplodeListener extends BadListener
{

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event)
	{
		event.setCancelled(true);
	}

}
