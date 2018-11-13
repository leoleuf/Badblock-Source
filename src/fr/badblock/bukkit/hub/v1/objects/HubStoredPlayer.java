package fr.badblock.bukkit.hub.v1.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.chests.objects.CustomChest;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.settings.defaults.MountConfig;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.data.GameData;
import fr.badblock.gameapi.utils.general.Callback;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HubStoredPlayer implements GameData {

	public static HubStoredPlayer get(BadblockPlayer player) {
		HubStoredPlayer hubStoredPlayer = player.getPlayerData().gameData("hub", HubStoredPlayer.class);
		if (hubStoredPlayer.getProperties() == null)
			hubStoredPlayer.setProperties(new HashSet<>());
		if (hubStoredPlayer.getChests() == null)
			hubStoredPlayer.setChests(new ArrayList<>());
		if (hubStoredPlayer.getLastGivenChests() == null)
			hubStoredPlayer.setLastGivenChests(new HashMap<>());
		return hubStoredPlayer;
	}
	
	public static void get(String player, Callback<HubStoredPlayer> callback) {
		Player playero = Bukkit.getPlayer(player);
		if (playero != null) {
			BadblockPlayer badblockPlayer = (BadblockPlayer) playero;
			HubStoredPlayer hubStoredPlayer = badblockPlayer.getPlayerData().gameData("hub", HubStoredPlayer.class);
			if (hubStoredPlayer.getProperties() == null)
				hubStoredPlayer.setProperties(new HashSet<>());
			if (hubStoredPlayer.getChests() == null)
				hubStoredPlayer.setChests(new ArrayList<>());
			if (hubStoredPlayer.getLastGivenChests() == null)
				hubStoredPlayer.setLastGivenChests(new HashMap<>());
			callback.done(hubStoredPlayer, null);
			return;
		}
	}
	
	// Hub chat
	public boolean hubChat;
	public String lastMountName;
	public TreeMap<String, MountConfig> mountConfigs;
	public TreeMap<String, MountConfig> particleConfigs;
	public List<CustomChest> chests;
	public Map<Integer, Long> lastGivenChests;
	public boolean			hidePlayers;
	public Set<String> properties;
	public String	   lastLocation;
	public long		   maxLastLocationTime;
	public boolean connectInventory;

	public HubStoredPlayer() {
		this.setHubChat(true);
		this.setProperties(new HashSet<>());
		this.setMountConfigs(new TreeMap<>());
		this.setChests(new ArrayList<>());
		this.setLastGivenChests(new HashMap<>());
	}

}
