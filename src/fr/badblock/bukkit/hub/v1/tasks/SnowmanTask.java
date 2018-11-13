package fr.badblock.bukkit.hub.v1.tasks;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.utils.ConfigUtils;
import fr.badblock.gameapi.utils.entities.CustomCreature;
import fr.badblock.gameapi.utils.entities.CustomCreature.CreatureFlag;
import fr.badblock.gameapi.utils.entities.CustomCreature.TargetType;

public class SnowmanTask extends CustomTask {

	private List<CustomCreature> creatures;
	private int					 max 		= 16;

	public SnowmanTask() {
		super(0, 20 * 30, true);
	}

	@Override
	public void done() {
		if (creatures == null) creatures = new ArrayList<>();
		List<Location> l = ConfigUtils.getLocationList(BadBlockHub.getInstance(), "christmas_creepers");
		long count = creatures.stream().filter(creature -> creature.getBukkit().isValid()).count();
		if (count < max) {
			Location loc = l.get(new SecureRandom().nextInt(l.size()));
			CustomCreature customCreature = GameAPI.getAPI().spawnCustomEntity(loc, EntityType.SNOWMAN);
			customCreature.addTargetable(EntityType.PLAYER, TargetType.NEAREST);
			customCreature.addCreatureFlag(CreatureFlag.INVINCIBLE);
			creatures.add(customCreature);
		}
	}

}
