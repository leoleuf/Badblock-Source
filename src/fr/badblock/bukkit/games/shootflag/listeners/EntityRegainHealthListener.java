package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityRegainHealthEvent;

import fr.badblock.gameapi.BadListener;

public class EntityRegainHealthListener extends BadListener
{

	@EventHandler
	public void onEntityRegainHealth(EntityRegainHealthEvent event)
	{
		Entity entity = event.getEntity();
		if (entity instanceof Player)
		{
			event.setCancelled(true);
		}
	}

}
