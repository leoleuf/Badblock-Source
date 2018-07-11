package fr.badblock.bukkit.games.bedwars.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;

import fr.badblock.gameapi.configuration.values.MapLocation;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BedWarsConfiguration {
	public String 	   		   fallbackServer   = "lobby";
	public String 	   		   defaultKit		= "defaultKit";
	public int    	   		   maxPlayersInTeam = 4;
	public boolean			   enabledAutoTeamManager = false;
	public int				   minPlayersAutoTeam = 1;
	public int				   maxPlayersAutoTeam = 4;
	public int    	   		   minPlayers	    = 4;
	public MapLocation 		   spawn;
	
	public List<MapLocation>   sheeps		    = new ArrayList<>();
	public List<SpawnableItem> items			= Arrays.asList(new SpawnableItem());
	
	public class SpawnableItem {
		public String item  = Material.EMERALD.name();
		public int	  ticks = 60;
	}
}
