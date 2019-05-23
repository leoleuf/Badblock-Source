package fr.badblock.bukkit.games.spaceballs.rockets;

import org.bukkit.Location;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class RocketRectileDirection extends BukkitRunnable {
	private Projectile projectile;
	private Location   firstLoc;
	private Vector 	   firstVelocity;
	private int 	   range;

	public RocketRectileDirection(Projectile projectile, int range){
		this.projectile    = projectile;
		this.firstLoc      = projectile.getLocation();
		this.firstVelocity = projectile.getVelocity();
		this.range		   = range;
	}

	public void run(){
		if(projectile.isDead() || projectile.getLocation().getY() < 0 || firstLoc.distance(projectile.getLocation()) > range){
			cancel();
		} else projectile.setVelocity(firstVelocity);
	}
}