package fr.badblock.bukkit.games.badsoup.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;

import fr.badblock.gameapi.configuration.BadConfiguration;
import fr.badblock.gameapi.configuration.values.MapBoolean;
import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.configuration.values.MapNumber;
import lombok.Data;

@Data
public class SoupMapConfiguration {
	private int				  time;
	private int				  dimension;
	
	/*
	 * Spawn pour les joueurs morts ou arrivant apr�s le d�but
	 */
	private Location 		  spawnLocation;
	private List<Location>	  locations;
	private List<Location>	  deathMatchs;
	private Location		  specDeathmatch;
	private boolean			  withTeam;
	
	private BadConfiguration  config;
	
	public SoupMapConfiguration(BadConfiguration config){
		this.config = config;
		
		time			= config.getValue("time", MapNumber.class, new MapNumber(2000)).getHandle().intValue();
		dimension		= config.getValue("dimension", MapNumber.class, new MapNumber(0)).getHandle().intValue();
		spawnLocation   = config.getValue("spawnLocation", MapLocation.class, new MapLocation()).getHandle();
		locations		= config.getValueList("spawns", MapLocation.class, new ArrayList<>()).getHandle();
		deathMatchs		= config.getValueList("deathMatchs", MapLocation.class, new ArrayList<>()).getHandle();
		specDeathmatch	= config.getValue("specDeathmatch", MapLocation.class, new MapLocation()).getHandle();
		withTeam		= config.getValue("withTeam", MapBoolean.class, new MapBoolean(true)).getHandle();
	}
	
	public void save(File file){
		config.save(file);
	}
}
