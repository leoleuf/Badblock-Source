package fr.badblock.bukkit.games.survivalgames.runnables.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.survivalgames.PluginSurvival;
import fr.badblock.bukkit.games.survivalgames.SGAchievementList;
import fr.badblock.bukkit.games.survivalgames.players.SurvivalData;
import fr.badblock.bukkit.games.survivalgames.players.SurvivalScoreboard;
import fr.badblock.bukkit.games.survivalgames.players.TimeProvider;
import fr.badblock.bukkit.games.survivalgames.result.SurvivalResults;
import fr.badblock.bukkit.games.survivalgames.runnables.EndEffectRunnable;
import fr.badblock.bukkit.games.survivalgames.runnables.GameRunnable;
import fr.badblock.bukkit.games.survivalgames.runnables.KickRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.data.PlayerAchievementState;
import fr.badblock.gameapi.utils.general.TimeUnit;

public class DeathmatchRunnable extends BukkitRunnable implements TimeProvider {
	public static final int DEATHMATCH_TIME = 180;
	private 			int time			= DEATHMATCH_TIME;
	public static   boolean deathmatch		= false;

	public DeathmatchRunnable() {
		deathmatch = true;

		SurvivalScoreboard.setTimeProvider(this);
	}

	@Override
	public void run() {
		GameRunnable.generalTime++;
		time--;

		int 		   players = 0;
		BadblockPlayer winner  = null;

		for(BadblockPlayer p : GameAPI.getAPI().getRealOnlinePlayers()){

			if(!p.inGameData(SurvivalData.class).death){
				winner = p;
				players++;
			}
		}

		if(players == 1 || time == 0){
			cancel();

			GameAPI.getAPI().getGameServer().setGameState(GameState.FINISHED);

			Location winnerLocation = PluginSurvival.getInstance().getMapConfiguration().getSpawnLocation();
			Location looserLocation = winnerLocation.clone().add(0d, 7d, 0d);

			for(BadblockPlayer player : GameAPI.getAPI().getOnlinePlayers()){
				try {
					player.heal();
					player.clearInventory();
					player.setInvulnerable(true);

					double badcoins = player.inGameData(SurvivalData.class).getScore() / 4;
					double xp	    = player.inGameData(SurvivalData.class).getScore() / 2;

					if(winner != null && winner.equals(player)){
						player.getPlayerData().addRankedPoints(3);
						player.teleport(winnerLocation);
						player.setAllowFlight(true);
						player.setFlying(true);

						new BukkitRunnable() {
							int count = 5;

							@Override
							public void run() {
								count--;

								player.teleport(winnerLocation);
								player.setAllowFlight(true);
								player.setFlying(true);

								if(count == 0)
									cancel();
							}
						}.runTaskTimer(GameAPI.getAPI(), 5L, 5L);

						player.sendTranslatedTitle("survival.title-win", winner.getName());
						player.getPlayerData().incrementStatistic("survival", SurvivalScoreboard.WINS);

						player.getPlayerData().incrementAchievements(player, SGAchievementList.SG_WIN_1, SGAchievementList.SG_WIN_2, SGAchievementList.SG_WIN_3, SGAchievementList.SG_WIN_4);

						PlayerAchievementState state = player.getPlayerData().getAchievementState(SGAchievementList.SG_BSURVIVOR);

						if(state.getProgress() >= 9){
							state.progress(1000.0d);
							state.trySucceed(player, SGAchievementList.SG_BSURVIVOR);
						}
						if(player.inGameData(SurvivalData.class).kills >= 5){
							player.getPlayerData().incrementAchievements(player, SGAchievementList.SG_FEERL_1, SGAchievementList.SG_FEERL_2, SGAchievementList.SG_FEERL_3, SGAchievementList.SG_FEERL_4);
						}
					} else {
						badcoins = ((double) badcoins) / 1.5d;

						player.jailPlayerAt(looserLocation);
						player.sendTranslatedTitle("survival.title-loose", winner.getName());
						player.getPlayerData().incrementAchievements(player, SGAchievementList.SG_LOOSER);
					}
					
					if(badcoins > 20 * player.getPlayerData().getBadcoinsMultiplier())
						badcoins = 20 * player.getPlayerData().getBadcoinsMultiplier();
					if(xp > 50 * player.getPlayerData().getXpMultiplier())
						xp = 50 * player.getPlayerData().getXpMultiplier();

					int rbadcoins = badcoins < 2 ? 2 : (int) badcoins;
					int rxp		  = xp < 5 ? 5 : (int) xp;

					player.getPlayerData().addBadcoins(rbadcoins, true);
					player.getPlayerData().addXp(rxp, true);

					player.inGameData(SurvivalData.class).checkSword(player);

					if(player.inGameData(SurvivalData.class).canSword){
						player.getPlayerData().incrementAchievements(player, SGAchievementList.SG_ROOKIE);
					}

					if(player.inGameData(SurvivalData.class).receivedDamage == 0.0d){
						player.getPlayerData().incrementAchievements(player, SGAchievementList.SG_HALF_GOD);
					}

					new BukkitRunnable(){

						@Override
						public void run(){
							if(player.isOnline()){
								player.sendTranslatedActionBar("survival.win", rbadcoins, rxp);
							}
						}

					}.runTaskTimer(GameAPI.getAPI(), 0, 30L);

					player.getCustomObjective().generate();
				} catch(Exception e){
					e.printStackTrace();
				}
			}

			new SurvivalResults(TimeUnit.SECOND.toShort(GameRunnable.generalTime, TimeUnit.SECOND, TimeUnit.HOUR));
			new EndEffectRunnable(winner).runTaskTimer(GameAPI.getAPI(), 0, 1L);
			new KickRunnable().runTaskTimer(GameAPI.getAPI(), 0, 20L);
		} else if(players == 0){
			cancel();
			Bukkit.shutdown();
			return;
		}
	}

	@Override
	public String getId(int num) {
		return "deathmatch";
	}

	@Override
	public int getTime(int num) {
		return time;
	}

	@Override
	public int getProvidedCount() {
		return 1;
	}
}