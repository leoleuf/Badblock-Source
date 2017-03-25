package fr.badblock.bukkit.games.spaceballs.rockets;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import fr.badblock.gameapi.players.BadblockPlayer;

public class RocketTeleport implements Rocket {
	@Override
	public int getRange() {
		return 30;
	}

	@Override
	public String getName() {
		return "teleport";
	}

	@Override
	public void impact(BadblockPlayer launcher, Block block) {
		teleport(launcher, block.getLocation());
	}

	@Override
	public void impact(BadblockPlayer launcher, Entity entity) {
		teleport(launcher, entity.getLocation());
	}
	
	protected void teleport(BadblockPlayer launcher, Location location){
		location.setDirection(launcher.getLocation().getDirection());
		
		launcher.teleport(location);
		launcher.sendTranslatedTitle("spaceballs.rockets.teleport.teleported");
	}
}
