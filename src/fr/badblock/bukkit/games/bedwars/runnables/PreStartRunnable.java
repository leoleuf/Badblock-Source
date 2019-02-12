package fr.badblock.bukkit.games.bedwars.runnables;

import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PreStartRunnable extends BukkitRunnable {
	@Override
	public void run(){
		if(StartRunnable.task != null) cancel();
		else doJob();
	}
	
	public static void doJob(){
		TranslatableString actionbar = GameMessages.missingPlayersActionBar( PluginBedWars.getInstance().getMaxPlayers() - Bukkit.getOnlinePlayers().size() );
		for(Player player : Bukkit.getOnlinePlayers()){
			BadblockPlayer bPlayer = (BadblockPlayer) player;
			bPlayer.sendTranslatedActionBar(actionbar.getKey(), actionbar.getObjects());
		}
	}
}
