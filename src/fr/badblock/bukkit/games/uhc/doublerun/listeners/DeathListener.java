package fr.badblock.bukkit.games.uhc.doublerun.listeners;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.games.uhc.doublerun.PluginUHC;
import fr.badblock.bukkit.games.uhc.doublerun.UHCAchievementList;
import fr.badblock.bukkit.games.uhc.doublerun.configuration.UHCConfiguration.MapCustomMaterial;
import fr.badblock.bukkit.games.uhc.doublerun.players.UHCData;
import fr.badblock.bukkit.games.uhc.doublerun.players.UHCScoreboard;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.events.fakedeaths.FakeDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.FightingDeathEvent;
import fr.badblock.gameapi.events.fakedeaths.FightingDeathEvent.FightingDeaths;
import fr.badblock.gameapi.events.fakedeaths.NormalDeathEvent;
import fr.badblock.gameapi.game.rankeds.RankedManager;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;

public class DeathListener extends BadListener {

	@EventHandler
	public void onDeath(NormalDeathEvent e){
		death(e, e.getPlayer(), null, e.getLastDamageCause());
		e.setDeathMessage(GameMessages.deathEventMessage(e));
	}

	@EventHandler
	public void onDeath(EntityDeathEvent ev) {
		if (PluginUHC.getInstance().getConfiguration().randomMobDrops.containsKey(ev.getEntityType().name())) {
			List<MapCustomMaterial> materials = PluginUHC.getInstance().getConfiguration().randomMobDrops.get(ev.getEntityType().name());
			int randomTotal = materials.stream().mapToInt(i -> i.getProbability()).sum();
			int random = new Random().nextInt(randomTotal);

			int t = 0;
			for (MapCustomMaterial entry : materials)
			{
				t += entry.getProbability();

				if (random < t)
				{
					Material material = UHCMapProtector.getFrom(entry.getName());
					if (material.equals(Material.AIR))
					{
						break;
					}
					ev.getEntity().getWorld().dropItemNaturally(ev.getEntity().getLocation(), new ItemStack(material, entry.getAmount(), (byte) entry.getData()));
					break;
				}
			}
		}
	}

	@EventHandler
	public void onDeath(FightingDeathEvent e){
		death(e, e.getPlayer(), e.getKiller(), e.getLastDamageCause());
		e.setDeathMessage(GameMessages.deathEventMessage(e));

		if(e.getKiller().getType() == EntityType.PLAYER){
			BadblockPlayer killer = (BadblockPlayer) e.getKiller();
			killer.getPlayerData().incrementAchievements(killer, UHCAchievementList.UHC_KILL_1, UHCAchievementList.UHC_KILL_2, UHCAchievementList.UHC_KILL_3, UHCAchievementList.UHC_KILL_4, UHCAchievementList.UHC_KILLER);

			if(e.getFightType() == FightingDeaths.BOW){
				killer.getPlayerData().incrementAchievements(killer, UHCAchievementList.UHC_SHOOTER, UHCAchievementList.UHC_USHOOTER);
			}
		}
	}

	private Map<String, Long> lastDeath = new HashMap<>();

	private void death(FakeDeathEvent e, BadblockPlayer player, Entity killer, DamageCause last){
		if (lastDeath.containsKey(player.getName())) {
			if (lastDeath.get(player.getName()) > System.currentTimeMillis()) {
				e.setCancelled(true);
				return;
			}
		}
		lastDeath.put(player.getName(), System.currentTimeMillis() + 1000L);
		Location respawnPlace = null;

		if (player.getOpenInventory() != null && player.getOpenInventory().getCursor() != null)
			player.getOpenInventory().setCursor(null);
		player.getPlayerData().addRankedPoints(-2);
		player.getPlayerData().incrementStatistic("uhcspeed", UHCScoreboard.DEATHS);
		player.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), UHCScoreboard.DEATHS, 1);
		player.inGameData(UHCData.class).killed(player, killer, last);
		player.getCustomObjective().generate();

		player.getPlayerData().incrementStatistic("uhcspeed", UHCScoreboard.LOOSES);
		player.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), UHCScoreboard.LOOSES, 1);
		BadblockTeam team = player.getTeam();

		player.sendTranslatedTitle("uhcspeed.player-loose-title");
		player.sendTimings(20, 80, 20);
		e.setLightning(true);

		if(team != null){

			e.setDeathMessageEnd(new TranslatableString("uhcspeed.player-loose-team", player.getName(), team.getChatName()));

			team.leaveTeam(player);

			if(team.getOnlinePlayers().size() == 0){
				GameAPI.getAPI().getGameServer().cancelReconnectionInvitations(team);
				GameAPI.getAPI().unregisterTeam(team);

				new TranslatableString("uhcspeed.team-loose", team.getChatName()).broadcast();;
			}

		} else {
			e.setDeathMessageEnd(new TranslatableString("uhcspeed.player-loose", player.getName()));
		}

		GameAPI.getAPI().getOnlinePlayers().forEach(p -> p.playSound(Sound.WITHER_SPAWN));

		player.setBadblockMode(BadblockMode.SPECTATOR);
		e.setTimeBeforeRespawn(0);

		if(killer == null){
			respawnPlace = PluginUHC.getInstance().getDefaultLoc();
		} else {
			respawnPlace = killer.getLocation();
		}

		if(killer != null && killer.getType() == EntityType.PLAYER){
			BadblockPlayer bKiller = (BadblockPlayer) killer;
			bKiller.getPlayerData().incrementStatistic("uhcspeed", UHCScoreboard.KILLS);
			bKiller.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), UHCScoreboard.KILLS, 1);
			bKiller.inGameData(UHCData.class).kills++;

			bKiller.getCustomObjective().generate();
		}

		player.getCustomObjective().generate();
		e.setRespawnPlace(respawnPlace);
		player.postResult(null);
	}
}
