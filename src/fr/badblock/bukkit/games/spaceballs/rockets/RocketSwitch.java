package fr.badblock.bukkit.games.spaceballs.rockets;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import fr.badblock.gameapi.players.BadblockPlayer;

public class RocketSwitch implements Rocket {
	@Override
	public int getRange() {
		return 30;
	}

	@Override
	public String getName() {
		return "switch";
	}

	@Override
	public void impact(BadblockPlayer launcher, Block block) {
		BadblockPlayer with = getNearbiestPlayer(block.getLocation(), 3.0d, launcher);

		if(with == null){
			messageNoTarget(launcher);
		} else switchPlayers(launcher, with);
	}

	@Override
	public void impact(BadblockPlayer launcher, Entity entity) {
		if(entity instanceof BadblockPlayer)
			switchPlayers(launcher, (BadblockPlayer) entity);
		else impact(launcher, entity.getLocation().getBlock());
	}
	
	protected void switchPlayers(BadblockPlayer launcher, BadblockPlayer with){
		Location withLoc = with.getLocation().clone();
		
		with.teleport(launcher);

		Vector prevVeloc = with.getVelocity().clone();
		with.damage(0.0d, launcher);
		with.setVelocity(prevVeloc);
		
		launcher.teleport(withLoc);
		
		launcher.sendTranslatedTitle("spaceballs.rockets.switch.teleported", with.getName());
		with.sendTranslatedTitle("spaceballs.rockets.switch.teleported", launcher.getName());
	}
}
