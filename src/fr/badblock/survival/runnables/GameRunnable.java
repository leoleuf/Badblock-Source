package fr.badblock.survival.runnables;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameServer.WhileRunningConnectionTypes;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.servers.ChestGenerator;
import fr.badblock.survival.PluginSurvival;
import fr.badblock.survival.players.SurvivalData;
import fr.badblock.survival.players.SurvivalScoreboard;
import fr.badblock.survival.players.TimeProvider;
import fr.badblock.survival.runnables.game.StartDeathmatchRunnable;

public class GameRunnable extends BukkitRunnable implements TimeProvider {
	public static final int MAX_TIME   = 20 * 60;
	public static final int CHEST_TIME = 9 * 60;

	public static int generalTime = 0;
	
	public boolean forceEnd   = false;
	private int    time 	  = MAX_TIME;
	
	public GameRunnable() {
		SurvivalScoreboard.setTimeProvider(this);
	}	
	
	@Override
	public void run() {
		generalTime++;
		int players = 0;

		if(time == CHEST_TIME){
			ChestGenerator gen = GameAPI.getAPI().getChestGenerator();
			gen.addItemInConfiguration(new ItemStack(Material.COMPASS), PluginSurvival.getInstance().getConfiguration().addedCompassProb, false);
			gen.resetChests();
			
			endId   = chestId;
			chestId = -1;
			
		}
		
		for(BadblockPlayer p : GameAPI.getAPI().getRealOnlinePlayers()){
			if(!p.inGameData(SurvivalData.class).death){
				players++;
			}
		}

		if((players > 0 && players <= 3) || forceEnd || time == 0){
			cancel();
			
			GameAPI.getAPI().getGameServer().cancelReconnectionInvitations();
			GameAPI.getAPI().getGameServer().whileRunningConnection(WhileRunningConnectionTypes.SPECTATOR);

			new StartDeathmatchRunnable().runTaskTimer(GameAPI.getAPI(), 0, 20L);
		} else if(players == 0){
			cancel();
			Bukkit.shutdown();
			return;
		}

		time--;
	}

	private int endId   = 1;
	private int chestId = 0;
	
	@Override
	public String getId(int num) {
		return num == endId ? "end" : "chests";
	}

	@Override
	public int getTime(int num) {
		return num == chestId ? time - CHEST_TIME : time;
	}

	@Override
	public int getProvidedCount() {
		return time < CHEST_TIME ? 1 : 2;
	}
}
