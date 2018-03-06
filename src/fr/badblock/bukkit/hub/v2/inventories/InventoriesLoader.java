package fr.badblock.bukkit.hub.v2.inventories;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;

import fr.badblock.bukkit.hub.v2.BadBlockHub;
import fr.badblock.bukkit.hub.v2.inventories.objects.InventoriesConfig;
import fr.badblock.bukkit.hub.v2.inventories.objects.InventoryObject;
import fr.badblock.bukkit.hub.v2.players.HubPlayer;
import fr.badblock.bukkit.hub.v2.utils.JsonFile;
import fr.badblock.gameapi.BadblockPlugin;
import lombok.Getter;

public class InventoriesLoader {

	@Getter private static InventoriesConfig			config		= null;
	@Getter private static Map<String, InventoryObject> inventories = new HashMap<>();
	
	public static void loadInventories(BadblockPlugin plugin)
	{
		File pluginFolder = plugin.getDataFolder();
		// Gestion de la configuration niventories.json
		File inventoriesConfig = new File(pluginFolder, "inventories.json");
		if (!inventoriesConfig.exists())
		{
			try
			{
				inventoriesConfig.createNewFile();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			config = new InventoriesConfig();
			JsonFile.saveFile(inventoriesConfig, config);
			BadBlockHub.log("Loaded new inventory configuration!");
		}
		else
		{
			config = JsonFile.getFile(inventoriesConfig, InventoriesConfig.class);
			BadBlockHub.log("Loaded inventory configuration!");
		}
		// Gestion des dossiers d'inventaires
		File inventoriesFolder = new File(pluginFolder, "inventories");
		if (!inventoriesFolder.exists())
			inventoriesFolder.mkdirs();
		File[] inventoryFiles = inventoriesFolder.listFiles(File::isFile);
		if (inventoryFiles == null) return;
		Arrays.asList(inventoryFiles).forEach(file -> {
			String name = FilenameUtils.removeExtension(file.getName());
			InventoryObject inventoryObject = JsonFile.getFile(file, InventoryObject.class);
			inventories.put(name, inventoryObject);
			BadBlockHub.log("Loaded inventory configuration: '" + name + "'");
		});
	}
	
	public static void reloadInventories(BadblockPlugin plugin)
	{
		getInventories().clear();
		loadInventories(plugin);
		HubPlayer.getPlayers().forEach(hubPlayer -> hubPlayer.giveDefaultInventory());
	}

	public static InventoryObject getDefaultInventory()
	{
		return getInventory(getConfig().getJoinDefaultInventory());
	}
	
	public static InventoryObject getInventory(String name)
	{
		return inventories.get(name);
	}
	
}
