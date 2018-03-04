package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBedEnterEvent;

import fr.badblock.gameapi.BadListener;

public class PlayerBedEnterListener extends BadListener
{

	@EventHandler
	public void onPlayerBedEnter(PlayerBedEnterEvent event)
	{
		event.setCancelled(true);
	}

}
