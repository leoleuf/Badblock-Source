package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;

import fr.badblock.gameapi.BadListener;

public class PlayerPickupItemListener extends BadListener
{

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event)
	{
		event.setCancelled(true);
	}

}
