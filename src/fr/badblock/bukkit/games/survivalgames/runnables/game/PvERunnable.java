package fr.badblock.bukkit.games.survivalgames.runnables.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.survivalgames.players.SurvivalScoreboard;
import fr.badblock.bukkit.games.survivalgames.players.TimeProvider;
import fr.badblock.bukkit.games.survivalgames.runnables.GameRunnable;
import fr.badblock.bukkit.games.survivalgames.runnables.StartRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class PvERunnable extends BukkitRunnable implements TimeProvider {
	public static boolean   pve  = false;
	
	public static final int TIME = 30;
	private int 			time = TIME;
	
	public PvERunnable() {
		SurvivalScoreboard.setTimeProvider(this);
	}
	
	@Override
	public void run() {
		GameRunnable.generalTime++;
		time--;

		if( (time % 10 == 0 || time <= 5) && time > 0 && time <= 30){
			ChatColor 		   color = StartRunnable.getColor(time);
			TranslatableString title = new TranslatableString("survival.pvein.title", time, color.getChar());

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;

				bPlayer.sendTranslatedTitle(title.getKey(), title.getObjects());
				bPlayer.sendTimings(2, 30, 2);
			}
		} else if(time == 0){
			cancel();
			
			pve = true;
			TranslatableString title = new TranslatableString("survival.pve.title");

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;
				
				bPlayer.sendTranslatedTitle(title.getKey(), title.getObjects());
				bPlayer.sendTimings(2, 30, 2);
			}
			
			StartRunnable.gameTask = new GameRunnable();
			StartRunnable.gameTask.runTaskTimer(GameAPI.getAPI(), 20L, 20L);
		}
	}

	@Override
	public String getId(int num) {
		return "pve";
	}

	@Override
	public int getTime(int num) {
		return time;
	}

	@Override
	public int getProvidedCount() {
		return 1;
	}
}