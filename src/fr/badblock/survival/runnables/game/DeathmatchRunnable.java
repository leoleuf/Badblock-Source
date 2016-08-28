package fr.badblock.survival.runnables.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.data.PlayerAchievementState;
import fr.badblock.gameapi.utils.general.TimeUnit;
import fr.badblock.survival.PluginSurvival;
import fr.badblock.survival.SGAchievementList;
import fr.badblock.survival.players.SurvivalData;
import fr.badblock.survival.players.SurvivalScoreboard;
import fr.badblock.survival.players.TimeProvider;
import fr.badblock.survival.result.SurvivalResults;
import fr.badblock.survival.runnables.EndEffectRunnable;
import fr.badblock.survival.runnables.GameRunnable;
import fr.badblock.survival.runnables.KickRunnable;

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

		for(Player p : Bukkit.getOnlinePlayers()){
			BadblockPlayer player = (BadblockPlayer) p;

			if(!player.inGameData(SurvivalData.class).death){
				winner = player;
				players++;
			}
		}
		
		if(players == 1 || time == 0){
			cancel();
			
			GameAPI.getAPI().getGameServer().setGameState(GameState.FINISHED);

			Location winnerLocation = PluginSurvival.getInstance().getMapConfiguration().getSpawnLocation();
			Location looserLocation = winnerLocation.clone().add(0d, 7d, 0d);

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bp = (BadblockPlayer) player;
				bp.heal();
				bp.clearInventory();
				bp.setInvulnerable(true);

				double badcoins = bp.inGameData(SurvivalData.class).getScore() / 10;
				double xp	    = bp.inGameData(SurvivalData.class).getScore() / 5;

				if(winner != null && winner.equals(bp)){
					bp.teleport(winnerLocation);
					bp.setAllowFlight(true);
					bp.setFlying(true);
					bp.sendTranslatedTitle("survival.title-win", winner.getName());
					bp.getPlayerData().incrementStatistic("survival", SurvivalScoreboard.WINS);

					bp.getPlayerData().incrementAchievements(bp, SGAchievementList.SG_WIN_1, SGAchievementList.SG_WIN_2, SGAchievementList.SG_WIN_3, SGAchievementList.SG_WIN_4);
				
					PlayerAchievementState state = bp.getPlayerData().getAchievementState(SGAchievementList.SG_BSURVIVOR);
					
					if(state.getProgress() >= 9){
						state.progress(1000.0d);
						state.trySucceed(bp, SGAchievementList.SG_BSURVIVOR);
					}
					
					if(bp.inGameData(SurvivalData.class).kills >= 5){
						bp.getPlayerData().incrementAchievements(bp, SGAchievementList.SG_FEERL_1, SGAchievementList.SG_FEERL_2, SGAchievementList.SG_FEERL_3, SGAchievementList.SG_FEERL_4);
					}
				} else {
					badcoins = ((double) badcoins) / 1.5d;

					bp.jailPlayerAt(looserLocation);
					bp.sendTranslatedTitle("survival.title-loose", winner.getName());
					bp.getPlayerData().incrementAchievements(bp, SGAchievementList.SG_LOOSER);
				}

				if(badcoins > 20)
					badcoins = 20;
				if(xp > 50)
					xp = 50;

				int rbadcoins = badcoins < 2 ? 2 : (int) badcoins;
				int rxp		  = xp < 5 ? 5 : (int) xp;

				bp.getPlayerData().addBadcoins(rbadcoins, true);
				bp.getPlayerData().addXp(rxp, true);

				bp.inGameData(SurvivalData.class).checkSword(bp);
				
				if(bp.inGameData(SurvivalData.class).canSword){
					bp.getPlayerData().incrementAchievements(bp, SGAchievementList.SG_ROOKIE);
				}
				
				if(bp.inGameData(SurvivalData.class).receivedDamage == 0.0d){
					bp.getPlayerData().incrementAchievements(bp, SGAchievementList.SG_HALF_GOD);
				}
				
				new BukkitRunnable(){

					@Override
					public void run(){
						if(bp.isOnline()){
							bp.sendTranslatedActionBar("survival.win", rbadcoins, rxp);
						}
					}

				}.runTaskTimer(GameAPI.getAPI(), 0, 30L);

				bp.getCustomObjective().generate();
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