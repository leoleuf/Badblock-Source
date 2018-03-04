package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemHeldEvent;

import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameState;

public class PlayerItemHeldListener extends BadListener
{

	@EventHandler
	public void onPlayerItemHeld(PlayerItemHeldEvent event)
	{
		if (GameAPI.getAPI().getGameServer().getGameState().equals(GameState.WAITING))
		{
			return;
		}
		event.setCancelled(true);
	}

}
