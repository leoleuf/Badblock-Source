package fr.badblock.spaceballs.configuration;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.configuration.BadConfiguration;
import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.configuration.values.MapMaterial;
import fr.badblock.gameapi.configuration.values.MapNumber;
import fr.badblock.gameapi.configuration.values.MapSelection;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.selections.CuboidSelection;
import fr.badblock.spaceballs.entities.SpaceTeamData;
import lombok.Data;

@Data
public class SpaceMapConfiguration {
	private int				  time;
	private int				  dimension;
	
	/*
	 * Pour empêcher les joueurs de sortir
	 */
	private CuboidSelection   mapBounds;
	
	private CuboidSelection   towerBounds;
	
	/*
	 * Spawn pour les joueurs morts ou arrivant après le début
	 */
	private Location 		  spawnLocation;
	
	/*
	 * Les blocs ne pouvant être posés ou cassés dans la map
	 */
	private List<MapMaterial> unbreakableBlocks;
	
	private BadConfiguration  config;
	
	public SpaceMapConfiguration(BadConfiguration config){
		this.config = config;
		
		time			  = config.getValue("time", MapNumber.class, new MapNumber(2000)).getHandle().intValue();
		dimension		  = config.getValue("dimension", MapNumber.class, new MapNumber(0)).getHandle().intValue();
		mapBounds 	      = config.getValue("mapBounds", MapSelection.class, new MapSelection()).getHandle();
		towerBounds 	  = config.getValue("towerBounds", MapSelection.class, new MapSelection()).getHandle();
		spawnLocation     = config.getValue("spawnLocation", MapLocation.class, new MapLocation()).getHandle();
		unbreakableBlocks = config.getValueList("unbreakableBlocks", MapMaterial.class, Arrays.asList(new MapMaterial(Material.DIAMOND_BLOCK, (short) 0)));
	
		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			team.teamData(SpaceTeamData.class).load(config.getSection(team.getKey()));
		}
	}
	
	public void save(File file){
		config.save(file);
	}
}
