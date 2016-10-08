package fr.badblock.survival.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.badblock.gameapi.BadListener;
import fr.badblock.survival.runnables.game.DeathmatchRunnable;
import fr.badblock.survival.runnables.game.PvERunnable;
import fr.badblock.survival.runnables.game.PvPRunnable;

public class DamageListener extends BadListener  {
	@EventHandler(ignoreCancelled=true)
	public void onDamageNormal(EntityDamageEvent e){
		if(inGame() && e.getEntityType() == EntityType.PLAYER && !PvERunnable.pve && e.getCause() != DamageCause.ENTITY_ATTACK){
			e.setCancelled(true);
		}
	}
	
	@EventHandler(ignoreCancelled=true,priority=EventPriority.HIGHEST)
	public void onDamageNormal(EntityDamageByEntityEvent e){
		if(inGame() && e.getEntityType() == EntityType.PLAYER && !PvPRunnable.pvp){
			e.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent e){
		e.setCancelled(false);
	}
	
	@EventHandler
	public void onRegainHealth(EntityRegainHealthEvent e){
		if(e.getRegainReason() == RegainReason.SATIATED && inGame() && e.getEntityType() == EntityType.PLAYER && DeathmatchRunnable.deathmatch){
			e.setCancelled(true);
		}
	}
}
