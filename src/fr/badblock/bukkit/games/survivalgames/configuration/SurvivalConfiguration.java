package fr.badblock.bukkit.games.survivalgames.configuration;

import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.configuration.values.MapSelection;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SurvivalConfiguration {
	public String 	   		   fallbackServer   = "lobby";
	public int				   minPlayers		= 10;
	public int    	   		   maxPlayers		= 24;
	public MapLocation 		   spawn;
	
	public int				   addedCompassProb = 50;
	public MapSelection		   zombieGame	    = new MapSelection();
}
