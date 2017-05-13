package fr.badblock.bukkit.games.survivalgames.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.badblock.bukkit.games.survivalgames.PluginSurvival;
import fr.badblock.bukkit.games.survivalgames.configuration.SurvivalMapConfiguration;
import fr.badblock.bukkit.games.survivalgames.players.SurvivalData;
import fr.badblock.bukkit.games.survivalgames.players.SurvivalScoreboard;
import fr.badblock.bukkit.games.survivalgames.runnables.BossBarRunnable;
import fr.badblock.bukkit.games.survivalgames.runnables.PreStartRunnable;
import fr.badblock.bukkit.games.survivalgames.runnables.StartRunnable;
import fr.badblock.bukkit.games.survivalgames.runnables.game.DeathmatchRunnable;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.events.api.SpectatorJoinEvent;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;

public class JoinListener extends BadListener {
	@EventHandler
	public void onSpectatorJoin(SpectatorJoinEvent e){
		SurvivalMapConfiguration config = PluginSurvival.getInstance().getMapConfiguration();

		if(DeathmatchRunnable.deathmatch){
			e.getPlayer().teleport(config.getSpecDeathmatch());
		} else {
			e.getPlayer().teleport(config.getSpawnLocation());
		}

		new SurvivalScoreboard(e.getPlayer());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		e.setJoinMessage(null);

		BadblockPlayer player = (BadblockPlayer) e.getPlayer();

		if(inGame()){
			if(player.inGameData(SurvivalData.class).death){
				player.setBadblockMode(BadblockMode.SPECTATOR);
			}

			return;
		}

		if (!player.getBadblockMode().equals(BadblockMode.SPECTATOR)) {
			new BossBarRunnable(player.getUniqueId()).runTaskTimer(GameAPI.getAPI(), 0, 20L);

			player.setGameMode(GameMode.SURVIVAL);
			player.sendTranslatedTitle("survival.join.title");
			player.teleport(PluginSurvival.getInstance().getConfiguration().spawn.getHandle());
			player.sendTimings(0, 80, 20);
			player.sendTranslatedTabHeader(new TranslatableString("survival.tab.header"), new TranslatableString("survival.tab.footer"));

			GameMessages.joinMessage(GameAPI.getGameName(), player.getName(), Bukkit.getOnlinePlayers().size(), PluginSurvival.getInstance().getMaxPlayers()).broadcast();
		}
		PreStartRunnable.doJob();
		StartRunnable.joinNotify(Bukkit.getOnlinePlayers().size(), PluginSurvival.getInstance().getMaxPlayers());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if (StartRunnable.gameTask == null && BukkitUtils.getPlayers().size() - 1 < PluginSurvival.getInstance().getConfiguration().minPlayers) {
			StartRunnable.stopGame();
			StartRunnable.time = 60;
		}
		e.setQuitMessage(null);
	}
}
