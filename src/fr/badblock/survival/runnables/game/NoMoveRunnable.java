package fr.badblock.survival.runnables.game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.data.InGameKitData;
import fr.badblock.gameapi.players.kits.PlayerKit;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;
import fr.badblock.survival.PluginSurvival;
import fr.badblock.survival.SGAchievementList;
import fr.badblock.survival.configuration.SurvivalMapConfiguration;
import fr.badblock.survival.players.SurvivalScoreboard;
import fr.badblock.survival.players.TimeProvider;
import fr.badblock.survival.runnables.StartRunnable;

public class NoMoveRunnable extends BukkitRunnable implements TimeProvider {
	public static final int NOMOVE_TIME = 15;
	public static   boolean noMove		= false;

	private 			int time		= NOMOVE_TIME;

	public NoMoveRunnable(SurvivalMapConfiguration config){
		SurvivalScoreboard.setTimeProvider(this);

		GameAPI.getAPI().getGameServer().setGameState(GameState.RUNNING);
		GameAPI.getAPI().getGameServer().saveTeamsAndPlayersForResult();

		Bukkit.getWorlds().forEach(world -> {
			world.setTime(config.getTime());
			world.getEntities().forEach(entity -> {
				if(entity.getType() != EntityType.PLAYER)
					entity.remove();
			});
		});

		for(BadblockPlayer p : GameAPI.getAPI().getOnlinePlayers()){
			p.changePlayerDimension(BukkitUtils.getEnvironment( config.getDimension() ));

			p.undisguise();
			p.setMaxHealth(20.0d);
			p.heal();

			boolean good = true;


			for(PlayerKit toUnlock : PluginSurvival.getInstance().getKits().values()){
				if(!toUnlock.isVIP()){
					if(p.getPlayerData().getUnlockedKitLevel(toUnlock) < 2){
						good = false; break;
					}
				}
			}

			if(good && !p.getPlayerData().getAchievementState(SGAchievementList.SG_ALLKITS).isSucceeds()){
				p.getPlayerData().getAchievementState(SGAchievementList.SG_ALLKITS).succeed();
				SGAchievementList.SG_ALLKITS.reward(p);
			}

			PlayerKit kit = p.inGameData(InGameKitData.class).getChoosedKit();

			if(kit != null){
				kit.giveKit(p);
			} else {
				p.clearInventory();
			}
			if (!PluginSurvival.getInstance().getMapConfiguration().isWithTeam()) {
				p.sendTranslatedTitle("survival.title_withteam");
				p.sendTimings(2, 20*5, 2);
			}
		}

		BukkitUtils.teleportPlayersToLocations(PluginSurvival.getInstance().getMapConfiguration().getLocations(), null, player -> {
			return true;
		});


		GameAPI.getAPI().getJoinItems().doClearInventory(false);
		GameAPI.getAPI().getJoinItems().end();

		noMove = true;
	}

	@Override
	public void run() {
		time--;

		ChatColor 		   color 	 = StartRunnable.getColor(time);
		TranslatableString actionbar = GameMessages.startInActionBar(time, color);

		BukkitUtils.forEachPlayers(player -> {
			if(time > 0)
				player.sendTranslatedActionBar(actionbar.getKey(), actionbar.getObjects());

			player.setLevel(time);
			player.setExp(0.0f);			
		});

		if( (time % 10 == 0 || time <= 5) && time > 0 && time <= 30){
			TranslatableString title = new TranslatableString("survival.movein.title", time, color.getChar());

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;

				bPlayer.sendTranslatedTitle(title.getKey(), title.getObjects());
				bPlayer.sendTimings(2, 30, 2);
			}
		} else if(time == 0){
			cancel();

			noMove = false;

			TranslatableString title = new TranslatableString("survival.move.title");

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;

				bPlayer.sendTranslatedTitle(title.getKey(), title.getObjects());
				bPlayer.sendTimings(2, 30, 2);
				bPlayer.sendTranslatedMessage("survival.scoreboard.teams_message_" + PluginSurvival.getInstance().getMapConfiguration().isWithTeam());
			}

			GameAPI.getAPI().getChestGenerator().beginJob();

			new PvPRunnable().runTaskTimer(GameAPI.getAPI(), 20L, 20L);
		}
	}

	@Override
	public String getId(int num) {
		return "nomove";
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
