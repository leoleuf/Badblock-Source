package fr.badblock.rush.runnables;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.rush.PluginRush;

public class KickRunnable extends BukkitRunnable {
	private int time = 20;
	
	@Override
	public void run(){
		if(time <= 0 && time > -3){
			
			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bplayer = (BadblockPlayer) player;
				bplayer.sendPlayer(PluginRush.getInstance().getConfiguration().fallbackServer);
			}
			
		} else if(time == -3){
			Bukkit.shutdown();
		}
		
		time--;
	}
}
