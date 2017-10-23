package fr.badblock.bukkit.games.spaceballs.runnables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.spaceballs.PluginSB;
import fr.badblock.bukkit.games.spaceballs.SBAchievementList;
import fr.badblock.bukkit.games.spaceballs.configuration.SpaceMapConfiguration;
import fr.badblock.bukkit.games.spaceballs.entities.SpaceTeamData;
import fr.badblock.bukkit.games.spaceballs.players.SpaceData;
import fr.badblock.bukkit.games.spaceballs.players.SpaceScoreboard;
import fr.badblock.bukkit.games.spaceballs.result.SBResults;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.game.rankeds.RankedCalc;
import fr.badblock.gameapi.game.rankeds.RankedManager;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.players.data.InGameKitData;
import fr.badblock.gameapi.players.data.PlayerAchievementState;
import fr.badblock.gameapi.players.kits.PlayerKit;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.general.TimeUnit;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import lombok.Getter;

public class GameRunnable extends BukkitRunnable {
	public static final int MAX_TIME = 60 * 20;

	public static boolean damage = false;
	public boolean forceEnd 		 = false;
	@Getter
	private int    time 			 = MAX_TIME;

	public GameRunnable(SpaceMapConfiguration config){
		GameAPI.getAPI().getGameServer().setGameState(GameState.RUNNING);
		GameAPI.getAPI().getGameServer().saveTeamsAndPlayersForResult();

		Bukkit.getWorlds().forEach(world -> {
			world.setTime(config.getTime());
			world.getEntities().forEach(entity -> {
				if(entity.getType() != EntityType.PLAYER)
					entity.remove();
			});
		});

		for(BadblockTeam team : GameAPI.getAPI().getTeams()){

			Location location = team.teamData(SpaceTeamData.class).getRespawnLocation();
			location.getChunk().load();

			for(BadblockPlayer p : team.getOnlinePlayers()){
				handle(p);
			}

		}

		GameAPI.getAPI().getJoinItems().doClearInventory(false);
		GameAPI.getAPI().getJoinItems().end();
	}

	public static void handle(BadblockPlayer player) {
		BadblockTeam team = player.getTeam();
		if (team == null) return;
		Location location = team.teamData(SpaceTeamData.class).getRespawnLocation();
		player.changePlayerDimension(BukkitUtils.getEnvironment( PluginSB.getInstance().getMapConfiguration().getDimension() ));
		player.teleport(location);

		boolean good = true;


		for(PlayerKit toUnlock : PluginSB.getInstance().getKits().values()){
			if(!toUnlock.isVIP()){
				if(player.getPlayerData().getUnlockedKitLevel(toUnlock) < 2){
					good = false; break;
				}
			}
		}

		if(good && !player.getPlayerData().getAchievementState(SBAchievementList.SB_ALLKITS).isSucceeds()){
			player.getPlayerData().getAchievementState(SBAchievementList.SB_ALLKITS).succeed();
			SBAchievementList.SB_ALLKITS.reward(player);
		}

		PlayerKit kit = player.inGameData(InGameKitData.class).getChoosedKit();

		if(kit != null){
			kit.giveKit(player);
		} else {
			PluginSB.getInstance().giveDefaultKit(player);
		}
	}

	@Override
	public void run() {
		GameAPI.setJoinable(time > MAX_TIME / 2);
		if(time == MAX_TIME - 2){
			damage = true;

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bp = (BadblockPlayer) player;
				bp.pseudoJail(bp.getTeam().teamData(SpaceTeamData.class).getRespawnLocation(), 300.0d);
			}
		} else if(time == 0)
			forceEnd = true;

		int size = GameAPI.getAPI().getTeams().size();

