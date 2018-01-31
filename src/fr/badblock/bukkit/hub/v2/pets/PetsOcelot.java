package fr.badblock.bukkit.hub.v2.pets;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Ocelot;
import fr.badblock.gameapi.players.BadblockPlayer;

public class PetsOcelot extends PetsFollowSystem{
	
	
	public void deploy(BadblockPlayer player) {
		LivingEntity entity = player.getWorld().spawn(player.getLocation(), Ocelot.class);
		followPlayer(player, entity, 1);
	}
	
	@SuppressWarnings("unlikely-arg-type")
	public void undeploy(BadblockPlayer player) {
		//NOT FOUND for removing it correctly
		player.getWorld().getLivingEntities().remove(PetsFollowSystem.task);
	}

}
