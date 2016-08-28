package fr.badblock.survival.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;

import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.survival.runnables.game.DeathmatchRunnable;
import fr.badblock.survival.runnables.game.PvERunnable;
import fr.badblock.survival.runnables.game.PvPRunnable;
import net.md_5.bungee.api.ChatColor;

public class DamageListener extends BadListener  {
	@EventHandler(ignoreCancelled=true)
	public void onDamageNormal(EntityDamageEvent e){
		System.out.println(e.isCancelled());

		if(inGame() && e.getEntityType() == EntityType.PLAYER && !PvERunnable.pve && e.getCause() != DamageCause.ENTITY_ATTACK){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent e){
		System.out.println(e.isCancelled());
	}
	
	@EventHandler(ignoreCancelled=true,priority=EventPriority.HIGHEST)
	public void onDamageNormal(EntityDamageByEntityEvent e){
		System.out.println(e.isCancelled());

		if(inGame() && e.getEntityType() == EntityType.PLAYER && !PvPRunnable.pvp){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDamage(EntityDamageByEntityEvent e){
		System.out.println(e.isCancelled());
	}
	
	@EventHandler(priority=EventPriority.MONITOR,ignoreCancelled=true)
	public void onDamageMonitor(EntityDamageEvent e){
		if(e.getEntityType() == EntityType.PLAYER && e.getDamage() >= 1.0d){
			BadblockPlayer player = (BadblockPlayer) e.getEntity();
			
			for(Entity entity : player.getNearbyEntities(16.0d, 16.0d, 16.0d)){
				if(entity.getType() == EntityType.PLAYER){
					BadblockPlayer viewer = (BadblockPlayer) entity;
					viewer.showFloatingText(ChatColor.RED + "-" + (int) e.getDamage(), player.getLocation().add(0, 2.3, 0), 10, 0.1d);
				}
			}
		}
	}
	
	@EventHandler
	public void onRegainHealth(EntityRegainHealthEvent e){
		if(e.getRegainReason() == RegainReason.SATIATED && inGame() && e.getEntityType() == EntityType.PLAYER && DeathmatchRunnable.deathmatch){
			e.setCancelled(true);
		}
	}
}
