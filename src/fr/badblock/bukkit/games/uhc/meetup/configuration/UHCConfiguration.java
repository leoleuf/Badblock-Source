package fr.badblock.bukkit.games.uhc.meetup.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;

import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.configuration.values.MapSelection;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UHCConfiguration {
	public String 	   		   fallbackServer    = "lobby";
	public int    	   		   maxPlayersInTeam  = 4;
	public boolean			   allowTeams		 = true;
	public MapConfig		   map				 = new MapConfig();
	public TimeConfig		   time				 = new TimeConfig();
	public MapLocation 		   spawn;
	public MapSelection		   spawnZone		 = new MapSelection();
	public Map<String, List<MapCustomMaterial>> randomBreaks = new HashMap<>();
	public Map<String, List<MapCustomMaterial>> randomMobDrops = new HashMap<>();
	public List<MapCustomRecipe> recipes = new ArrayList<>();
	
	public int				   minPlayers		 = 10;
	public boolean			   manageNether		 = false;

	public World getNether(){
		return Bukkit.getWorld( Bukkit.getWorlds().get(0).getName() + "_nether" );
	}
	
	public class TimeConfig {
		public boolean			   teleportAtPrepEnd = true;
		public int				   totalTime		 = 35;
		public int				   pveTime			 = 1;
		public int				   pvpTime			 = 20;
		public int				   prepTime			 = 20;
	}

	public class MapConfig {
		public int     overworldSize 		= 120;
		public int     overworldSizeAfterTp	= 100;
		public int     netherSize			= 150;
		public boolean manageNether 	    = false;
	}

	@Data
	public class MapCustomMaterial {
		
		public String name;
		public int amount;
		public int probability;
		public int data;
	}

	@Data
	public class MapCustomEnchantment {
		
		public String	enchantment;
		public int			level;
		
		public Enchantment toEnchantment()
		{
			return Enchantment.getByName(enchantment);
		}
		
	}
	
	@Data
	public class MapCustomRecipe {
		
		public String	firstLine;
		public String	secondLine;
		public String	thirdLine;
		public Map<String, String> identifiers;
		public MapCustomRecipeResult result;
		
	}

	
	@Data
	public class MapCustomRecipeResult {
		
		public String name;
		public int amount;
		public int data;
		public MapCustomEnchantment[] enchantments;
		
	}
}
