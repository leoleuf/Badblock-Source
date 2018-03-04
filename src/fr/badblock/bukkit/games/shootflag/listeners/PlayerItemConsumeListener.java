package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import fr.badblock.gameapi.BadListener;

public class PlayerItemConsumeListener extends BadListener
{

	@EventHandler
	public void onPlayerConsume(PlayerItemConsumeEvent event)
	{
		// TODO: consumable pots?
		event.setCancelled(true);
	}

}
