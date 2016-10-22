package fr.badblock.spaceballs.rockets;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;

import fr.badblock.gameapi.players.BadblockPlayer;

public class RocketPigmen implements Rocket {
	private Random random = new Random();
	
	@Override
	public int getRange() {
		return 30;
	}

	@Override
	public String getName() {
		return "pigmen";
	}

	@Override
	public void impact(BadblockPlayer launcher, Block block) {
		spawn(launcher, block.getLocation());
	}

	@Override
	public void impact(BadblockPlayer launcher, Entity entity) {
		spawn(launcher, entity.getLocation());
	}
	
	protected void spawn(BadblockPlayer launcher, Location location){
		BadblockPlayer concerned = getNearbiestPlayer(location, 10.0d, launcher);

		int nbr = random.nextInt(2) + 3;
		
		for(int i=0;i<nbr;i++){
			PigZombie pig = (PigZombie) location.getWorld().spawnEntity(location, EntityType.PIG_ZOMBIE);
			pig.setAngry(true);
			
			if(concerned != null) {
				pig.setTarget(concerned);
			}
		}
	}
}
