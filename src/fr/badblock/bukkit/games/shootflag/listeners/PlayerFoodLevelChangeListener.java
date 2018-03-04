package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import fr.badblock.gameapi.BadListener;

public class PlayerFoodLevelChangeListener extends BadListener
{

	@EventHandler
	public void onPlayerFoodLevelChange(FoodLevelChangeEvent event)
	{
		event.setCancelled(true);
	}

}
