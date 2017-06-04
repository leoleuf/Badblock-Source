package fr.badblock.bukkit.games.badsoup.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import fr.badblock.bukkit.games.badsoup.PluginSoup;
import fr.badblock.bukkit.games.badsoup.SPAchievementList;
import fr.badblock.bukkit.games.badsoup.players.SoupData;
import fr.badblock.bukkit.games.badsoup.players.SoupScoreboard;
import fr.badblock.bukkit.games.badsoup.runnables.GameRunnable;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.events.fakedeaths.FakeDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.FightingDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.FightingDeathEvent.FightingDeaths;
import fr.badblock.gameapi.events.fakedeaths.NormalDeathEvent;
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
		death(e, e.getPlayer(), e.getKiller(), e.getLastDamageCause());
		e.setDeathMessage(GameMessages.deathEventMessage(e));
		
		if(e.getKiller().getType() == EntityType.PLAYER){
			BadblockPlayer killer = (BadblockPlayer) e.getKiller();
			killer.getPlayerData().incrementAchievements(killer, SPAchievementList.SG_KILL_1, SPAchievementList.SG_KILL_2, SPAchievementList.SG_KILL_3, SPAchievementList.SG_KILL_4, SPAchievementList.SG_BKILLER, SPAchievementList.SG_BSURVIVOR);

			if(e.getFightType() == FightingDeaths.BOW){
				killer.getPlayerData().incrementAchievements(killer, SPAchievementList.SG_SHOOTER, SPAchievementList.SG_USHOOTER);
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
		player.getPlayerData().incrementStatistic("survival", SoupScoreboard.DEATHS);
		player.inGameData(SoupData.class).death		= true;
		player.inGameData(SoupData.class).deathTime = GameRunnable.generalTime;
		GameAPI.getAPI().getOnlinePlayers().stream().filter(badblockPlayer -> badblockPlayer.getCustomObjective() != null).forEach(badblockPlayer -> badblockPlayer.getCustomObjective().generate());
		
		e.setLightning(true);
			
		player.setBadblockMode(BadblockMode.SPECTATOR);
		e.setTimeBeforeRespawn(0);
			
		if(killer == null){
			respawnPlace = PluginSoup.getInstance().getMapConfiguration().getSpawnLocation();
		} else {
			respawnPlace = killer.getLocation();
		}

		if(killer != null && killer.getType() == EntityType.PLAYER){
			BadblockPlayer bKiller = (BadblockPlayer) killer;
			bKiller.getPlayerData().incrementStatistic("survival", SoupScoreboard.KILLS);
			bKiller.inGameData(SoupData.class).kills++;
			
			bKiller.getCustomObjective().generate();
		}
		
		player.getPlayerData().incrementStatistic("survival", SoupScoreboard.LOOSES);
		player.getCustomObjective().generate();
		e.setRespawnPlace(respawnPlace);
	}
}
