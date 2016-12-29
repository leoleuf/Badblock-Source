package fr.badblock.spaceballs.runnables;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;
import fr.badblock.spaceballs.PluginSB;
import fr.badblock.spaceballs.configuration.SpaceMapConfiguration;
import fr.badblock.spaceballs.players.SpaceScoreboard;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StartRunnable extends BukkitRunnable {
	public    static final int 		     TIME_BEFORE_START = 30;
	protected static 	   StartRunnable task 		       = null;
	public    static 	   GameRunnable  gameTask		   = null;

	private int time;
	
	@Override
	public void run() {
		GameAPI.setJoinable(time > 10);
		if(time == 0){
			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;
				bPlayer.playSound(Sound.ORB_PICKUP);
			}
			
			String winner = GameAPI.getAPI().getBadblockScoreboard().getWinner().getInternalName();
			File   file   = new File(PluginSB.MAP, winner + ".json");
			
			SpaceMapConfiguration config = new SpaceMapConfiguration(GameAPI.getAPI().loadConfiguration(file));
			config.save(file);
			PluginSB.getInstance().setMapConfiguration(config);
			GameAPI.getAPI().balanceTeams(true);
			
			gameTask = new GameRunnable(config);
			gameTask.runTaskTimer(GameAPI.getAPI(), 0, 20L);
			
			cancel();
		} else if(time % 10 == 0 || time <= 5){
			sendTime(time);
		}
		
		if(time == 3){
			GameAPI.getAPI().getBadblockScoreboard().endVote();
			
			for(Player player : Bukkit.getOnlinePlayers()){
				new SpaceScoreboard((BadblockPlayer) player);
			}
		}
		
		sendTimeHidden(time);

		boolean ok = time > 10;
		time = TIME_BEFORE_START / Bukkit.getMaxPlayers();
		if (time < 10 && ok) time = 10;
		time--;
	}
	
	protected void start(){
		sendTime(time);
		
		runTaskTimer(GameAPI.getAPI(), 0, 20L);
	}

	private void sendTime(int time){
		ChatColor color = getColor(time);
		TranslatableString title = GameMessages.startIn(time, color);
		
		for(Player player : Bukkit.getOnlinePlayers()){
			BadblockPlayer bPlayer = (BadblockPlayer) player;

			bPlayer.playSound(Sound.NOTE_PLING);
			bPlayer.sendTranslatedTitle(title.getKey(), title.getObjects());
			bPlayer.sendTimings(2, 30, 2);
		}
	}
	
	private void sendTimeHidden(int time){
		ChatColor color = getColor(time);
		TranslatableString actionbar = GameMessages.startInActionBar(time, color);
		
		for(Player player : Bukkit.getOnlinePlayers()){
			BadblockPlayer bPlayer = (BadblockPlayer) player;

			if(time > 0)
				bPlayer.sendTranslatedActionBar(actionbar.getKey(), actionbar.getObjects());
			bPlayer.setLevel(time);
			bPlayer.setExp(0.0f);
		}
	}
	
	private ChatColor getColor(int time){
		if(time == 1)
			return ChatColor.DARK_RED;
		else if(time <= 5)
			return ChatColor.RED;
		else return ChatColor.AQUA;
	}

	public static void joinNotify(int currentPlayers, int maxPlayers){
		if(currentPlayers < PluginSB.getInstance().getConfiguration().minPlayers) return;
		
		startGame(false);
	}
	
	public static void startGame(boolean force){
		if(task == null){
			task = new StartRunnable(force ? 10 : TIME_BEFORE_START);
			task.start();
		}
	}
	
	public static void stopGame(){
		if(gameTask != null){
			gameTask.forceEnd = true;
			task.time = TIME_BEFORE_START;
		} else if(task != null){
			task.time = TIME_BEFORE_START;
			task.cancel();
		}
		
		task = null;
		gameTask = null;
	}
	
	public static boolean started(){
		return task != null;
	}
}
