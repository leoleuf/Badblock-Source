package fr.badblock.bukkit.games.bedwars.runnables;

import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.gameapi.utils.BukkitUtils;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class KickRunnable extends BukkitRunnable {
	private int time = 15;

	@Override
	public void run(){
		if(time == -3) Bukkit.shutdown();
		else if(time <= 5) BukkitUtils.getAllPlayers().forEach(player -> player.sendPlayer(PluginBedWars.getInstance().getConfiguration().fallbackServer));
		time--;
	}
}
