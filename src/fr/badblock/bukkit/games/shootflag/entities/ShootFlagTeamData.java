package fr.badblock.bukkit.games.shootflag.entities;

import org.bukkit.Location;

import fr.badblock.gameapi.configuration.BadConfiguration;
import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.players.data.TeamData;
import lombok.Getter;
import lombok.Setter;

public class ShootFlagTeamData implements TeamData {
	
	@Getter private Location 	   	  			spawnLocation;
	@Getter@Setter 	private int				  	points;

	public void load(BadConfiguration config){
		spawnLocation = config.getValue("spawnLocation", MapLocation.class, new MapLocation()).getHandle();
	}

	public void save(BadConfiguration config){
		config.setValue("spawnLocation", new MapLocation(spawnLocation));
	}

	public void addPoints(int points)
	{
		setPoints(getPoints() + points);
	}
	
}
