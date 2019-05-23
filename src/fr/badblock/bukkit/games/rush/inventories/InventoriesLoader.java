package fr.badblock.bukkit.games.rush.inventories;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.plugin.Plugin;

import fr.badblock.bukkit.games.rush.PluginRush;
import fr.badblock.bukkit.games.rush.inventories.objects.InventoryItemObject;
import fr.badblock.bukkit.games.rush.inventories.objects.InventoryObject;
import fr.badblock.bukkit.games.rush.inventories.utils.ChatColorUtils;
import fr.badblock.bukkit.games.rush.inventories.utils.JsonFile;
import lombok.Getter;

public class InventoriesLoader {

	@Getter private static Map<String, InventoryObject> inventories = new HashMap<>();
	
	public static void loadInventories(Plugin plugin) {
		File pluginFolder = plugin.getDataFolder();
		// Gestion des dossiers d'inventaires
		File inventoriesFolder = new File(pluginFolder, "inventories");
		if (!inventoriesFolder.exists())
			inventoriesFolder.mkdirs();
		File[] inventoryFiles = inventoriesFolder.listFiles(File::isFile);
		if (inventoryFiles == null) return;
		Arrays.asList(inventoryFiles).forEach(file -> {
			String name = FilenameUtils.removeExtension(file.getName());
			InventoryObject inventoryObject = JsonFile.getFile(file, InventoryObject.class);
			for (InventoryItemObject item : inventoryObject.getItems()) {
				if (item.getName() == null || item.getName().isEmpty()) continue;
				item.setName(ChatColorUtils.translate(item.getName()));
			}
			inventories.put(name, inventoryObject);
			PluginRush.getInstance().getServer().getConsoleSender().sendMessage("Loaded inventory configuration: '" + name + "'");
		});
	}
	
	public static void reloadInventories(Plugin plugin) {
		getInventories().clear();
		loadInventories(plugin);
	}
	
	public static InventoryObject getInventory(String name) {
		return inventories.get(name);
	}
	
}
