package fr.badblock.bukkit.games.bedwars.entities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import fr.badblock.gameapi.configuration.BadConfiguration;
import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.configuration.values.MapSelection;
import fr.badblock.gameapi.players.data.TeamData;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.selections.CuboidSelection;
import lombok.Getter;
import lombok.Setter;

public class BedWarsTeamData implements TeamData {
	@Getter 	   private Location 	   	  firstBedPart, secondBedPart;
	@Getter 	   private List<Location> 	  itemSpawnLocations;
	@Getter		   private CuboidSelection	  spawnSelection;
	@Getter 	   private Location 	   	  respawnLocation;
	@Getter@Setter private TranslatableString bed						   = new TranslatableString("bedwars.result.teams.bed-keeped");
	private boolean	  hasBed					   = true;
	public  int				  health;
	public int				  resourceSpeedLevel		   = 1;
	public int				  speedMining;
	public int				  speed;
	public int				  strength;
	public int				  protection;
	public int				  heal;
	public long			  slowDig;
	public boolean	      trespassing;

	public void load(BadConfiguration config){
		firstBedPart 	    = config.getValue("bed", MapLocation.class, new MapLocation()).getHandle();
		itemSpawnLocations  = config.getValueList("itemSpawnLocations", MapLocation.class, new ArrayList<>()).getHandle();
		respawnLocation     = config.getValue("respawnLocation", MapLocation.class, new MapLocation()).getHandle();
		spawnSelection	    = config.getValue("spawnSelection", MapSelection.class, new MapSelection()).getHandle();

		resourceSpeedLevel = 1;
		speedMining = 0;

		spawnSelection.getFirstBound().setY(0.0d);
		spawnSelection.getSecondBound().setY(256.0d);
	}

	public void save(BadConfiguration config){
		config.setValue("bed", new MapLocation(firstBedPart));
		config.setValueList("itemSpawnLocations", MapLocation.toMapList(itemSpawnLocations));
		config.setValue("respawnLocation", new MapLocation(respawnLocation));
		config.setValue("spawnSelection", new MapSelection(spawnSelection));
	}

	public boolean hasBed(){
		return hasBed;
	}

	public void broked(boolean explosion, String player){
		firstBedPart  = null;
		secondBedPart = null;

		hasBed = false;
		bed  = new TranslatableString("bedwars.result.teams.bed-" + (explosion ? "explode" : "broked"), player);
	}

	public Location findOtherBedPart(){

		if(firstBedPart == null) return null;

		if(secondBedPart != null)
			return secondBedPart;

		Location bed = firstBedPart.clone();
		Location testLoc = bed.clone().add(1, 0, 0);

		if(testLoc.getBlock().getType() == Material.BED_BLOCK)
			return testLoc;
		testLoc = bed.clone().add(-1, 0, 0);
		if(testLoc.getBlock().getType() == Material.BED_BLOCK)
			return secondBedPart = testLoc;
		testLoc = bed.clone().add(0, 0, 1);
		if(testLoc.getBlock().getType() == Material.BED_BLOCK)
			return secondBedPart = testLoc;
		testLoc = bed.clone().add(0, 0, -1);
		if(testLoc.getBlock().getType() == Material.BED_BLOCK)
			return secondBedPart = testLoc;

		return null; //WTF ? x)

	}
}
