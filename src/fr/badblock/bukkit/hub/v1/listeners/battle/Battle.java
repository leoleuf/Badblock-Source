package fr.badblock.bukkit.hub.v1.listeners.battle;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.ConfigUtils;

public class Battle {

	public static List<BadblockPlayer> players 	   	   = new ArrayList<>();
	public static List<BattleMob> 	   battleMobs  	   = new ArrayList<>();
	public static Entity			   entity          = null;
	public static int			   	   id 		   	   = 0;
	public static List<Location>	   battleLocations = new ArrayList<>();
	public static List<BattleKitItem>  battleItems	   = new ArrayList<>();
	
	@SuppressWarnings("deprecation")
	public static void load() {
		BadBlockHub hub = BadBlockHub.getInstance();
		FileConfiguration config = hub.getConfig();
		config.getConfigurationSection("battle.mobs").getKeys(false).forEach(key -> {
			ConfigurationSection configurationSection = config.getConfigurationSection("battle.mobs." + key);
			EntityType et = EntityType.fromName(configurationSection.getString("type"));
			Location spawnLocation = ConfigUtils.getLocation(hub, "battle.mobs." + key + ".location");
			battleMobs.add(new BattleMob(spawnLocation, et));
		});
		config.getConfigurationSection("battle.items").getKeys(false).forEach(key -> battleItems.add(new BattleKitItem(config.getConfigurationSection("battle.items." + key))));
		config.getStringList("battle.teleportLocations").forEach(teleportLocation -> battleLocations.add(ConfigUtils.convertStringToLocation(teleportLocation)));
		// Shuffle
		long randomSeed = System.nanoTime();
		Collections.shuffle(battleMobs, new Random(randomSeed));
		spawn(battleMobs.get(0));
	}
	
	public static void npcClick(BadblockPlayer player) {
		//long alivePlayers = players.parallelStream().filter(playerZ -> playerZ.isOnline() && playerZ.isValid() && BadBlockHub.getInstance().getBattleArena().isInSelection(playerZ)).count();
		/*if (alivePlayers >= BadBlockHub.getInstance().getBattlePlayersLimit() && !player.hasPermission("hub.vip.bypassbattleplayerslimit")) {
			player.sendTranslatedMessage("hub.battle.oversized", alivePlayers, BadBlockHub.getInstance().getBattlePlayersLimit());
			return;
		}*/
		if (battleLocations.isEmpty()) {
			player.sendTranslatedMessage("hub.battle.noteleportlocation");
			return;	
		}
		player.clearInventory();
		Battle.battleItems.forEach(battleKitItem -> battleKitItem.give(player));
		player.teleport(battleLocations.get(new SecureRandom().nextInt(battleLocations.size())));
		player.sendTranslatedMessage("hub.battle.youhavebeenteleported");
	}
	
	public static void spawn(BattleMob battleMob) {
		// Null prevention
		if (battleMob == null) {
			id = 0;
			return;
		}
		entity = battleMob.getLocation().getWorld().spawnEntity(battleMob.getLocation(), battleMob.getEntityType());
		id++;
	}
	
}
