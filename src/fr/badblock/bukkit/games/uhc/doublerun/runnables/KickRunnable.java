package fr.badblock.bukkit.games.uhc.doublerun.runnables;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.uhc.doublerun.PluginUHC;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;

public class KickRunnable extends BukkitRunnable {
	private int time = 25;

	@Override
	public void run(){
		if(time == -3){
			Bukkit.shutdown();
		}else if(time <= 5){

			for(BadblockPlayer player : GameAPI.getAPI().getOnlinePlayers()){
				player.sendPlayer(PluginUHC.getInstance().getConfiguration().fallbackServer);
			}

		} 

		time--;
	}
}