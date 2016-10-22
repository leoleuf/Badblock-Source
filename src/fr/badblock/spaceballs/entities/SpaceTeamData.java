package fr.badblock.spaceballs.entities;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.configuration.BadConfiguration;
import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.configuration.values.MapSelection;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.data.TeamData;
import fr.badblock.gameapi.utils.selections.CuboidSelection;
import fr.badblock.spaceballs.players.SpaceData;
import lombok.Getter;

public class SpaceTeamData implements TeamData {
	@Getter 	   private List<Location>	  chests;
	@Getter 	   private List<Location> 	  itemSpawnLocations;
	@Getter		   private CuboidSelection	  spawnSelection;
	@Getter 	   private Location 	   	  respawnLocation;

	private int diamonds;
	
	public void load(BadConfiguration config){
		chests		 	    = config.getValueList("chests", MapLocation.class, new ArrayList<>()).getHandle();
		itemSpawnLocations  = config.getValueList("itemSpawnLocations", MapLocation.class, new ArrayList<>()).getHandle();
		respawnLocation     = config.getValue("respawnLocation", MapLocation.class, new MapLocation()).getHandle();
		spawnSelection	    = config.getValue("spawnSelection", MapSelection.class, new MapSelection()).getHandle();
	
		spawnSelection.getFirstBound().setY(0.0d);
		spawnSelection.getSecondBound().setY(256.0d);
	}
	
	public void save(BadConfiguration config){
		config.setValueList("chests", MapLocation.toMapList(chests));
		config.setValueList("itemSpawnLocations", MapLocation.toMapList(itemSpawnLocations));
		config.setValue("respawnLocation", new MapLocation(respawnLocation));
		config.setValue("spawnSelection", new MapSelection(spawnSelection));
	}
	
	public int getDiamondsCount(){
		return diamonds;
	}
	
	public void putDiamond(BadblockPlayer player, Block block){
		boolean can = false;
		
		for(Location location : chests){
			if(location.getBlock().getLocation().distance(block.getLocation()) <= 1){
				can = true;
				break;
			}
		}
		
		if(can){
			int count = player.countItems(Material.DIAMOND, (byte) 0);
			
			if(count == 0){
				player.sendTranslatedMessage("spaceballs.diamond.donthave");
			} else {
				player.removeItems(Material.DIAMOND, (byte) 0, -1);
				diamonds += count;
				
				player.inGameData(SpaceData.class).launchRocket(player);
				
				GameAPI.getAPI().getOnlinePlayers().forEach(p -> {
					p.sendTranslatedMessage("spaceballs.diamond.put", player.getTeam().getChatName(), player.getName(), count );
					p.getCustomObjective().generate();
				});
			}
		}
	}
}
