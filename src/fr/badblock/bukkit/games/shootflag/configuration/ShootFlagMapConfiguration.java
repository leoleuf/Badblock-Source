package fr.badblock.bukkit.games.shootflag.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import fr.badblock.bukkit.games.shootflag.entities.ShootFlagTeamData;
import fr.badblock.bukkit.games.shootflag.flags.Flag;
import fr.badblock.bukkit.games.shootflag.flags.MapFlag;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.configuration.BadConfiguration;
import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.configuration.values.MapNumber;
import fr.badblock.gameapi.players.BadblockTeam;
import lombok.Data;

@Data
public class ShootFlagMapConfiguration
{
	
	private int				  time;
	private int				  dimension;
	private Location		  spawnLocation;
	
	/**
	 * Spawn
	 */
	private List<Location> 	  respawnLocations;
	
	private List<Flag> 	  	  flags;
	
	private BadConfiguration  config;
	
	public ShootFlagMapConfiguration(BadConfiguration config)
	{
		this.config = config;
		
		time			= config.getValue("time", MapNumber.class, new MapNumber(2000)).getHandle().intValue();
		dimension		= config.getValue("dimension", MapNumber.class, new MapNumber(0)).getHandle().intValue();
		spawnLocation   = config.getValue("spawnLocation", MapLocation.class, new MapLocation()).getHandle();

		if (Double.isNaN(time))
		{
			time = 2000;
		}
		if (Double.isNaN(dimension))
		{
			dimension = 0;
		}
		
		respawnLocations	= config.getValueList("respawnLocations", MapLocation.class, new ArrayList<>()).getHandle();
		flags 				= config.getValueList("flags", MapFlag.class, new ArrayList<>()).getHandle();
		
		if (flags == null)
		{
			flags = new ArrayList<>();
		}
		
		for(BadblockTeam team : GameAPI.getAPI().getTeams())
		{
			team.teamData(ShootFlagTeamData.class).load(config.getSection(team.getKey()));
		}
	}
	
	public void save(File file){
		if (Double.isNaN(time))
		{
			time = 2000;
		}
		if (spawnLocation == null)
		{
			spawnLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
		}
		if (flags == null)
		{
			flags = new ArrayList<>();
		}
		config.setValue("time", new MapNumber(time));
		config.setValue("dimension", new MapNumber(0));
		config.setValue("spawnLocation", new MapLocation(spawnLocation));
		config.setValueList("respawnLocations", MapLocation.toMapList(respawnLocations));
		config.setValueList("flags", MapFlag.toMapList(flags));
		config.save(file);
	}
}
