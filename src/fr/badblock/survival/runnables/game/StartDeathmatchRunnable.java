package fr.badblock.survival.runnables.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameServer.WhileRunningConnectionTypes;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.survival.PluginSurvival;
import fr.badblock.survival.SGAchievementList;
import fr.badblock.survival.configuration.SurvivalMapConfiguration;
import fr.badblock.survival.players.SurvivalData;
import fr.badblock.survival.players.TimeProvider;
import fr.badblock.survival.runnables.GameRunnable;
import fr.badblock.survival.runnables.StartRunnable;

public class StartDeathmatchRunnable extends BukkitRunnable implements TimeProvider {
	public static final int BEFORE_TIME = 30;
	private 			int time		= BEFORE_TIME;
	
	public StartDeathmatchRunnable() {
		GameAPI.getAPI().getGameServer().cancelReconnectionInvitations();
		GameAPI.getAPI().getGameServer().whileRunningConnection(WhileRunningConnectionTypes.SPECTATOR);
	}
	
	@Override
	public void run() {
		GameRunnable.generalTime++;
		time--;

		if( (time % 10 == 0 || time <= 5) && time > 0 && time <= 30){
			ChatColor 		   color = StartRunnable.getColor(time);
			TranslatableString title = new TranslatableString("survival.movein.title", time, color.getChar());

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;

				bPlayer.sendTranslatedTitle(title.getKey(), title.getObjects());
				bPlayer.sendTimings(2, 30, 2);
			}
		} else if(time == 0){
			cancel();
			
			for(Player p : Bukkit.getOnlinePlayers()){
				BadblockPlayer player = (BadblockPlayer) p;

				SurvivalMapConfiguration config =  PluginSurvival.getInstance().getMapConfiguration();

				BukkitUtils.teleportPlayersToLocations(config.getDeathMatchs(), config.getSpecDeathmatch(), sd -> {
					SurvivalData data = sd.inGameData(SurvivalData.class);
					data.checkSword(sd);
					
					return !data.death;
				});

				if(!player.inGameData(SurvivalData.class).death){
					player.getPlayerData().incrementAchievements(player, SGAchievementList.SG_SURVI_1, SGAchievementList.SG_SURVI_2, SGAchievementList.SG_SURVI_3, SGAchievementList.SG_SURVI_4);
					player.heal();
				}
			}

			new DeathmatchRunnable().runTaskTimer(GameAPI.getAPI(), 20L, 20L);
		}
	}

	@Override
	public String getId(int num) {
		return "startdeathmatch";
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