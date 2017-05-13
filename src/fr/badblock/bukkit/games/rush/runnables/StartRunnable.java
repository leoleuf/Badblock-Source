package fr.badblock.bukkit.games.rush.runnables;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.rush.PluginRush;
import fr.badblock.bukkit.games.rush.configuration.RushMapConfiguration;
import fr.badblock.bukkit.games.rush.listeners.JoinListener;
import fr.badblock.bukkit.games.rush.players.RushScoreboard;
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
	public 	  static 	   GameRunnable  gameTask		   = null;

	public static int time;

	@Override
	public void run() {
		GameAPI.setJoinable(time > 10);
		if(time == 0){
			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;
				bPlayer.playSound(Sound.ORB_PICKUP);
			}
			for(BadblockPlayer player : BukkitUtils.getPlayers()){
				if (player.getCustomObjective() == null)
					new RushScoreboard((BadblockPlayer) player);
			}

			for(Sheep sheep : JoinListener.sheeps){
				sheep.eject();
				sheep.remove();
			}

			JoinListener.sheeps.clear();

			String winner = GameAPI.getAPI().getBadblockScoreboard().getWinner().getInternalName();
			File   file   = new File(PluginRush.MAP, winner + ".json");

			RushMapConfiguration config = new RushMapConfiguration(GameAPI.getAPI().loadConfiguration(file));
			config.save(file);

			PluginRush.getInstance().setMapConfiguration(config);
			GameAPI.getAPI().balanceTeams(true);

			gameTask = new GameRunnable(config);
			gameTask.runTaskTimer(GameAPI.getAPI(), 0, 20L);

			cancel();
		} else if(time % 10 == 0 || time <= 5){
			sendTime(time);
		}

		if(time == 5){
			String winner = GameAPI.getAPI().getBadblockScoreboard().getWinner().getInternalName();
			File   file   = new File(PluginRush.MAP, winner + ".json");

			RushMapConfiguration config = new RushMapConfiguration(GameAPI.getAPI().loadConfiguration(file));
			config.save(file);

			GameAPI.getAPI().getBadblockScoreboard().endVote();

			for(Player player : Bukkit.getOnlinePlayers()){
				new RushScoreboard((BadblockPlayer) player);
			}

			GameAPI.getAPI().setEmptyChunks(config.getMapBounds(), true);
			GameAPI.getAPI().loadChunks(config.getMapBounds(), time * 20);
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

	private ChatColor getColor(int time){
		if(time == 1)
			return ChatColor.DARK_RED;
		else if(time <= 5)
			return ChatColor.RED;
		else return ChatColor.AQUA;
	}

	public static void joinNotify(int currentPlayers, int maxPlayers){
		if (task != null) {
			int a = time - (TIME_BEFORE_START / Bukkit.getMaxPlayers());
			if (time >= 60 && (a <= 60 || Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers())) time = 60;
			else if (time <= 60) time = a;
		}
		if(currentPlayers < PluginRush.getInstance().getConfiguration().minPlayers) return;
		
		startGame(false);
		int a = time - (TIME_BEFORE_START / Bukkit.getMaxPlayers());
		if (time >= 60 && (a <= 60 || Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers())) time = 60;
		else if (time <= 60) time = a;
	}

	public static void startGame(boolean force){
		if(task == null){
			task = new StartRunnable();
			time = force ? 10 : TIME_BEFORE_START;
			task.start();
		}
	}

	public static void stopGame(){
		if(gameTask != null){
			gameTask.forceEnd = true;
			time = TIME_BEFORE_START;
		} else if(task != null){
			task.cancel();
			time = TIME_BEFORE_START;
		}

		task = null;
		gameTask = null;
	}

	public static boolean started(){
		return task != null;
	}
}
