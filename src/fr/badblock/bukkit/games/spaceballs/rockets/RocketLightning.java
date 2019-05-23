package fr.badblock.bukkit.games.spaceballs.rockets;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import fr.badblock.gameapi.players.BadblockPlayer;

public class RocketLightning implements Rocket {
	@Override
	public int getRange() {
		return 30;
	}

	@Override
	public String getName() {
		return "lightning";
	}

	@Override
	public void impact(BadblockPlayer launcher, Block block) {
		BadblockPlayer with = getNearbiestPlayer(block.getLocation(), 3.0d, launcher);
		
		if(with == null){
			lightning(block.getLocation());
		} else lightning(launcher, with);
	}

	@Override
	public void impact(BadblockPlayer launcher, Entity entity) {
		if(entity instanceof BadblockPlayer){
			BadblockPlayer with = (BadblockPlayer) entity;
			
			if(test(with, launcher) != null){
				lightning(launcher, (BadblockPlayer) entity);
				return;
			}
		}
		
		impact(launcher, entity.getLocation().getBlock());
	}
	
	protected BadblockPlayer test(BadblockPlayer player, BadblockPlayer launcher){
		if(player != null && player.getTeam() != null && player.getTeam().equals(launcher.getTeam()))
			return null;
		
		return player;
	}
	
	protected void lightning(BadblockPlayer launcher, BadblockPlayer with){
		with.getWorld().strikeLightningEffect(with.getLocation());
		with.damage(2000.0d, launcher);
	}
	
	protected void lightning(Location location){
		location.getWorld().strikeLightningEffect(location);
	}
}
