package fr.badblock.spaceballs.configuration;

import fr.badblock.gameapi.configuration.values.MapLocation;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SpaceConfiguration {
	public String 	   		   fallbackServer   = "lobby";
	public String			   defaultKit		= "defaultkit";
	public int    	   		   maxPlayersInTeam = 4;
	public MapLocation 		   spawn;
}
