package fr.badblock.bukkit.games.pvpbox.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import fr.badblock.bukkit.games.pvpbox.PvPBox;
import fr.badblock.bukkit.games.pvpbox.PvPBoxAchievementList;
import fr.badblock.bukkit.games.pvpbox.players.BoxPlayer;
import fr.badblock.bukkit.games.pvpbox.players.BoxScoreboard;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.events.fakedeaths.FakeDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.FightingDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.NormalDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.PlayerFakeRespawnEvent;
import fr.badblock.gameapi.game.rankeds.RankedManager;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.data.PlayerAchievementState;

public class PlayerDeathListener extends BadListener
{

	@EventHandler
	public void onDeath(NormalDeathEvent e)
	{
		death(e, e.getPlayer(), null, e.getLastDamageCause());
		e.setDeathMessage(null);
	}

	@EventHandler
	public void onDeath(FightingDeathEvent event)
	{
		death(event, event.getPlayer(), event.getKiller(), event.getLastDamageCause());
		event.setDeathMessage(null);
		
		BoxPlayer boxKilled = BoxPlayer.get(event.getPlayer());
		
		if (boxKilled != null)
		{
			boxKilled.setLastHit(0);
			boxKilled.setDeaths(boxKilled.getDeaths() + 1);
		}
		
		if (event.getKiller().getType() == EntityType.PLAYER)
		{
			BadblockPlayer killer = (BadblockPlayer) event.getKiller();

			double o = killer.getHealth() + 10;
			if (o > killer.getMaxHealth()) o = killer.getMaxHealth();
			killer.setHealth(o);
			
			BoxPlayer boxKiller = BoxPlayer.get(killer);

			if (boxKiller != null)
			{
				boxKiller.setKills(boxKiller.getKills() + 1);
				int add = 8;
				int bcoins = killer.getPlayerData().addBadcoins(add, true);

				boxKiller.setXp(boxKiller.getXp() + bcoins);

				killer.sendTranslatedMessage("pvpbox.xpkill", bcoins);
				killer.sendTranslatedMessage("pvpbox.badcoinsplaykill", add);

				if (bcoins > add)
				{
					killer.sendTranslatedMessage("pvpbox.xpkilladd", (bcoins - add));
					killer.sendTranslatedMessage("pvpbox.badcoinsplaykilladd", (bcoins - add));
				}

				incrementAchievements(killer, PvPBoxAchievementList.PVPBOX_KILL_1,
						PvPBoxAchievementList.PVPBOX_KILL_2,
						PvPBoxAchievementList.PVPBOX_KILL_3,
						PvPBoxAchievementList.PVPBOX_KILL_4,
						PvPBoxAchievementList.PVPBOX_KILL_5,
						PvPBoxAchievementList.PVPBOX_KILL_6,
						PvPBoxAchievementList.PVPBOX_KILL_7,
						PvPBoxAchievementList.PVPBOX_KILL_8,
						PvPBoxAchievementList.PVPBOX_KILL_9,
						PvPBoxAchievementList.PVPBOX_KILLER,
						PvPBoxAchievementList.PVPBOX_UKILLER);
			}

			BadblockPlayer player = event.getPlayer();
			String playerName = player.getName().toLowerCase();
			
			// Assist
			for (BoxPlayer bPlayer : BoxPlayer.players.values())
			{
				if (!bPlayer.getPlayer().isOnline())
				{
					continue;
				}

				if (bPlayer.getPlayer().equals(killer))
				{
					continue;
				}

				if (!bPlayer.getLastAttacks().containsKey(playerName))
				{
					continue;
				}

				long time = System.currentTimeMillis() - bPlayer.getLastAttacks().get(playerName);
				
				if (time > 3000)
				{
					continue;
				}

				bPlayer.setAssists(bPlayer.getAssists() + 1);
				int add = 4;
				int bcoins = killer.getPlayerData().addBadcoins(add, true);

				bPlayer.setXp(bPlayer.getXp() + bcoins);

				bPlayer.getPlayer().sendTranslatedMessage("pvpbox.xpassist", bcoins);
				bPlayer.getPlayer().sendTranslatedMessage("pvpbox.badcoinsplayassist", add);

				if (bcoins > add)
				{
					bPlayer.getPlayer().sendTranslatedMessage("pvpbox.xpassistadd", (bcoins - add));
					bPlayer.getPlayer().sendTranslatedMessage("pvpbox.badcoinsplayassistadd", (bcoins - add));
				}

				incrementAchievements(bPlayer.getPlayer(), PvPBoxAchievementList.PVPBOX_ASSISTS_1,
						PvPBoxAchievementList.PVPBOX_ASSISTS_2,
						PvPBoxAchievementList.PVPBOX_ASSISTS_3,
						PvPBoxAchievementList.PVPBOX_ASSISTS_4,
						PvPBoxAchievementList.PVPBOX_ASSISTS_5,
						PvPBoxAchievementList.PVPBOX_ASSISTS_6,
						PvPBoxAchievementList.PVPBOX_ASSISTS_7,
						PvPBoxAchievementList.PVPBOX_ASSISTS_8,
						PvPBoxAchievementList.PVPBOX_ASSISTS_9,
						PvPBoxAchievementList.PVPBOX_ASSIST
						);
				
				double v = bPlayer.getPlayer().getHealth() + 10;
				if (v > bPlayer.getPlayer().getMaxHealth()) v = bPlayer.getPlayer().getMaxHealth();
				bPlayer.getPlayer().setHealth(v);
			}
			
		}
	}

