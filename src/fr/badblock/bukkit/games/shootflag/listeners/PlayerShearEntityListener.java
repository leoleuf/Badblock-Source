package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerShearEntityEvent;

import fr.badblock.gameapi.BadListener;

public class PlayerShearEntityListener extends BadListener
{

	@EventHandler
	public void onPlayerShearEntity(PlayerShearEntityEvent event)
	{
		event.setCancelled(true);
	}

}
