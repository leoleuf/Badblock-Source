package fr.badblock.bukkit.games.survivalgames.runnables;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.survivalgames.PluginSurvival;
import fr.badblock.bukkit.games.survivalgames.configuration.SurvivalMapConfiguration;
import fr.badblock.bukkit.games.survivalgames.players.SurvivalScoreboard;
import fr.badblock.bukkit.games.survivalgames.runnables.game.NoMoveRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StartRunnable extends BukkitRunnable {
	public    static final int 		     TIME_BEFORE_START = 300;
	protected static 	   StartRunnable task 		       = null;
	public    static 	   GameRunnable  gameTask		   = null;

	private int time;

	@Override
	public void run() {
		GameAPI.setJoinable(time > 10);
		if(time == 0){
			for(BadblockPlayer player : BukkitUtils.getPlayers()) {
				if (player.getCustomObjective() == null)
					new SurvivalScoreboard((BadblockPlayer) player);
			}
			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;
				bPlayer.playSound(Sound.ORB_PICKUP);
			}

			String winner = GameAPI.getAPI().getBadblockScoreboard().getWinner().getInternalName();
			File   file   = new File(PluginSurvival.MAP, winner + ".json");

			SurvivalMapConfiguration config = new SurvivalMapConfiguration(GameAPI.getAPI().loadConfiguration(file));
			config.save(file);
			PluginSurvival.getInstance().setMapConfiguration(config);

			new NoMoveRunnable(config).runTaskTimer(GameAPI.getAPI(), 0, 20L);
			cancel();
		} else if(time % 10 == 0 || time <= 5){
			sendTime(time);
		}

		if(time == 3){
			GameAPI.getAPI().getBadblockScoreboard().endVote();

			for(Player player : Bukkit.getOnlinePlayers()){
				new SurvivalScoreboard((BadblockPlayer) player);
			}
		}

		sendTimeHidden(time);

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

	public static ChatColor getColor(int time){
		if(time == 1)
			return ChatColor.DARK_RED;
		else if(time <= 5)
			return ChatColor.RED;
		else return ChatColor.AQUA;
	}

	public static void joinNotify(int currentPlayers, int maxPlayers){
		if (task != null) {
			int a = task.time - (TIME_BEFORE_START / Bukkit.getMaxPlayers());
			if ((a < task.time && task.time <= 10) || ((a < 10 || Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) && task.time >= 10)) task.time = 10;
			else task.time = a;
		}
		int minPlayers = PluginSurvival.getInstance().getConfiguration().minPlayers;

		if(currentPlayers >= minPlayers)
			startGame();
	}

	public static void startGame(){
		if(task == null){
			task = new StartRunnable(TIME_BEFORE_START);
			task.start();
		}
	}

	public static void stopGame(){
		if(gameTask != null){
			gameTask.forceEnd = true;
			task.time = TIME_BEFORE_START;
		} else if(task != null){
			task.cancel();
			task.time = TIME_BEFORE_START;
		} else {
			new KickRunnable();
		}

		task 	 = null;
		gameTask = null;
	}

	public static boolean started(){
		return task != null;
	}
}
