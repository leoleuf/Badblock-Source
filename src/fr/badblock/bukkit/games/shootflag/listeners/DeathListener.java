package fr.badblock.bukkit.games.shootflag.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import fr.badblock.bukkit.games.shootflag.PluginShootFlag;
import fr.badblock.bukkit.games.shootflag.ShootFlagAchievementList;
import fr.badblock.bukkit.games.shootflag.players.ShootFlagData;
import fr.badblock.bukkit.games.shootflag.players.ShootFlagScoreboard;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.events.fakedeaths.FakeDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.FightingDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.NormalDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.PlayerFakeRespawnEvent;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.data.PlayerAchievementState;
import fr.badblock.gameapi.utils.BukkitUtils;
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
			incrementAchievements(killer, ShootFlagAchievementList.SHOOTFLAG_KILL_1, ShootFlagAchievementList.SHOOTFLAG_KILL_2, ShootFlagAchievementList.SHOOTFLAG_KILL_3, ShootFlagAchievementList.SHOOTFLAG_KILL_4, ShootFlagAchievementList.SHOOTFLAG_KILLER, ShootFlagAchievementList.SHOOTFLAG_UKILLER);
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerFakeRespawnEvent e){
		if (e.getPlayer().getOpenInventory() != null && e.getPlayer().getOpenInventory().getCursor() != null)
			e.getPlayer().getOpenInventory().setCursor(null);
		PluginShootFlag.getInstance().giveDefaultKit(e.getPlayer());
	}

	private Map<String, Long> lastDeath = new HashMap<>();
	
	private void death(FakeDeathEvent e, BadblockPlayer player, Entity killer, DamageCause last){
		e.getDrops().clear();
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
		
		Location respawnPlace = findRespawnPlace(player);

		player.getPlayerData().incrementStatistic("shootflag", ShootFlagScoreboard.DEATHS);
		player.inGameData(ShootFlagData.class).deaths++;
		player.getCustomObjective().generate();

		e.setTimeBeforeRespawn(0);

		if(killer != null){
			e.setWhileRespawnPlace(killer.getLocation());
		} else if(last == DamageCause.VOID){
			e.setWhileRespawnPlace(respawnPlace);
		}

		if(killer != null && killer.getType() == EntityType.PLAYER){
			BadblockPlayer bKiller = (BadblockPlayer) killer;
			bKiller.getPlayerData().incrementStatistic("shootflag", ShootFlagScoreboard.KILLS);
			bKiller.inGameData(ShootFlagData.class).kills++;

			bKiller.getCustomObjective().generate();
		}

		player.setHealth(20);
		player.setMaxHealth(20);
		player.getCustomObjective().generate();
		player.setWalkSpeed(0.45F);
		player.setGameMode(GameMode.ADVENTURE);
		e.setRespawnPlace(respawnPlace);
	}
	
	private Location findRespawnPlace(BadblockPlayer player)
	{
		Location location = null;
		double x = 0;
		for (Location l : PluginShootFlag.getInstance().getMapConfiguration().getRespawnLocations())
		{
			if (location != null)
			{
				for (BadblockPlayer p : BukkitUtils.getPlayers())
				{
					if (p.equals(player))
					{
						continue;
					}
					double b = l.distance(p.getLocation());
					if (b > x)
					{
						x = b;
						location = l;
					}
					else
					{
						break;
					}
				}
			}
			else
			{
				location = l;
				x = 1;
			}
		}
		return location;
	}

	private void incrementAchievements(BadblockPlayer player, PlayerAchievement... achievements){
		for(PlayerAchievement achievement : achievements){
			PlayerAchievementState state = player.getPlayerData().getAchievementState(achievement);
			state.progress(1.0d);
			state.trySucceed(player, achievement);
		}
	}
}
