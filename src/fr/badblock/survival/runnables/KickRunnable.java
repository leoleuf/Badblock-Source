package fr.badblock.survival.runnables;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.survival.PluginSurvival;

public class KickRunnable extends BukkitRunnable {
	private int time = 15;

	@Override
	public void run(){
		if(time <= 5){

			for(BadblockPlayer player : BukkitUtils.getPlayers()){
				player.sendPlayer(PluginSurvival.getInstance().getConfiguration().fallbackServer);
			}

		}else if(time == -3){
			Bukkit.shutdown();
		}

		time--;
	}
}


