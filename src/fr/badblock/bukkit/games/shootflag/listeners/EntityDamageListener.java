package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import fr.badblock.bukkit.games.shootflag.players.ShootFlagData;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;

public class EntityDamageListener extends BadListener
{
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if (!inGame())
		{
			event.setCancelled(true);
			return;
		}
		
		// Player damage type
		Entity entity = event.getEntity();
		if (entity instanceof Player)
		{
			Entity damager = event.getDamager();
			if (damager instanceof Player)
			{
				BadblockPlayer player = (BadblockPlayer) damager;
				ShootFlagData playerData = player.inGameData(ShootFlagData.class);
				event.setCancelled(playerData.canHurt < System.currentTimeMillis());
			}
			else
			{
				event.setCancelled(true);
			}
		}
		else
		{
			event.setCancelled(true);
		}
		
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (!inGame())
		{
			event.setCancelled(true);
			return;
		}
		
		// Player damage type
		Entity entity = event.getEntity();
		if (!(entity instanceof Player) || !event.getCause().equals(DamageCause.ENTITY_ATTACK))
		{
			event.setCancelled(true);
		}
		
	}

}