		List<BadblockTeam> to = new ArrayList<>();

		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			if(team.getOnlinePlayers().size() == 0){
				GameAPI.getAPI().getGameServer().cancelReconnectionInvitations(team);
				to.add(team);

				new TranslatableString("spaceballs.team-loose", team.getChatName()).broadcast();;
			}
		}

		to.forEach(GameAPI.getAPI()::unregisterTeam);

		if(size == 1 || forceEnd){
			cancel();
			Iterator<BadblockTeam> teams = GameAPI.getAPI().getTeams().iterator();
			BadblockTeam winner = null;
			while (teams.hasNext()) {
				BadblockTeam team = teams.next();
				if (team != null) {
					if (winner == null || (team.teamData(SpaceTeamData.class) != null && team.teamData(SpaceTeamData.class).getDiamondsCount() > winner.teamData(SpaceTeamData.class).getDiamondsCount()))
						winner = team;
				}
			}


			GameAPI.getAPI().getGameServer().setGameState(GameState.FINISHED);

			Location winnerLocation = PluginSB.getInstance().getMapConfiguration().getSpawnLocation();
			Location looserLocation = winnerLocation.clone().add(0d, 7d, 0d);

			for(BadblockPlayer bp : GameAPI.getAPI().getOnlinePlayers()){
				bp.heal();
				bp.clearInventory();
				bp.setInvulnerable(true);

				double badcoins = bp.inGameData(SpaceData.class).getScore() / 4;
				double xp	    = bp.inGameData(SpaceData.class).getScore() / 2;

				if(winner.equals(bp.getTeam())){
					bp.teleport(winnerLocation);
					bp.setAllowFlight(true);
					bp.setFlying(true);

					new BukkitRunnable() {
						int count = 5;

						@Override
						public void run() {
							count--;

							bp.teleport(winnerLocation);
							bp.setAllowFlight(true);
							bp.setFlying(true);

							if(count == 0)
								cancel();
						}
					}.runTaskTimer(GameAPI.getAPI(), 5L, 5L);

					bp.sendTranslatedTitle("spaceballs.title-win", winner.getChatName());
					bp.getPlayerData().incrementStatistic("spaceballs", SpaceScoreboard.WINS);
					bp.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), SpaceScoreboard.WINS, 1);

					incrementAchievements(bp, SBAchievementList.SB_WIN_1, SBAchievementList.SB_WIN_2, SBAchievementList.SB_WIN_3, SBAchievementList.SB_WIN_4);
				} else {
					badcoins = ((double) badcoins) / 1.5d;

					bp.jailPlayerAt(looserLocation);
					bp.sendTranslatedTitle("spaceballs.title-loose", winner.getChatName());

					if(bp.getBadblockMode() == BadblockMode.PLAYER)
					{
						bp.getPlayerData().incrementStatistic("spaceballs", SpaceScoreboard.LOOSES);
						bp.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), SpaceScoreboard.LOOSES, 1);
					}
				}
				if(badcoins > 20 * bp.getPlayerData().getBadcoinsMultiplier())
					badcoins = 20 * bp.getPlayerData().getBadcoinsMultiplier();
				if(xp > 50 * bp.getPlayerData().getXpMultiplier())
					xp = 50 * bp.getPlayerData().getXpMultiplier();

				int rbadcoins = badcoins < 2 ? 2 : (int) badcoins;
				int rxp		  = xp < 5 ? 5 : (int) xp;

				bp.getPlayerData().addBadcoins(rbadcoins, true);
				bp.getPlayerData().addXp(rxp, true);

				new BukkitRunnable(){

					@Override
					public void run(){
						if(bp.isOnline()){
							bp.sendTranslatedActionBar("spaceballs.win", rbadcoins, rxp);
						}
					}

				}.runTaskTimer(GameAPI.getAPI(), 0, 30L);

				if (bp.getCustomObjective() != null)
					bp.getCustomObjective().generate();
			}

			// Work with rankeds
			String rankedGameName = RankedManager.instance.getCurrentRankedGameName();
			for (BadblockPlayer player : BukkitUtils.getPlayers())
			{
				RankedManager.instance.calcPoints(rankedGameName, player, new RankedCalc()
				{

					@Override
					public long done() {
						double kills = RankedManager.instance.getData(rankedGameName, player, SpaceScoreboard.KILLS);
						double deaths = RankedManager.instance.getData(rankedGameName, player, SpaceScoreboard.DEATHS);
						double wins = RankedManager.instance.getData(rankedGameName, player, SpaceScoreboard.WINS);
						double looses = RankedManager.instance.getData(rankedGameName, player, SpaceScoreboard.LOOSES);
						double diamonds = RankedManager.instance.getData(rankedGameName, player, SpaceScoreboard.DIAMONDS);
						double total = 
								( (kills / 0.5D) + (wins * 4) + 
										( (kills * diamonds) + (diamonds / 0.25) * (kills / (deaths > 0 ? deaths : 1) ) ) )
								/ (1 + looses);
						return (long) total;
					}

				});
			}
			RankedManager.instance.fill(rankedGameName);

			new SBResults(TimeUnit.SECOND.toShort(time, TimeUnit.SECOND, TimeUnit.HOUR), winner);
			new EndEffectRunnable(winnerLocation, winner).runTaskTimer(GameAPI.getAPI(), 0, 1L);
			new KickRunnable().runTaskTimer(GameAPI.getAPI(), 0, 20L);

		} else if(size == 0){
			cancel();
			Bukkit.shutdown();
			return;
		} else {
			for(BadblockPlayer player : GameAPI.getAPI().getOnlinePlayers())
				if (player.getCustomObjective() != null)
					player.getCustomObjective().generate();
		}

		time--;
	}

	private static void incrementAchievements(BadblockPlayer player, PlayerAchievement... achievements){
		for(PlayerAchievement achievement : achievements){
			PlayerAchievementState state = player.getPlayerData().getAchievementState(achievement);
			state.progress(1.0d);
			state.trySucceed(player, achievement);
		}
		player.saveGameData();
	}
}
