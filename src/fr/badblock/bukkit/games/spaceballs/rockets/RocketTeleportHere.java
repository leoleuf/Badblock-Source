package fr.badblock.bukkit.games.spaceballs.rockets;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import fr.badblock.gameapi.players.BadblockPlayer;

public class RocketTeleportHere implements Rocket {
	@Override
	public int getRange() {
		return 30;
	}

	@Override
	public String getName() {
		return "teleporthere";
	}

	@Override
	public void impact(BadblockPlayer launcher, Block block) {
		BadblockPlayer with = getNearbiestPlayer(block.getLocation(), 3.0d, launcher);

		if(with == null){
			messageNoTarget(launcher);
		} else teleportHere(launcher, with);
	}

	@Override
	public void impact(BadblockPlayer launcher, Entity entity) {
		if(entity instanceof BadblockPlayer)
			teleportHere(launcher, (BadblockPlayer) entity);
		
		impact(launcher, entity.getLocation().getBlock());
	}
	
	protected void teleportHere(BadblockPlayer launcher, BadblockPlayer with){
		with.teleport(launcher);

		Vector prevVeloc = with.getVelocity().clone();
		with.damage(0.0d, launcher);
		with.setVelocity(prevVeloc);
		
		launcher.sendTranslatedTitle("spaceballs.rockets.teleporthere.teleported", with.getName());
		with.sendTranslatedTitle("spaceballs.rockets.teleporthere.teleported-to", launcher.getName());
	}
}
