package fr.badblock.speeduhc.runnables.game;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.speeduhc.PluginUHC;
import fr.badblock.speeduhc.players.TimeProvider;
import fr.badblock.speeduhc.players.UHCData;
import fr.badblock.speeduhc.players.UHCScoreboard;
import fr.badblock.speeduhc.result.UHCResults;
import fr.badblock.speeduhc.runnables.EndEffectRunnable;
import fr.badblock.speeduhc.runnables.KickRunnable;

public class DeathmatchRunnable extends BukkitRunnable implements TimeProvider {
	public static int generalTime = 35 * 60;
	public static boolean forceEnd = false;

	public DeathmatchRunnable() {
		UHCScoreboard.setTimeProvider(this);
	}

	public static int countEntities(){
		List<BadblockTeam> to = GameAPI.getAPI().getTeams().stream().filter(team -> team.getOnlinePlayers().isEmpty()).collect(Collectors.toList());

		for(BadblockTeam team : to){
			GameAPI.getAPI().getGameServer().cancelReconnectionInvitations(team);

			new TranslatableString("uhcspeed.team-loose", team.getChatName()).broadcast();;
			GameAPI.getAPI().unregisterTeam(team);
		}

		if(forceEnd || generalTime == 0)
			return 0;

		if(PluginUHC.getInstance().getConfiguration().allowTeams){
			return (int) GameAPI.getAPI().getTeams().parallelStream().filter(team -> team.getOnlinePlayers().size() > 0).count();
		} else {
			return GameAPI.getAPI().getRealOnlinePlayers().size();
		}
	}

	public static BadblockTeam getTeam(){
		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			if(team.getOnlinePlayers().size() == 0)
				continue;

			return team;
		}

		return null;
	}

	public static BadblockPlayer getPlayer(){
		for(BadblockPlayer player : GameAPI.getAPI().getOnlinePlayers()){
			if(player.getBadblockMode() == BadblockMode.SPECTATOR)
				continue;

			return player;
		}

		return null;
	}

	public static void doEnd(){
		int entities = countEntities();

		if(entities == 0){
			Bukkit.shutdown();
			return;
		}

		BadblockTeam   winner 		= getTeam();
		BadblockPlayer winnerPlayer = winner == null ? getPlayer() : null;
		if (winnerPlayer != null) {
			winnerPlayer.getPlayerData().addRankedPoints(3);
		}

		GameAPI.getAPI().getGameServer().setGameState(GameState.FINISHED);

		Location winnerLocation = PluginUHC.getInstance().getDefaultLoc();
		Location looserLocation = winnerLocation.clone().add(0d, 7d, 0d);

		for(BadblockPlayer player : GameAPI.getAPI().getOnlinePlayers()){
			try {
				BadblockPlayer bp = (BadblockPlayer) player;
				bp.heal();
				bp.clearInventory();
				bp.setInvulnerable(true);

				bp.inGameData(UHCData.class).doReward(bp, winner, winnerPlayer, winnerLocation, looserLocation);

				bp.getCustomObjective().generate();
			} catch(Exception e){
				e.printStackTrace();
			}
		}

		try {
			new UHCResults(winner, winnerPlayer);
			BukkitUtils.getPlayers().forEach(bp -> bp.sendTranslatedMessage("game.waitforbeingteleportedinanothergame", Bukkit.getServerName().split("_")[0]));
			new EndEffectRunnable(winnerLocation, winner).runTaskTimer(GameAPI.getAPI(), 0, 1L);
		} catch(Exception e){
			e.printStackTrace();
		}
		
		new KickRunnable().runTaskTimer(GameAPI.getAPI(), 0, 20L);
	}

	@Override
	public void run() {
		if(countEntities() <= 1){
			cancel();
			doEnd();
		}

		generalTime--;
	}

	@Override
	public String getId(int num) {
		return "deathmatch";
	}

	@Override
	public int getTime(int num) {
		return generalTime;
	}

	@Override
	public int getProvidedCount() {
		return 1;
	}
}