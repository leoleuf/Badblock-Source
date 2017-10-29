package fr.badblock.bukkit.games.survivalgames.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import fr.badblock.bukkit.games.survivalgames.PluginSurvival;
import fr.badblock.bukkit.games.survivalgames.SGAchievementList;
import fr.badblock.bukkit.games.survivalgames.players.SurvivalData;
import fr.badblock.bukkit.games.survivalgames.players.SurvivalScoreboard;
import fr.badblock.bukkit.games.survivalgames.runnables.GameRunnable;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.events.fakedeaths.FakeDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.FightingDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.FightingDeathEvent.FightingDeaths;
import fr.badblock.gameapi.events.fakedeaths.NormalDeathEvent;
import fr.badblock.gameapi.game.rankeds.RankedManager;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;

public class DeathListener extends BadListener {
	@EventHandler
	public void onDeath(NormalDeathEvent e){
		death(e, e.getPlayer(), null, e.getLastDamageCause());
		e.setDeathMessage(GameMessages.deathEventMessage(e));
	}
	
	@EventHandler
	public void onDeath(FightingDeathEvent e){
		if(!inGame())
		{
			e.setCancelled(true);
			return;
		}
		
		death(e, e.getPlayer(), e.getKiller(), e.getLastDamageCause());
		e.setDeathMessage(GameMessages.deathEventMessage(e));
		
		if(e.getKiller().getType() == EntityType.PLAYER){
			BadblockPlayer killer = (BadblockPlayer) e.getKiller();
			killer.getPlayerData().incrementAchievements(killer, SGAchievementList.SG_KILL_1, SGAchievementList.SG_KILL_2, SGAchievementList.SG_KILL_3, SGAchievementList.SG_KILL_4, SGAchievementList.SG_BKILLER, SGAchievementList.SG_BSURVIVOR);

			if(e.getFightType() == FightingDeaths.BOW){
				killer.getPlayerData().incrementAchievements(killer, SGAchievementList.SG_SHOOTER, SGAchievementList.SG_USHOOTER);
			}
		}
	}

	private Map<String, Long> lastDeath = new HashMap<>();
	
	private void death(FakeDeathEvent e, BadblockPlayer player, Entity killer, DamageCause last){
		if (lastDeath.containsKey(player.getName())) {
			if (lastDeath.get(player.getName()) > System.currentTimeMillis()) {
				e.setDeathMessage(null);
				e.setDeathMessageEnd(null);
			}
		}
		lastDeath.put(player.getName(), System.currentTimeMillis() + 1000L);
		Location respawnPlace = null;
		if (player.getOpenInventory() != null && player.getOpenInventory().getCursor() != null)
			player.getOpenInventory().setCursor(null);

		player.getPlayerData().addRankedPoints(-2);
		player.getPlayerData().incrementStatistic("survival", SurvivalScoreboard.DEATHS);
		player.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), SurvivalScoreboard.DEATHS, 1);
		player.inGameData(SurvivalData.class).death		= true;
		player.inGameData(SurvivalData.class).deathTime = GameRunnable.generalTime;
		GameAPI.getAPI().getOnlinePlayers().stream().filter(badblockPlayer -> badblockPlayer.getCustomObjective() != null).forEach(badblockPlayer -> badblockPlayer.getCustomObjective().generate());
		
		e.setLightning(true);
			
		player.setBadblockMode(BadblockMode.SPECTATOR);
		e.setTimeBeforeRespawn(0);
			
		if(killer == null){
			respawnPlace = PluginSurvival.getInstance().getMapConfiguration().getSpawnLocation();
		} else {
			respawnPlace = killer.getLocation();
		}

		if(killer != null && killer.getType() == EntityType.PLAYER){
			BadblockPlayer bKiller = (BadblockPlayer) killer;
			bKiller.getPlayerData().incrementStatistic("survival", SurvivalScoreboard.KILLS);
			bKiller.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), SurvivalScoreboard.KILLS, 1);
			bKiller.inGameData(SurvivalData.class).kills++;
			
			bKiller.getCustomObjective().generate();
		}
		
		player.getPlayerData().incrementStatistic("survival", SurvivalScoreboard.LOOSES);
		player.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), SurvivalScoreboard.LOOSES, 1);
		player.getCustomObjective().generate();
		e.setRespawnPlace(respawnPlace);
		player.postResult(null);
	}
}
