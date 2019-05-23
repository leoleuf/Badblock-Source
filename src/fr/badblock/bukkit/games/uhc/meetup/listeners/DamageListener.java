package fr.badblock.bukkit.games.uhc.meetup.listeners;

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
import org.bukkit.projectiles.ProjectileSource;

import fr.badblock.bukkit.games.uhc.meetup.players.UHCData;
import fr.badblock.bukkit.games.uhc.meetup.runnables.game.GameRunnable;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;

public class DamageListener extends BadListener  {
	@EventHandler(ignoreCancelled=true)
	public void onDamageNormal(EntityDamageEvent e){
		if(inGame() && e.getEntityType() == EntityType.PLAYER && !GameRunnable.pve && e.getCause() != DamageCause.ENTITY_ATTACK){
			e.setCancelled(true);
		}

		if(e.getEntityType() == EntityType.PLAYER && e.getCause() != DamageCause.ENTITY_ATTACK){
			BadblockPlayer player = (BadblockPlayer) e.getEntity();

			player.inGameData(UHCData.class).receivedDamage += e.getFinalDamage();
		}
	}
	
	@EventHandler(ignoreCancelled=true,priority=EventPriority.HIGHEST)
	public void onDamageNormal(EntityDamageByEntityEvent e){
		if(inGame() && e.getEntityType() == EntityType.PLAYER && !GameRunnable.pve &&
				(e.getDamager().getType() == EntityType.PLAYER ||
				(e.getDamager() != null && e.getDamager() instanceof Projectile && ((Projectile)e.getDamager()).getShooter() != null &&
				((Projectile)e.getDamager()).getShooter() instanceof Player && !GameRunnable.pve))) {
			e.setCancelled(true);
		}


		if(e.getEntityType() == EntityType.PLAYER){
			BadblockPlayer player = (BadblockPlayer) e.getEntity();
			ProjectileSource projectileSource = null;
			if (e.getDamager() instanceof Projectile) {
				Projectile projectile = (Projectile) e.getDamager();
				projectileSource = projectile.getShooter();
			}
			if (projectileSource != null && projectileSource instanceof Player) 
				e.setCancelled(!inGame() || !GameRunnable.pve);
			player.inGameData(UHCData.class).receivedDamage += e.getFinalDamage();
		}

		if(GameRunnable.pve){
			BadblockPlayer damager = asPlayer(e.getDamager());

			if(damager != null){
				damager.inGameData(UHCData.class).givedDamage += e.getFinalDamage();
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
		if(e.getRegainReason() == RegainReason.SATIATED && inGame() && e.getEntityType() == EntityType.PLAYER){
			e.setCancelled(true);
		}
	}
}
