package fr.badblock.survival.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.survival.players.SurvivalData;
import fr.badblock.survival.runnables.game.DeathmatchRunnable;
import fr.badblock.survival.runnables.game.PvERunnable;
import fr.badblock.survival.runnables.game.PvPRunnable;

public class DamageListener extends BadListener  {
	@EventHandler(ignoreCancelled=true)
	public void onDamageNormal(EntityDamageEvent e){
		if(inGame() && e.getEntityType() == EntityType.PLAYER && !PvERunnable.pve && e.getCause() != DamageCause.ENTITY_ATTACK){
			e.setCancelled(true);
		}
		
		if(PvERunnable.pve && e.getEntityType() == EntityType.PLAYER && e.getCause() != DamageCause.ENTITY_ATTACK){
			BadblockPlayer player = (BadblockPlayer) e.getEntity();
			
			player.inGameData(SurvivalData.class).receivedDamage += e.getFinalDamage();
		}
	}
	
	@EventHandler(ignoreCancelled=true,priority=EventPriority.HIGHEST)
	public void onDamageNormal(EntityDamageByEntityEvent e){
		if(inGame() && e.getEntityType() == EntityType.PLAYER && !PvPRunnable.pvp){
			e.setCancelled(true);
		}
		
		if(PvPRunnable.pvp && e.getEntityType() == EntityType.PLAYER && e.getCause() != DamageCause.ENTITY_ATTACK){
			BadblockPlayer player = (BadblockPlayer) e.getEntity();
			
			player.inGameData(SurvivalData.class).receivedDamage += e.getFinalDamage();
		
			BadblockPlayer damager = asPlayer(e.getDamager());
			
			if(damager != null){
				damager.inGameData(SurvivalData.class).givedDamage += e.getFinalDamage();
			}
		}
	}
	
	private BadblockPlayer asPlayer(Entity e){
		if(e instanceof Player){
			return (BadblockPlayer) e;
		} else if(e instanceof Projectile){
			Projectile proj = (Projectile) e;
			
			if(proj.getShooter() instanceof Player)
				return (BadblockPlayer) proj.getShooter();
		}
		
		return null;
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
