package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;

import fr.badblock.gameapi.BadListener;

public class PlayerDropItemListener extends BadListener
{

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event)
	{
		event.setCancelled(true);
	}

}
