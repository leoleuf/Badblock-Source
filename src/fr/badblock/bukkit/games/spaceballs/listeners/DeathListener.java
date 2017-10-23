package fr.badblock.bukkit.games.spaceballs.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.games.spaceballs.PluginSB;
import fr.badblock.bukkit.games.spaceballs.SBAchievementList;
import fr.badblock.bukkit.games.spaceballs.entities.SpaceTeamData;
import fr.badblock.bukkit.games.spaceballs.players.SpaceData;
import fr.badblock.bukkit.games.spaceballs.players.SpaceScoreboard;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.events.fakedeaths.FakeDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.FightingDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.FightingDeathEvent.FightingDeaths;
import fr.badblock.gameapi.game.rankeds.RankedManager;
import fr.badblock.gameapi.events.fakedeaths.NormalDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.PlayerFakeRespawnEvent;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;

public class DeathListener extends BadListener {
	@EventHandler
	public void onRespawn(PlayerFakeRespawnEvent e){
		if (e.getPlayer().getOpenInventory() != null && e.getPlayer().getOpenInventory().getCursor() != null)
			e.getPlayer().getOpenInventory().setCursor(null);
		PluginSB.getInstance().giveDefaultKit(e.getPlayer());
	}

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
			killer.getPlayerData().incrementAchievements(killer, SBAchievementList.SB_KILL_1, SBAchievementList.SB_KILL_2, SBAchievementList.SB_KILL_3, SBAchievementList.SB_KILL_4);

			if(e.getFightType() == FightingDeaths.BOW){
				killer.getPlayerData().incrementAchievements(killer, SBAchievementList.SB_SHOOTER, SBAchievementList.SB_USHOOTER);
			}
		}
	}

	private Map<String, Long> lastDeath = new HashMap<>();

	private void death(FakeDeathEvent e, BadblockPlayer player, Entity killer, DamageCause last){
		if(player.getTeam() == null) return; //WTF
		if (lastDeath.containsKey(player.getName())) {
			if (lastDeath.get(player.getName()) > System.currentTimeMillis()) {
				e.setCancelled(true);
				return;
			}
		}
		lastDeath.put(player.getName(), System.currentTimeMillis() + 1000L);
		if (player.getOpenInventory() != null && player.getOpenInventory().getCursor() != null)
			player.getOpenInventory().setCursor(null);

		Location respawnPlace = null;

		player.getPlayerData().incrementStatistic("spaceballs", SpaceScoreboard.DEATHS);
		player.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), SpaceScoreboard.DEATHS, 1);
		player.inGameData(SpaceData.class).deaths++;
		player.getCustomObjective().generate();

		e.setTimeBeforeRespawn(5);
		respawnPlace = player.getTeam().teamData(SpaceTeamData.class).getRespawnLocation();

		if(killer != null){
			e.setWhileRespawnPlace(killer.getLocation());
		} else if(last == DamageCause.VOID){
			e.setWhileRespawnPlace(respawnPlace);
		}

		if(killer != null && killer.getType() == EntityType.PLAYER){
			BadblockPlayer bKiller = (BadblockPlayer) killer;
			bKiller.getPlayerData().incrementStatistic("spaceballs", SpaceScoreboard.KILLS);
			bKiller.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), SpaceScoreboard.KILLS, 1);
			bKiller.inGameData(SpaceData.class).kills++;

			bKiller.getCustomObjective().generate();
		}

		int diamonds = 0;

		for(int i=0;i<e.getDrops().size();i++){
			ItemStack item = e.getDrops().get(i);

			if(item.getType() == Material.FIREWORK){
				continue;
			}

			if(item.getType() == Material.DIAMOND){
				diamonds += item.getAmount();
			}

			e.getDrops().remove(i);
			i--;
		}

		e.setDeathMessageEnd(new TranslatableString("spaceballs.death-message-end", diamonds));

		player.getCustomObjective().generate();
		e.setRespawnPlace(respawnPlace);
	}
}
