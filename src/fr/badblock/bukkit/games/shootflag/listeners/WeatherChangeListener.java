package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.weather.WeatherChangeEvent;

import fr.badblock.gameapi.BadListener;

public class WeatherChangeListener extends BadListener
{

	@EventHandler
	public void onWeatherChange(WeatherChangeEvent event)
	{
		if (event.toWeatherState())
		{
			event.setCancelled(true);
		}
	}

}
