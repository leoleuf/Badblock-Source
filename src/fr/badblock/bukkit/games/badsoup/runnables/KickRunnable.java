package fr.badblock.bukkit.games.badsoup.runnables;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.badsoup.PluginSoup;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;

public class KickRunnable extends BukkitRunnable {
	private int time = 15;

	@Override
	public void run(){
		if(time == -3){
			Bukkit.shutdown();
		}else if(time <= 5){

			for(BadblockPlayer player : BukkitUtils.getAllPlayers()){
				player.sendPlayer(PluginSoup.getInstance().getConfiguration().fallbackServer);
			}

		} 

		time--;
	}
}