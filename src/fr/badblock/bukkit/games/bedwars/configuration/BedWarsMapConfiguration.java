package fr.badblock.bukkit.games.bedwars.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;

import fr.badblock.bukkit.games.bedwars.configuration.breakable.MapInventoryNPC;
import fr.badblock.bukkit.games.bedwars.configuration.floatingtexts.MapFloatingText;
import fr.badblock.bukkit.games.bedwars.configuration.npc.MapBreakableBlock;
import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
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
public class BedWarsMapConfiguration {

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
	
	private List<MapBreakableBlock> blockRotations = new ArrayList<>();

	private List<MapInventoryNPC> inventoryNPC = new ArrayList<>();

	private List<MapFloatingText> floatingTexts = new ArrayList<>();

	private List<MapLocation> spawnDiamonds = new ArrayList<>();
	private List<MapLocation> spawnEmeralds = new ArrayList<>();
	
	private BadConfiguration  config;
	
	public BedWarsMapConfiguration(BadConfiguration config){
		this.config = config;
		
		time			= config.getValue("time", MapNumber.class, new MapNumber(2000)).getHandle().intValue();
		dimension		= config.getValue("dimension", MapNumber.class, new MapNumber(0)).getHandle().intValue();
		mapBounds 	    = config.getValue("mapBounds", MapSelection.class, new MapSelection()).getHandle();
		spawnLocation   = config.getValue("spawnLocation", MapLocation.class, new MapLocation()).getHandle();
		allowBows		= config.getValue("allowBows", MapBoolean.class, new MapBoolean(true)).getHandle();
		breakableBlocks = config.getValueList("breakableBlocks", MapMaterial.class, Arrays.asList(new MapMaterial(Material.DIAMOND, (short) 1)));
		blockRotations = config.getValueList("blockRotations", MapBreakableBlock.class, Arrays.asList());
		inventoryNPC = config.getValueList("inventoryNPC", MapInventoryNPC.class, Arrays.asList());
		floatingTexts = config.getValueList("floatingTexts", MapFloatingText.class, Arrays.asList());
		
		spawnDiamonds = config.getValueList("spawnDiamonds", MapLocation.class, Arrays.asList());
		spawnEmeralds = config.getValueList("spawnEmeralds", MapLocation.class, Arrays.asList());

		System.out.println("Load floating texts : " + floatingTexts.size());
		System.out.println("block rotations: " + blockRotations.size());
		
		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			team.teamData(BedWarsTeamData.class).load(config.getSection(team.getKey()));
		}
	}
	
	public void save(File file){
		config.save(file);
	}
}
