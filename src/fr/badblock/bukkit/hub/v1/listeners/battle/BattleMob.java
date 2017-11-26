package fr.badblock.bukkit.hub.v1.listeners.battle;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class BattleMob {

	public Location   location;
	public EntityType entityType;
	
}
