package fr.badblock.bukkit.games.rush.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import fr.badblock.bukkit.games.rush.PluginRush;
import fr.badblock.bukkit.games.rush.RushAchievementList;
import fr.badblock.bukkit.games.rush.entities.RushTeamData;
import fr.badblock.bukkit.games.rush.players.RushData;
import fr.badblock.bukkit.games.rush.players.RushScoreboard;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.events.fakedeaths.FakeDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.FightingDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.FightingDeathEvent.FightingDeaths;
import fr.badblock.gameapi.events.fakedeaths.NormalDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.PlayerFakeRespawnEvent;
import fr.badblock.gameapi.game.rankeds.RankedManager;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.players.data.PlayerAchievementState;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;

public class DeathListener extends BadListener {
	@EventHandler
	public void onDeath(NormalDeathEvent e){
		death(e, e.getPlayer(), null, e.getLastDamageCause());
		e.setDeathMessage(GameMessages.deathEventMessage(e));
	}

	@EventHandler
	public void onDeath(FightingDeathEvent e){
		death(e, e.getPlayer(), e.getKiller(), e.getLastDamageCause());
		e.setDeathMessage(GameMessages.deathEventMessage(e));

		if(e.getKiller().getType() == EntityType.PLAYER){
			BadblockPlayer killer = (BadblockPlayer) e.getKiller();
			incrementAchievements(killer, RushAchievementList.RUSH_KILL_1, RushAchievementList.RUSH_KILL_2, RushAchievementList.RUSH_KILL_3, RushAchievementList.RUSH_KILL_4, RushAchievementList.RUSH_KILLER, RushAchievementList.RUSH_UKILLER);

			if(e.getFightType() == FightingDeaths.BOW){
				incrementAchievements(killer, RushAchievementList.RUSH_SHOOTER, RushAchievementList.RUSH_USHOOTER);
			}
		}
	}

	private Map<String, Long> lastDeath = new HashMap<>();

	private void death(FakeDeathEvent e, BadblockPlayer player, Entity killer, DamageCause last){
		if(player.getTeam() == null) return; //WTF
		if (lastDeath.containsKey(player.getName())) {
			if (lastDeath.get(player.getName()) > System.currentTimeMillis()) {
				e.setDeathMessage(null);
				e.setDeathMessageEnd(null);
				e.setCancelled(true);
				return;
			}
		}
		lastDeath.put(player.getName(), System.currentTimeMillis() + 1000L);
		if (player.getOpenInventory() != null && player.getOpenInventory().getCursor() != null)
			player.getOpenInventory().setCursor(null);

		Location respawnPlace = null;

		player.getPlayerData().incrementStatistic("rush", RushScoreboard.DEATHS);
		player.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), RushScoreboard.DEATHS, 1);
		player.inGameData(RushData.class).deaths++;
		player.getCustomObjective().generate();

		if(player.getTeam().teamData(RushTeamData.class).getFirstBedPart() == null){
			player.getPlayerData().incrementStatistic("rush", RushScoreboard.LOOSES);
			player.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), RushScoreboard.LOOSES, 1);
			BadblockTeam team = player.getTeam();

			e.setDeathMessageEnd(new TranslatableString("rush.player-loose", player.getName(), team.getChatName()));

			player.sendTranslatedTitle("rush.player-loose-title");
			player.sendTimings(20, 80, 20);
			e.setLightning(true);

			team.leaveTeam(player);

			if(team.getOnlinePlayers().size() == 0){
				GameAPI.getAPI().getGameServer().cancelReconnectionInvitations(team);
				GameAPI.getAPI().unregisterTeam(team);

				GameAPI.getAPI().getOnlinePlayers().forEach(p -> {

				});

				new TranslatableString("rush.team-loose", team.getChatName()).broadcast();;
			}

			player.setBadblockMode(BadblockMode.SPECTATOR);
			e.setTimeBeforeRespawn(0);
			player.postResult(null);
			if(killer == null){
				respawnPlace = PluginRush.getInstance().getMapConfiguration().getSpawnLocation();
			} else {
				respawnPlace = killer.getLocation();
			}
		} else {
			e.setTimeBeforeRespawn(3);
			respawnPlace = player.getTeam().teamData(RushTeamData.class).getRespawnLocation();

			player.setMaxHealth(20 + player.getTeam().teamData(RushTeamData.class).health);
			player.setHealth(20 + player.getTeam().teamData(RushTeamData.class).health);
			if(killer != null){
				e.setWhileRespawnPlace(killer.getLocation());
			} else if(last == DamageCause.VOID){
				e.setWhileRespawnPlace(respawnPlace);
			}
		}

		if(killer != null && killer.getType() == EntityType.PLAYER){
			BadblockPlayer bKiller = (BadblockPlayer) killer;
			bKiller.getPlayerData().incrementStatistic("rush", RushScoreboard.KILLS);
			bKiller.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), RushScoreboard.KILLS, 1);
			bKiller.inGameData(RushData.class).kills++;
			if (bKiller.getCustomObjective() != null)
				bKiller.getCustomObjective().generate();
		}

		player.getCustomObjective().generate();
		e.setRespawnPlace(respawnPlace);
	}

	@EventHandler
	public void onRespawn(PlayerFakeRespawnEvent e){
		if (e.getPlayer().getOpenInventory() != null && e.getPlayer().getOpenInventory().getCursor() != null)
			e.getPlayer().getOpenInventory().setCursor(null);
		PluginRush.getInstance().giveDefaultKit(e.getPlayer());
	}

	private void incrementAchievements(BadblockPlayer player, PlayerAchievement... achievements){
		for(PlayerAchievement achievement : achievements){
			PlayerAchievementState state = player.getPlayerData().getAchievementState(achievement);
			state.progress(1.0d);
			state.trySucceed(player, achievement);
		}
		player.saveGameData();
	}
}
