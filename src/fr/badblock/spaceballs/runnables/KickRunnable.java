package fr.badblock.spaceballs.runnables;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.spaceballs.PluginSB;

public class KickRunnable extends BukkitRunnable {
	private int time = 15;

	@Override
	public void run(){
		if(time == -3){
			Bukkit.shutdown();
		}else if(time <= 5){

			for(BadblockPlayer player : BukkitUtils.getPlayers()){
				player.sendPlayer(PluginSB.getInstance().getConfiguration().fallbackServer);
			}

		} 

		time--;
	}
}
