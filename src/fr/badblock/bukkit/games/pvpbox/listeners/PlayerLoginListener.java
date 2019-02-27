package fr.badblock.bukkit.games.pvpbox.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;

public class PlayerLoginListener extends BadListener
{

	@EventHandler
	public void onLogin(PlayerLoginEvent event)
	{
		double tps = GameAPI.getAPI().getGameServer().getPassmarkTps();
		
		if (tps < 18)
		{
			event.disallow(Result.KICK_FULL, GameAPI.i18n().get("pvpbox.overload")[0]);
		}
	}
	
}
