package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemBreakEvent;

import fr.badblock.gameapi.BadListener;

public class PlayerItemBreakListener extends BadListener
{

	@EventHandler
	public void onPlayerItemBreak(PlayerItemBreakEvent event)
	{
		event.getBrokenItem().setDurability((short) 0);
	}

}
