package fr.badblock.survival.runnables.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.survival.players.SurvivalScoreboard;
import fr.badblock.survival.players.TimeProvider;
import fr.badblock.survival.runnables.GameRunnable;
import fr.badblock.survival.runnables.StartRunnable;

public class PvPRunnable extends BukkitRunnable implements TimeProvider {
	public static boolean pvp = false;
	
	public static final int TIME = 30;
	private 			int time = TIME;
	
	public PvPRunnable() {
		SurvivalScoreboard.setTimeProvider(this);
	}
	
	@Override
	public void run() {
		GameRunnable.generalTime++;
		time--;

		if( (time % 10 == 0 || time <= 5) && time > 0 && time <= 30){
			ChatColor 		   color = StartRunnable.getColor(time);
			TranslatableString title = new TranslatableString("survival.pvpin.title", time, color.getChar());

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;

				bPlayer.sendTranslatedTitle(title.getKey(), title.getObjects());
				bPlayer.sendTimings(2, 30, 2);
			}
		} else if(time == 0){
			cancel();
			
			pvp = true;
			TranslatableString title = new TranslatableString("survival.pvp.title");

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;
				
				bPlayer.sendTranslatedTitle(title.getKey(), title.getObjects());
				bPlayer.sendTimings(2, 30, 2);
			}
			
			new PvERunnable().runTaskTimer(GameAPI.getAPI(), 20L, 20L);
		}
	}

	@Override
	public String getId(int num) {
		return num == 0 ? "pvp" : "pve";
	}

	@Override
	public int getTime(int num) {
		return time + (num == 1 ? PvERunnable.TIME : 0);
	}

	@Override
	public int getProvidedCount() {
		return 2;
	}
}
