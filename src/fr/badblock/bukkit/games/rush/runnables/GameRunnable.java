package fr.badblock.bukkit.games.rush.runnables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.rush.PluginRush;
import fr.badblock.bukkit.games.rush.RushAchievementList;
import fr.badblock.bukkit.games.rush.configuration.RushConfiguration.SpawnableItem;
import fr.badblock.bukkit.games.rush.configuration.RushMapConfiguration;
import fr.badblock.bukkit.games.rush.entities.RushTeamData;
import fr.badblock.bukkit.games.rush.players.RushData;
import fr.badblock.bukkit.games.rush.players.RushScoreboard;
import fr.badblock.bukkit.games.rush.result.RushResults;
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

public class GameRunnable extends BukkitRunnable {
	public static boolean damage = false;
	public boolean forceEnd = false;
	public static int    time 	= 0;

	public GameRunnable(RushMapConfiguration config){
		GameAPI.getAPI().getGameServer().setGameState(GameState.RUNNING);
		GameAPI.getAPI().getGameServer().saveTeamsAndPlayersForResult();

		for(SpawnableItem item : PluginRush.getInstance().getConfiguration().items){
			new ItemSpawnRunnable(Material.matchMaterial(item.item), item.ticks).start();
		}

		if (!PluginRush.getInstance().getMapConfiguration().getAllowBows()) {
			remove(Material.BOW);
			remove(Material.ARROW);
		}

		Bukkit.getWorlds().forEach(world -> {
			world.setTime(config.getTime());
			world.getEntities().forEach(entity -> {
				if(entity.getType() != EntityType.PLAYER)
					entity.remove();
			});
		});

		for(BadblockTeam team : GameAPI.getAPI().getTeams()){


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
		Location location = team.teamData(RushTeamData.class).getRespawnLocation();
		player.changePlayerDimension(BukkitUtils.getEnvironment( PluginRush.getInstance().getMapConfiguration().getDimension() ));
		player.teleport(location);
		player.setGameMode(GameMode.SURVIVAL);
		if (player.getCustomObjective() == null)
			new RushScoreboard((BadblockPlayer) player);
		player.getCustomObjective().generate();

		boolean good = true;


		for(PlayerKit toUnlock : PluginRush.getInstance().getKits().values()){
			if(!toUnlock.isVIP()){
				if(player.getPlayerData().getUnlockedKitLevel(toUnlock) < 2){
					good = false; break;
				}
			}
		}

		if(good && !player.getPlayerData().getAchievementState(RushAchievementList.RUSH_ALLKITS).isSucceeds()){
			player.getPlayerData().getAchievementState(RushAchievementList.RUSH_ALLKITS).succeed();
			RushAchievementList.RUSH_ALLKITS.reward(player);
		}

		PlayerKit kit = player.inGameData(InGameKitData.class).getChoosedKit();

		if(kit != null){
			if (PluginRush.getInstance().getMapConfiguration().getAllowBows())
				kit.giveKit(player);
			else
				kit.giveKit(player, Material.BOW, Material.ARROW);
		} else {
			PluginRush.getInstance().giveDefaultKit(player);
		}
	}

	public void remove(Material m) {
		Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
		Recipe recipe;
		while(it.hasNext())
		{
			recipe = it.next();
			if (recipe != null && recipe.getResult().getType() == m)
			{
				it.remove();
			}
		}
	}

	@Override
	public void run() {
		GameAPI.setJoinable(GameRunnable.time < 900);
		BukkitUtils.getPlayers().stream().filter(player -> player.getCustomObjective() != null).forEach(player -> player.getCustomObjective().generate());
		if(time == 2){
			damage = true;

			for(BadblockPlayer player : GameAPI.getAPI().getRealOnlinePlayers()){
				if(player.getTeam() != null)
					player.pseudoJail(player.getTeam().teamData(RushTeamData.class).getRespawnLocation(), 300.0d);
			}
		}

		int size = GameAPI.getAPI().getTeams().size();

		List<BadblockTeam> to = new ArrayList<>();

		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			if(team.getOnlinePlayers().size() == 0){
				GameAPI.getAPI().getGameServer().cancelReconnectionInvitations(team);
				to.add(team);

				new TranslatableString("rush.team-loose", team.getChatName()).broadcast();;
			}
		}

		if (GameAPI.getAPI().getTeams().size() > 1) {
			to.forEach(GameAPI.getAPI()::unregisterTeam);
		}

		if(GameAPI.getAPI().getTeams().stream().filter(team -> team.playersCurrentlyOnline() > 0).count() <= 1 || forceEnd){
			cancel();
			Iterator<BadblockTeam> iterator = GameAPI.getAPI().getTeams().iterator();
			BadblockTeam winner = null;
			if (iterator.hasNext()) winner = iterator.next();

			GameAPI.getAPI().getGameServer().setGameState(GameState.FINISHED);

			Location winnerLocation = PluginRush.getInstance().getMapConfiguration().getSpawnLocation();
			Location looserLocation = winnerLocation.clone().add(0d, 7d, 0d);

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bp = (BadblockPlayer) player;
				bp.heal();
				bp.clearInventory();
				bp.setInvulnerable(true);

				double badcoins = bp.inGameData(RushData.class).getScore() / 4; // 10
				double xp	    = bp.inGameData(RushData.class).getScore() / 2; // 5

				if (winner != null) {
					if(winner.equals(bp.getTeam())){
						bp.getPlayerData().addRankedPoints(3);
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
						bp.sendTranslatedTitle("rush.title-win", winner.getChatName());
						bp.getPlayerData().incrementStatistic("rush", RushScoreboard.WINS);
						bp.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), RushScoreboard.WINS, 1);

						incrementAchievements(bp, RushAchievementList.RUSH_WIN_1, RushAchievementList.RUSH_WIN_2, RushAchievementList.RUSH_WIN_3, RushAchievementList.RUSH_WIN_4);

						if(time <= 600){
							incrementAchievements(bp, RushAchievementList.RUSH_RUSHER_1, RushAchievementList.RUSH_RUSHER_2, RushAchievementList.RUSH_RUSHER_3, RushAchievementList.RUSH_RUSHER_4);
						}
					} else {
						bp.getPlayerData().addRankedPoints(-2);
						badcoins = ((double) badcoins) / 1.5d;

						bp.jailPlayerAt(looserLocation);
						bp.sendTranslatedTitle("rush.title-loose", winner.getChatName());

						if(bp.getBadblockMode() == BadblockMode.PLAYER)
						{
							bp.getPlayerData().incrementStatistic("rush", RushScoreboard.LOOSES);
							bp.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), RushScoreboard.LOOSES, 1);
						}
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
							bp.sendTranslatedActionBar("rush.win", rbadcoins, rxp);
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
						double kills = RankedManager.instance.getData(rankedGameName, player, RushScoreboard.KILLS);
						double deaths = RankedManager.instance.getData(rankedGameName, player, RushScoreboard.DEATHS);
						double wins = RankedManager.instance.getData(rankedGameName, player, RushScoreboard.WINS);
						double looses = RankedManager.instance.getData(rankedGameName, player, RushScoreboard.LOOSES);
						double brokenBeds = RankedManager.instance.getData(rankedGameName, player, RushScoreboard.BROKENBEDS);
						double total = 
								( (kills / 0.5D) + (wins * 4) + 
										( (kills * brokenBeds) + (brokenBeds * 2) * (kills / (deaths > 0 ? deaths : 1) ) ) )
								/ (1 + looses);
						return (long) total;
					}

				});
			}
			RankedManager.instance.fill(rankedGameName);

			if (winner != null)
				new RushResults(TimeUnit.SECOND.toShort(time, TimeUnit.SECOND, TimeUnit.HOUR), winner);
			if (winner != null)
				new EndEffectRunnable(winnerLocation, winner).runTaskTimer(GameAPI.getAPI(), 0, 1L);
			new KickRunnable().runTaskTimer(GameAPI.getAPI(), 0, 20L);

		} else if(size == 0){
			cancel();
			Bukkit.shutdown();
			return;
		}

		time++;

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
