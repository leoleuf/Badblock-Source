package fr.badblock.rush.runnables;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.players.data.InGameKitData;
import fr.badblock.gameapi.players.data.PlayerAchievementState;
import fr.badblock.gameapi.players.kits.PlayerKit;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.general.TimeUnit;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.rush.PluginRush;
import fr.badblock.rush.RushAchievementList;
import fr.badblock.rush.configuration.RushConfiguration.SpawnableItem;
import fr.badblock.rush.configuration.RushMapConfiguration;
import fr.badblock.rush.entities.RushTeamData;
import fr.badblock.rush.players.RushData;
import fr.badblock.rush.players.RushScoreboard;
import fr.badblock.rush.result.RushResults;

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

		Bukkit.getWorlds().forEach(world -> {
			world.setTime(config.getTime());
			world.getEntities().forEach(entity -> {
				if(entity.getType() != EntityType.PLAYER)
					entity.remove();
			});
		});

		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			
			Location location = team.teamData(RushTeamData.class).getRespawnLocation();
			
			for(BadblockPlayer p : team.getOnlinePlayers()){
				p.changePlayerDimension(BukkitUtils.getEnvironment( config.getDimension() ));
				p.teleport(location);
				p.setGameMode(GameMode.SURVIVAL);

				boolean good = true;
				
				
				for(PlayerKit toUnlock : PluginRush.getInstance().getKits().values()){
					if(!toUnlock.isVIP()){
						if(p.getPlayerData().getUnlockedKitLevel(toUnlock) < 2){
							good = false; break;
						}
					}
				}
				
				if(good && !p.getPlayerData().getAchievementState(RushAchievementList.RUSH_ALLKITS).isSucceeds()){
					p.getPlayerData().getAchievementState(RushAchievementList.RUSH_ALLKITS).succeed();
					RushAchievementList.RUSH_ALLKITS.reward(p);
				}
				
				PlayerKit kit = p.inGameData(InGameKitData.class).getChoosedKit();
				
				if(kit != null){
					if (PluginRush.getInstance().getMapConfiguration().getAllowBows())
						kit.giveKit(p);
					else
						kit.giveKit(p, Material.BOW, Material.ARROW);
				} else {
					p.clearInventory();
				}
			}
			
		}
		
		GameAPI.getAPI().getJoinItems().doClearInventory(false);
		GameAPI.getAPI().getJoinItems().end();
	}

	@Override
	public void run() {
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
		
		to.forEach(GameAPI.getAPI()::unregisterTeam);
		
		if(size == 1 || forceEnd){
			cancel();
			BadblockTeam winner = GameAPI.getAPI().getTeams().iterator().next();

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
						bp.getPlayerData().incrementStatistic("rush", RushScoreboard.LOOSES);
				}
				if(badcoins > 20)
					badcoins = 20;
				if(xp > 50)
					xp = 50;

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
				
				bp.getCustomObjective().generate();
			}

			new RushResults(TimeUnit.SECOND.toShort(time, TimeUnit.SECOND, TimeUnit.HOUR), winner);
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
	}
}
