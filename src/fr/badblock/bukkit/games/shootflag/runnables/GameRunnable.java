package fr.badblock.bukkit.games.shootflag.runnables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.shootflag.PluginShootFlag;
import fr.badblock.bukkit.games.shootflag.ShootFlagAchievementList;
import fr.badblock.bukkit.games.shootflag.configuration.ShootFlagMapConfiguration;
import fr.badblock.bukkit.games.shootflag.entities.ShootFlagTeamData;
import fr.badblock.bukkit.games.shootflag.listeners.JoinListener;
import fr.badblock.bukkit.games.shootflag.players.ShootFlagData;
import fr.badblock.bukkit.games.shootflag.players.ShootFlagScoreboard;
import fr.badblock.bukkit.games.shootflag.result.ShootFlagResults;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.players.data.PlayerAchievementState;
import fr.badblock.gameapi.players.scoreboard.CustomObjective;
import fr.badblock.gameapi.utils.general.TimeUnit;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import lombok.Getter;

public class GameRunnable extends BukkitRunnable {
	public static int MAX_TIME = 60 * 10;
	public static boolean damage = false;

	public boolean forceEnd 		 = false;
	@Getter
	private int    time 			 = MAX_TIME;

	public GameRunnable(ShootFlagMapConfiguration config){
		MAX_TIME = config.getTime();
		GameAPI.getAPI().getGameServer().setGameState(GameState.RUNNING);
		GameAPI.getAPI().getGameServer().saveTeamsAndPlayersForResult();

		Bukkit.getWorlds().forEach(world -> {
			world.setTime(config.getTime());
		});

		for(BadblockTeam team : GameAPI.getAPI().getTeams()){

			Location location = team.teamData(ShootFlagTeamData.class).getSpawnLocation();
			location.getChunk().load();

			for(BadblockPlayer p : team.getOnlinePlayers()){
				JoinListener.handle(p);
			}

		}

		GameAPI.getAPI().getJoinItems().doClearInventory(false);
		GameAPI.getAPI().getJoinItems().end();
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
		GameAPI.setJoinable(time > MAX_TIME / 2);
		if(time == MAX_TIME - 2){
			damage = true;

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bp = (BadblockPlayer) player;
				bp.pseudoJail(bp.getTeam().teamData(ShootFlagTeamData.class).getSpawnLocation(), 300.0d);
			}
		} else if(time == 0){
			forceEnd = true;
		}

		int size = GameAPI.getAPI().getTeams().size();

		List<BadblockTeam> to  = new ArrayList<>();
		BadblockTeam	   max = null;

		Bukkit.getOnlinePlayers().forEach(player -> {
			CustomObjective obj = ((BadblockPlayer) player).getCustomObjective();
			if(obj != null)
				obj.generate();
		});

		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			if(team.getOnlinePlayers().size() == 0){
				GameAPI.getAPI().getGameServer().cancelReconnectionInvitations(team);
				to.add(team);

				new TranslatableString("shootflag.team-loose", team.getChatName()).broadcast();;
			} else if(max == null || max.teamData(ShootFlagTeamData.class).getPoints() < team.teamData(ShootFlagTeamData.class).getPoints())
				max = team;
		}

		to.forEach(GameAPI.getAPI()::unregisterTeam);

		if(GameAPI.getAPI().getTeams().stream().filter(team -> team.playersCurrentlyOnline() > 0).count() <= 1 || forceEnd){
			cancel();
			BadblockTeam winner = max;

			GameAPI.getAPI().getGameServer().setGameState(GameState.FINISHED);
			
			Location winnerLocation = PluginShootFlag.getInstance().getMapConfiguration().getSpawnLocation();
			Location looserLocation = winnerLocation.clone().add(0d, 7d, 0d);

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bp = (BadblockPlayer) player;
				bp.heal();
				bp.clearInventory();
				bp.setInvulnerable(true);

				double badcoins = bp.inGameData(ShootFlagData.class).getScore() / 4;
				double xp	    = bp.inGameData(ShootFlagData.class).getScore() / 2;

				if(winner != null && winner.equals(bp.getTeam())){
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
					bp.sendTranslatedTitle("shootflag.title-win", winner.getChatName());
					bp.getPlayerData().incrementStatistic("shootflag", ShootFlagScoreboard.WINS);

					incrementAchievements(bp, ShootFlagAchievementList.SHOOTFLAG_WIN_1, ShootFlagAchievementList.SHOOTFLAG_WIN_2, ShootFlagAchievementList.SHOOTFLAG_WIN_3, ShootFlagAchievementList.SHOOTFLAG_WIN_4);
				} else {
					bp.getPlayerData().addRankedPoints(-2);
					badcoins = ((double) badcoins) / 1.5d;

					bp.jailPlayerAt(looserLocation);
					bp.sendTranslatedTitle("shootflag.title-loose", winner.getChatName());

					if(bp.getBadblockMode() == BadblockMode.PLAYER)
						bp.getPlayerData().incrementStatistic("shootflag", ShootFlagScoreboard.LOOSES);
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
							bp.sendTranslatedActionBar("shootflag.win", rbadcoins, rxp);
						}
					}

				}.runTaskTimer(GameAPI.getAPI(), 0, 30L);

				if(bp.getCustomObjective() != null)
					bp.getCustomObjective().generate();
			}

			new ShootFlagResults(TimeUnit.SECOND.toShort(time, TimeUnit.SECOND, TimeUnit.HOUR), winner);
			new EndEffectRunnable(winnerLocation, winner).runTaskTimer(GameAPI.getAPI(), 0, 1L);
			new KickRunnable().runTaskTimer(GameAPI.getAPI(), 0, 20L);

		} else if(size == 0){
			cancel();
			Bukkit.shutdown();
			return;
		}

		time--;
	}

	private static void incrementAchievements(BadblockPlayer player, PlayerAchievement... achievements){
		for(PlayerAchievement achievement : achievements){
			PlayerAchievementState state = player.getPlayerData().getAchievementState(achievement);
			state.progress(1.0d);
			state.trySucceed(player, achievement);
		}
	}
}
