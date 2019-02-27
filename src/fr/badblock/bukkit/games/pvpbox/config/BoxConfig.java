package fr.badblock.bukkit.games.pvpbox.config;

import java.io.File;

import fr.badblock.bukkit.games.pvpbox.PvPBox;
import fr.badblock.bukkit.games.pvpbox.PvPBoxMapProtector;
import fr.badblock.bukkit.games.pvpbox.inventories.InventoriesLoader;
import fr.badblock.bukkit.games.pvpbox.kits.KitManager;
import fr.badblock.bukkit.games.pvpbox.totems.TotemManager;
import fr.badblock.bukkit.games.pvpbox.utils.JsonUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Data
public class BoxConfig
{

	private String					fallbackServer;
	private BoxLocation		spawnLocation;
	private BoxCuboid			spawnCuboid;
	
	private BoxCuboid		arenaCuboid;
	private long					antiSpawnKillTime;
	private long					spawnCommandTime;
	private long					dataLoaderThreads;
	
	public static void reload(PvPBox pvpbox)
	{
		// Load config
		File configFile = new File(pvpbox.getDataFolder(), "config.json");
		pvpbox.setBoxConfig(JsonUtils.load(configFile, BoxConfig.class));
		
		// Load kits
		KitManager.load();
		
		// Spawn cuboid
		pvpbox.getBoxConfig().getSpawnCuboid().buildCuboid();
		pvpbox.getBoxConfig().getArenaCuboid().buildCuboid();
		pvpbox.getBoxConfig().getSpawnLocation().buildLocation();
		
		// Load totems
		TotemManager.load();
		
		// Load inventories
		InventoriesLoader.loadInventories(pvpbox);
		
		// API
		pvpbox.getAPI().getJoinItems().registerLeaveItem(8, pvpbox.getBoxConfig().getFallbackServer());
		
		// Set map protector
		pvpbox.getAPI().setMapProtector(new PvPBoxMapProtector());
	}
	
}