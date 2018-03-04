package fr.badblock.bukkit.games.shootflag.configuration;

import org.bukkit.Bukkit;

import fr.badblock.gameapi.configuration.values.MapLocation;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ShootFlagConfiguration {
	
	public String 	   		   fallbackServer   = "lobby";
	public int    	   		   maxPlayersInTeam = 4;
	public int    	   		   minPlayers		= 2;
	public MapLocation 		   spawn			= new MapLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
	
}
