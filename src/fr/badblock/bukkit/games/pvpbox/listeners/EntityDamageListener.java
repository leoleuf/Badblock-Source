package fr.badblock.bukkit.games.pvpbox.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.projectiles.ProjectileSource;

import fr.badblock.bukkit.games.pvpbox.PvPBoxMapProtector;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;

public class EntityDamageListener extends BadListener
{

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event)
	{
		if (event.getCause().equals(DamageCause.FALL))
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event)
	{
		if (!event.getDamager().getType().equals(EntityType.PLAYER) && !event.getDamager().getType().equals(EntityType.ARROW))
		{
			event.setCancelled(true);
			return;
		}
		
		if (!event.getEntity().getType().equals(EntityType.PLAYER))
		{
			event.setCancelled(true);
			return;
		}
		
		BadblockPlayer dmgr = null;
		
		if (event.getDamager().getType().equals(EntityType.ARROW))
		{
			Arrow arrow = (Arrow) event.getDamager();
			ProjectileSource projectileSource = arrow.getShooter();
			if (projectileSource instanceof Player)
			{
				dmgr = (BadblockPlayer) projectileSource;
			}
		}
		else
		{
			dmgr = (BadblockPlayer) event.getDamager();
		}
		
		if (!PvPBoxMapProtector.damageCheck(dmgr, (BadblockPlayer) event.getEntity()))
		{
			event.setCancelled(true);
		}
	}
	

}
