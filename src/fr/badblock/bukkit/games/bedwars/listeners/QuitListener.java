package fr.badblock.bukkit.games.bedwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.bukkit.games.bedwars.players.BedWarsScoreboard;
import fr.badblock.bukkit.games.bedwars.runnables.StartRunnable;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.rankeds.RankedCalc;
import fr.badblock.gameapi.game.rankeds.RankedManager;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class QuitListener extends BadListener {
	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		if (StartRunnable.gameTask == null && BukkitUtils.getPlayers().size() - 1 < PluginBedWars.getInstance().getConfiguration().minPlayers) {
			StartRunnable.stopGame();
			StartRunnable.time = StartRunnable.time > 30 ? StartRunnable.time : 30;
		}
		if(!inGame()) return;

		BadblockPlayer player = (BadblockPlayer) e.getPlayer();
		BadblockTeam   team   = player.getTeam();

		if (StartRunnable.gameTask == null && BukkitUtils.getPlayers().size() < PluginBedWars.getInstance().getConfiguration().minPlayers) {
			StartRunnable.stopGame();
			StartRunnable.time = 60;
		}
		if(team == null) return;
		// Work with rankeds
		String rankedGameName = RankedManager.instance.getCurrentRankedGameName();
		player.getPlayerData().incrementTempRankedData(rankedGameName, BedWarsScoreboard.LOOSES, 1);
		RankedManager.instance.calcPoints(rankedGameName, player, new RankedCalc()
		{

			@Override
			public long done() {
				double kills = RankedManager.instance.getData(rankedGameName, player, BedWarsScoreboard.KILLS);
				double deaths = RankedManager.instance.getData(rankedGameName, player, BedWarsScoreboard.DEATHS);
				double wins = RankedManager.instance.getData(rankedGameName, player, BedWarsScoreboard.WINS);
				double looses = RankedManager.instance.getData(rankedGameName, player, BedWarsScoreboard.LOOSES);
				double brokenBeds = RankedManager.instance.getData(rankedGameName, player, BedWarsScoreboard.BROKENBEDS);
				double total = 
						( (kills / 0.5D) + (wins * 4) + 
								( (kills * brokenBeds) + (brokenBeds * 2) * (kills / (deaths > 0 ? deaths : 1) ) ) )
						/ (1 + looses);
				return (long) total;
			}

		});
		RankedManager.instance.fill(rankedGameName);

		if(team.getOnlinePlayers().size() == 0){
			GameAPI.getAPI().getGameServer().cancelReconnectionInvitations(team);
			GameAPI.getAPI().unregisterTeam(team);

			new TranslatableString("rush.team-loose", team.getChatName()).broadcast();
		}
	}
}
