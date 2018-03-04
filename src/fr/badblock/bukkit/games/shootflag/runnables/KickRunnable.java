package fr.badblock.bukkit.games.shootflag.runnables;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.shootflag.PluginShootFlag;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;

public class KickRunnable extends BukkitRunnable {
	private int time = 15;

	@Override
	public void run(){
		if(time == -3){
			Bukkit.shutdown();
		}else if(time <= 5){

			for(BadblockPlayer player : BukkitUtils.getPlayers()){
				player.sendPlayer(PluginShootFlag.getInstance().getConfiguration().fallbackServer);
			}

		} 

		time--;
	}
}