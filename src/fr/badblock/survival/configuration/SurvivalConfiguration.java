package fr.badblock.survival.configuration;

import fr.badblock.gameapi.configuration.values.MapLocation;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SurvivalConfiguration {
	public String 	   		   fallbackServer   = "lobby";
	public int    	   		   maxPlayers		= 24;
	public MapLocation 		   spawn;
	
	public int				   addedCompassProb = 50;
}
