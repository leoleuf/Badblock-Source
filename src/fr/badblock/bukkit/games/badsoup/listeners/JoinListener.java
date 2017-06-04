package fr.badblock.bukkit.games.badsoup.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.badblock.bukkit.games.badsoup.PluginSoup;
import fr.badblock.bukkit.games.badsoup.configuration.SoupMapConfiguration;
import fr.badblock.bukkit.games.badsoup.players.SoupData;
import fr.badblock.bukkit.games.badsoup.players.SoupScoreboard;
import fr.badblock.bukkit.games.badsoup.runnables.BossBarRunnable;
import fr.badblock.bukkit.games.badsoup.runnables.PreStartRunnable;
import fr.badblock.bukkit.games.badsoup.runnables.StartRunnable;
import fr.badblock.bukkit.games.badsoup.runnables.game.DeathmatchRunnable;
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
		SoupMapConfiguration config = PluginSoup.getInstance().getMapConfiguration();

		if(DeathmatchRunnable.deathmatch){
			e.getPlayer().teleport(config.getSpecDeathmatch());
		} else {
			e.getPlayer().teleport(config.getSpawnLocation());
		}

		new SoupScoreboard(e.getPlayer());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		e.setJoinMessage(null);

		BadblockPlayer player = (BadblockPlayer) e.getPlayer();

		if(inGame()){
			if(player.inGameData(SoupData.class).death){
				player.setBadblockMode(BadblockMode.SPECTATOR);
			}

			return;
		}

		if (!player.getBadblockMode().equals(BadblockMode.SPECTATOR)) {
			new BossBarRunnable(player.getUniqueId()).runTaskTimer(GameAPI.getAPI(), 0, 20L);

			player.setGameMode(GameMode.SURVIVAL);
			player.sendTranslatedTitle("survival.join.title");
			player.teleport(PluginSoup.getInstance().getConfiguration().spawn.getHandle());
			player.sendTimings(0, 80, 20);
			player.sendTranslatedTabHeader(new TranslatableString("survival.tab.header"), new TranslatableString("survival.tab.footer"));

			GameMessages.joinMessage(GameAPI.getGameName(), player.getName(), Bukkit.getOnlinePlayers().size(), PluginSoup.getInstance().getMaxPlayers()).broadcast();
		}
		PreStartRunnable.doJob();
		StartRunnable.joinNotify(Bukkit.getOnlinePlayers().size(), PluginSoup.getInstance().getMaxPlayers());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if (StartRunnable.gameTask == null && BukkitUtils.getPlayers().size() - 1 < PluginSoup.getInstance().getConfiguration().minPlayers) {
			StartRunnable.stopGame();
			StartRunnable.time = StartRunnable.time > 60 ? StartRunnable.time : 60;
		}
		e.setQuitMessage(null);
	}
}
