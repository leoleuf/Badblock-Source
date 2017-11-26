package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.chests.objects;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomPlayerInventory;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.chests.PlayerChestsSelectorInventory;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.bukkit.hub.v1.objects.HubStoredPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.ConfigUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data public class ChestLoader {

	@Getter @Setter private static ChestLoader	instance;

	private FileConfiguration 		  config;
	private List<ChestOpener>	  	  openers	= new ArrayList<>();
	private List<CustomChestType>	  chests	= new ArrayList<>();

	@SuppressWarnings("deprecation")
	public ChestLoader(Plugin plugin) {
		setInstance(this);
		File file = new File(plugin.getDataFolder(), "chests.yml");
		setConfig(YamlConfiguration.loadConfiguration(file));
		config.getConfigurationSection("openers").getKeys(false).forEach(key -> {
			ConfigurationSection configurationSection = config.getConfigurationSection("openers." + key);
			Location openerChestLocation = ConfigUtils.convertStringToBlockLocation(configurationSection.getString("openerChestLocation"));
			LinkedHashMap<Location, Location> chestLocations = new LinkedHashMap<>();
			ConfigurationSection chestsSection = config.getConfigurationSection("openers." + key + ".chests");
			chestsSection.getKeys(false).forEach(chestKey -> {
				ConfigurationSection chestSection = config.getConfigurationSection("openers." + key + ".chests." + chestKey);
				Location chestLocation = ConfigUtils.convertStringToBlockLocation(chestSection.getString("chestLocation"));
				Location chestTeleportLocation = ConfigUtils.convertStringToLocation(chestSection.getString("chestTeleportLocation"));
				chestLocations.put(chestLocation, chestTeleportLocation);		
			});
			openers.add(new ChestOpener(openerChestLocation, chestLocations));
		});
		config.getConfigurationSection("chests").getKeys(false).forEach(key -> {
			ConfigurationSection configurationSection = config.getConfigurationSection("chests." + key);
			int id = configurationSection.getInt("id");
			Material material = Material.getMaterial(configurationSection.getInt("material"));
			byte data = (byte) configurationSection.getInt("data");
			Map<String, Long> winRates = new HashMap<>();
			for (String winRate : configurationSection.getStringList("winRates")) {
				String[] splitter = winRate.split(":");
				String winName = splitter[0];
				long rarity = Long.parseLong(splitter[1]);
				winRates.put(winName, rarity);
			}
			chests.add(new CustomChestType(id, new ItemStack(material, data), configurationSection.getLong("giveEachSeconds", -1), winRates));
		});
	}

	public void open(BadblockPlayer player, ChestOpener chestOpener) {
		HubStoredPlayer hubStoredPlayer = HubStoredPlayer.get(player);
		/*if (!player.hasPermission("hub.chestsaccess")) {
			player.sendTranslatedMessage("hub.items.functionsoon");
			return;
		}*/
		// Aucun chest à ouvrir
		if (hubStoredPlayer.getChests().isEmpty() || hubStoredPlayer.getChests().stream().filter(chest -> !chest.isOpened()).count() == 0) {
			player.sendTranslatedMessage("hub.chests.noopenablechest");
			return;
		}
		// Set le chestopener pour le récupérer après car on peut pas le faire passer dans un arg de l'inventaire
		HubPlayer hubPlayer = HubPlayer.get(player);
		hubPlayer.setChestOpener(chestOpener);
		// Ouverture de l'inventaire
		CustomPlayerInventory.get(PlayerChestsSelectorInventory.class, player).open();
	}

}
