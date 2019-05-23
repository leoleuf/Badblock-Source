package fr.badblock.bukkit.games.spaceballs.rockets;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Projectile;

import fr.badblock.gameapi.players.BadblockPlayer;

public class RocketExplosive implements Rocket {
	private Random random = new Random();
	
	@Override
	public int getRange() {
		return 30;
	}

	@Override
	public String getName() {
		return "explosive";
	}

	@Override
	public void impact(BadblockPlayer launcher, Block block) {
	
	}

	@Override
	public void impact(BadblockPlayer launcher, Entity entity) {

	}
	
	protected void createExplosion(Location location){
		
	}
	
	@Override
	public Class<? extends Projectile> getProjectileClass(){
		return Fireball.class;
	}
	
	@Override
	public void customize(Projectile projectile){
		Fireball fb = (Fireball) projectile;
		
		fb.setIsIncendiary(true); // mouhaha :o
		fb.setYield(random.nextInt(3) + 4);
	}
}
