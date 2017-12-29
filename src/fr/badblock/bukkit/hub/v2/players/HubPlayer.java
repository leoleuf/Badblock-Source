package fr.badblock.bukkit.hub.v2.players;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import fr.badblock.bukkit.hub.v2.config.ConfigLoader;
import fr.badblock.bukkit.hub.v2.inventories.BukkitInventories;
import fr.badblock.gameapi.players.BadblockPlayer;
import lombok.Data;

@Data public class HubPlayer {

	private static Map<String, HubPlayer> players = new HashMap<>();
	
	private String		   name;
	private static BadblockPlayer player;
	private static String		   inventory;
	
	public HubPlayer(BadblockPlayer player) {
		this.setPlayer(player);
		this.setName(player.getName());
		players.put(getName(), this);
	}
	
	private String getName() {
		return name;
	}

	private void setPlayer(BadblockPlayer player) {
		HubPlayer.player = player;
		
	}
	private void setName(String name) {
		this.name = name;
	}
	
	public HubPlayer loadEverything() {
		loadData();
		loadPlayer();
		return this;
	}
	
	public HubPlayer loadData() {
		// TODO: load HubStoredPlayer
		return this;
	}
	
	public HubPlayer loadPlayer() {
		// TODO: load player : locations/inventories...
		if (!isOnline()) return this;
		getPlayer().teleport(ConfigLoader.getLoc().getLocation("spawn"));
		giveDefaultInventory();
		return this;
	}
	
	public HubPlayer giveDefaultInventory() {
		BukkitInventories.giveDefaultInventory(getPlayer());
		return this; 
	}
	
	public void unload() {
		players.remove(getName());
	}
	
	public boolean isOnline() {
		return getPlayer().isOnline();
	}
	
	public static BadblockPlayer getPlayer() {
		return player;
	}
	
	public static HubPlayer initialize(BadblockPlayer player) {
		return new HubPlayer(player);
	}
	
	public static HubPlayer get(BadblockPlayer player) {
		return players.get(player.getName());
	}
	
	public static Collection<HubPlayer> getPlayers() {
		return players.values();
	}

	public void setInventory(Object object) {
		
	}

	public static String getInventory() {
		return inventory;
	}
	
}
