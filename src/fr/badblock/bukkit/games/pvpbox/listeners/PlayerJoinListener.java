package fr.badblock.bukkit.games.pvpbox.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.badblock.bukkit.games.pvpbox.PvPBox;
import fr.badblock.bukkit.games.pvpbox.players.BoxPlayer;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.events.api.PlayerLoadedEvent;
import fr.badblock.gameapi.players.BadblockPlayer;

public class PlayerJoinListener extends BadListener
{

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();

		if (player.hasPermission("pvpbox.modo"))
		{
			event.setJoinMessage("");
		}
		else
		{
			event.setJoinMessage("[§a+§r] " + player.getName());
		}
	}

	@EventHandler
	public void onPlayerLoaded(PlayerLoadedEvent event)
	{	
		Bukkit.getScheduler().runTask(PvPBox.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				BadblockPlayer player = event.getPlayer();
				BoxPlayer boxPlayer = BoxPlayer.make(player);

				if (boxPlayer == null)
				{
					return;
				}

				boxPlayer.reset();
				player.setGameMode(GameMode.ADVENTURE);
			}
		});
	}

}