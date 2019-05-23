package fr.badblock.bukkit.games.survivalgames.listeners;

import java.util.Map;

import org.bukkit.event.EventHandler;

import fr.badblock.bukkit.games.survivalgames.PluginSurvival;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.events.HostUpdateEvent;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.BukkitUtils;

public class HostListener extends BadListener
{

	@EventHandler
	public void onHostUpdate(HostUpdateEvent event)
	{
		Map<String, Object> parameters = event.getParameters();

		if (parameters.containsKey("slots"))
		{
			int slots = Integer.parseInt((String) parameters.get("slots"));

			PluginSurvival.getInstance().getConfiguration().maxPlayers = slots;
			PluginSurvival.getInstance().setMaxPlayers(slots);
			try {
				BukkitUtils.setMaxPlayers(slots);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
