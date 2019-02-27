package fr.badblock.bukkit.games.pvpbox.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.badblock.bukkit.games.pvpbox.players.BoxPlayer;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;

public class PlayerQuitListener extends BadListener
{

	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		BadblockPlayer player = (BadblockPlayer) event.getPlayer();
		BoxPlayer boxPlayer = BoxPlayer.get(player);

		if (player.hasPermission("pvpbox.modo"))
		{
			event.setQuitMessage("");
		}
		else
		{
			event.setQuitMessage("[§c-§r] " + player.getName());
		}

		if (boxPlayer == null)
		{
			return;
		}

		boxPlayer.remove();
	}

}
