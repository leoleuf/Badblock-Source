package fr.badblock.bukkit.games.rush.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import fr.badblock.bukkit.games.rush.entities.RushTeamData;
import fr.badblock.bukkit.games.rush.inventories.npc.MapInventoryNPC;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.configuration.BadConfiguration;
import fr.badblock.gameapi.configuration.values.MapBoolean;
import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.configuration.values.MapMaterial;
import fr.badblock.gameapi.configuration.values.MapNumber;
import fr.badblock.gameapi.configuration.values.MapSelection;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.selections.CuboidSelection;
import lombok.Data;

@Data
public class RushMapConfiguration {
	private int				  time;
	private int				  dimension;
	
	/*
	 * Pour emp�cher les joueurs de sortir
	 */
	private CuboidSelection   mapBounds;
	
	/*
	 * Spawn pour les joueurs morts ou arrivant apr�s le d�but
	 */
	private Location 		  spawnLocation;
	
	/*
	 * Les blocs pouvant �tre pos�s ou cass�s dans la map
	 */
	private List<MapMaterial> breakableBlocks;
	
	/**
	 * La map est autoris� en arcs
	 */
	private Boolean			  allowBows;
	
	private BadConfiguration  config;
	private List<MapInventoryNPC> inventoryNPC = new ArrayList<>();
	
	public RushMapConfiguration(BadConfiguration config){
		this.config = config;
		
		time			= config.getValue("time", MapNumber.class, new MapNumber(2000)).getHandle().intValue();
		dimension		= config.getValue("dimension", MapNumber.class, new MapNumber(0)).getHandle().intValue();
		mapBounds 	    = config.getValue("mapBounds", MapSelection.class, new MapSelection()).getHandle();
		spawnLocation   = config.getValue("spawnLocation", MapLocation.class, new MapLocation()).getHandle();
		inventoryNPC = config.getValueList("inventoryNPC", MapInventoryNPC.class, Arrays.asList());
		allowBows		= config.getValue("allowBows", MapBoolean.class, new MapBoolean(true)).getHandle();
		breakableBlocks = config.getValueList("breakableBlocks", MapMaterial.class, Arrays.asList(new MapMaterial(Material.DIAMOND, (short) 1)));
	
		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			team.teamData(RushTeamData.class).load(config.getSection(team.getKey()));
		}
	}
	
	public void save(File file){
		config.save(file);
	}
}
