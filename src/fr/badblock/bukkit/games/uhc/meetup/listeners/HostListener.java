package fr.badblock.bukkit.games.uhc.meetup.listeners;

import java.util.Map;

import org.bukkit.event.EventHandler;

import fr.badblock.bukkit.games.uhc.meetup.PluginUHC;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.events.HostUpdateEvent;
import fr.badblock.gameapi.utils.BukkitUtils;

public class HostListener extends BadListener
{

	@EventHandler
	public void onHostUpdate(HostUpdateEvent event)
	{
		Map<String, Object> parameters = event.getParameters();

		if (parameters.containsKey("playersperteam"))
		{
			int perTeam = Integer.parseInt((String) parameters.get("playersperteam"));

			PluginUHC.getInstance().getConfiguration().maxPlayersInTeam = perTeam;
			PluginUHC.getInstance().setMaxPlayers(perTeam);
			try {
				BukkitUtils.setMaxPlayers(perTeam);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	
}
