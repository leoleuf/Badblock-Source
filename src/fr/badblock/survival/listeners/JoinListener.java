package fr.badblock.survival.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.events.api.SpectatorJoinEvent;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;
import fr.badblock.survival.PluginSurvival;
import fr.badblock.survival.configuration.SurvivalMapConfiguration;
import fr.badblock.survival.runnables.BossBarRunnable;
import fr.badblock.survival.runnables.PreStartRunnable;
import fr.badblock.survival.runnables.StartRunnable;
import fr.badblock.survival.runnables.game.DeathmatchRunnable;

public class JoinListener extends BadListener {
	@EventHandler
	public void onSpectatorJoin(SpectatorJoinEvent e){
		SurvivalMapConfiguration config = PluginSurvival.getInstance().getMapConfiguration();
		
		if(DeathmatchRunnable.deathmatch){
			e.getPlayer().teleport(config.getSpecDeathmatch());
		} else {
			e.getPlayer().teleport(config.getSpawnLocation());
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		e.setJoinMessage(null);
		
		if(inGame()){
			return;
		}
		
		BadblockPlayer player = (BadblockPlayer) e.getPlayer();
	
		new BossBarRunnable(player.getUniqueId()).runTaskTimer(GameAPI.getAPI(), 0, 20L);
		
		player.setGameMode(GameMode.SURVIVAL);
		player.sendTranslatedTitle("survival.join.title");
		player.teleport(PluginSurvival.getInstance().getConfiguration().spawn.getHandle());
		player.sendTimings(0, 80, 20);
		player.sendTranslatedTabHeader(new TranslatableString("survival.tab.header"), new TranslatableString("survival.tab.footer"));
		
		GameMessages.joinMessage(GameAPI.getGameName(), player.getName(), Bukkit.getOnlinePlayers().size(), PluginSurvival.getInstance().getMaxPlayers()).broadcast();
		PreStartRunnable.doJob();
		StartRunnable.joinNotify(Bukkit.getOnlinePlayers().size(), PluginSurvival.getInstance().getMaxPlayers());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		e.setQuitMessage(null);
	}
}
