package fr.badblock.bukkit.games.bedwars.configuration;

import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.configuration.BadConfiguration;
import fr.badblock.gameapi.configuration.values.*;
import fr.badblock.gameapi.utils.selections.CuboidSelection;
import lombok.Data;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.File;
import java.util.Collections;
import java.util.List;

@Data
public class BedWarsMapConfiguration {
	private int	time;
	private int dimension;
	private CuboidSelection mapBounds;
	private Location spawnLocation;
	private List<MapMaterial> breakableBlocks;
	private Boolean	allowBows;
	private BadConfiguration config;
	
	public BedWarsMapConfiguration(BadConfiguration config){
		this.config = config;
		time = config.getValue("time", MapNumber.class, new MapNumber(2000)).getHandle().intValue();
		dimension = config.getValue("dimension", MapNumber.class, new MapNumber(0)).getHandle().intValue();
		mapBounds = config.getValue("mapBounds", MapSelection.class, new MapSelection()).getHandle();
		spawnLocation = config.getValue("spawnLocation", MapLocation.class, new MapLocation()).getHandle();
		allowBows = config.getValue("allowBows", MapBoolean.class, new MapBoolean(true)).getHandle();
		breakableBlocks = config.getValueList("breakableBlocks", MapMaterial.class, Collections.singletonList(new MapMaterial(Material.DIAMOND, (short) 1)));
        GameAPI.getAPI().getTeams().forEach(t -> t.teamData(BedWarsTeamData.class).load(config.getSection(t.getKey())));
	}
	
	public void save(File file){
		config.save(file);
	}
}
