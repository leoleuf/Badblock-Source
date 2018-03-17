package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

import fr.badblock.gameapi.BadListener;

public class WeatherChangeListener extends BadListener
{
	
	private boolean denyRain = false;

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event)
	{
		if (event.toWeatherState())
		{
			event.setCancelled(denyRain);
		}
		denyRain = true;
	}

}