	private Map<String, Long> lastDeath = new HashMap<>();

	private void death(FakeDeathEvent e, BadblockPlayer player, Entity killer, DamageCause last){
		e.setLightning(false);
		if(player.getTeam() == null) return; //WTF
		if (lastDeath.containsKey(player.getName())) {
			if (lastDeath.get(player.getName()) > System.currentTimeMillis()) {
				e.setDeathMessage(null);
				e.setDeathMessageEnd(null);
				e.setCancelled(true);
				return;
			}
		}

		BoxPlayer boxPlayer = BoxPlayer.get(player);

		if (boxPlayer == null)
		{
			return;
		}

		lastDeath.put(player.getName(), System.currentTimeMillis() + 1000L);
		if (player.getOpenInventory() != null && player.getOpenInventory().getCursor() != null)
			player.getOpenInventory().setCursor(null);

		Location respawnPlace = PvPBox.getInstance().getBoxConfig().getSpawnLocation().getBukkitLocation();

		PlayerAchievementState state = player.getPlayerData().getAchievementState(PvPBoxAchievementList.PVPBOX_KILLER);
		state.setProgress(0);
		
		state = player.getPlayerData().getAchievementState(PvPBoxAchievementList.PVPBOX_UKILLER);
		state.setProgress(0);
		
		state = player.getPlayerData().getAchievementState(PvPBoxAchievementList.PVPBOX_ASSIST);
		state.setProgress(0);

		player.getPlayerData().incrementStatistic("pvpbox", "deaths");
		player.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), BoxScoreboard.DEATHS, 1);
		boxPlayer.setDeaths(boxPlayer.getDeaths() + 1);
		player.getCustomObjective().generate();

		player.setBadblockMode(BadblockMode.SPECTATOR);
		e.setTimeBeforeRespawn(0);

		player.setMaxHealth(20);
		player.setHealth(20);

		player.getCustomObjective().generate();
		e.setRespawnPlace(respawnPlace);
	}

	@EventHandler
	public void onRespawn(PlayerFakeRespawnEvent event)
	{
		BadblockPlayer player = event.getPlayer();

		if (player.getOpenInventory() != null && player.getOpenInventory().getCursor() != null)
		{
			player.getOpenInventory().setCursor(null);
		}

		BoxPlayer boxPlayer = BoxPlayer.get(player);

		if (boxPlayer == null)
		{
			return;
		}

		boxPlayer.reset();
	}

	private void incrementAchievements(BadblockPlayer player, PlayerAchievement... achievements)
	{
		for (PlayerAchievement achievement : achievements)
		{
			PlayerAchievementState state = player.getPlayerData().getAchievementState(achievement);
			state.progress(1.0d);
			state.trySucceed(player, achievement);
		}
		player.saveGameData();
	}

}