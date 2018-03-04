package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPortalEvent;

import fr.badblock.gameapi.BadListener;

public class PlayerPortalListener extends BadListener
{

	@EventHandler
	public void onPlayerPortal(PlayerPortalEvent event)
	{
		event.setCancelled(true);
	}

}
