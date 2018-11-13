package fr.badblock.bukkit.games.bedwars.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.badblock.bukkit.games.bedwars.BedWarsAchievementList;
import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
import fr.badblock.bukkit.games.bedwars.players.BedWarsData;
import fr.badblock.bukkit.games.bedwars.players.BedWarsScoreboard;
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

	List<Player> shears = new ArrayList<>();
	Map<ItemStack, Player> respawnItems = new HashMap<>();

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
			incrementAchievements(killer, BedWarsAchievementList.BEDWARS_KILL_1,
					BedWarsAchievementList.BEDWARS_KILL_2,
					BedWarsAchievementList.BEDWARS_KILL_3,
					BedWarsAchievementList.BEDWARS_KILL_4,
					BedWarsAchievementList.BEDWARS_KILLER,
					BedWarsAchievementList.BEDWARS_UKILLER);

			if(e.getFightType() == FightingDeaths.BOW){
				incrementAchievements(killer, BedWarsAchievementList.BEDWARS_SHOOTER,
						BedWarsAchievementList.BEDWARS_USHOOTER);
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

		player.getPlayerData().incrementStatistic("bedwars", BedWarsScoreboard.DEATHS);
		player.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), BedWarsScoreboard.DEATHS, 1);
		player.inGameData(BedWarsData.class).deaths++;
		player.getCustomObjective().generate();

		if(player.getTeam().teamData(BedWarsTeamData.class).getFirstBedPart() == null){
			player.getPlayerData().incrementStatistic("bedwars", BedWarsScoreboard.LOOSES);
			player.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), BedWarsScoreboard.LOOSES, 1);
			BadblockTeam team = player.getTeam();

			e.setDeathMessageEnd(new TranslatableString("bedwars.player-loose", player.getName(), team.getChatName()));

			player.sendTranslatedTitle("bedwars.player-loose-title");
			player.sendTimings(20, 80, 20);
			e.setLightning(true);

			team.leaveTeam(player);

			if(team.getOnlinePlayers().size() == 0){
				GameAPI.getAPI().getGameServer().cancelReconnectionInvitations(team);
				GameAPI.getAPI().unregisterTeam(team);

				GameAPI.getAPI().getOnlinePlayers().forEach(p -> {

				});

				new TranslatableString("bedwars.team-loose", team.getChatName()).broadcast();;
			}

			player.setBadblockMode(BadblockMode.SPECTATOR);
			e.setTimeBeforeRespawn(0);
			player.postResult(null);
			if(killer == null){
				respawnPlace = PluginBedWars.getInstance().getMapConfiguration().getSpawnLocation();
			} else {
				respawnPlace = killer.getLocation();
			}
		} else {

			e.setTimeBeforeRespawn(3);

			if (!shears.contains(player) && player.getInventory().contains(Material.SHEARS))
			{
				shears.add(player);
			}

			if (player.getInventory().contains(Material.DIAMOND_PICKAXE))
			{
				respawnItems.put(new ItemStack(Material.IRON_PICKAXE, 1), player);
				e.getDrops().remove(getItemStack(player, Material.DIAMOND_PICKAXE));
			}
			else if (player.getInventory().contains(Material.IRON_PICKAXE))
			{
				respawnItems.put(new ItemStack(Material.STONE_PICKAXE, 1), player);
				e.getDrops().remove(getItemStack(player, Material.IRON_PICKAXE));
			}
			else if (player.getInventory().contains(Material.STONE_PICKAXE))
			{
				respawnItems.put(new ItemStack(Material.STONE_PICKAXE, 1), player);
				e.getDrops().remove(getItemStack(player, Material.STONE_PICKAXE));
			}

			if (player.getInventory().contains(Material.WOOD_SWORD))
			{
				respawnItems.put(new ItemStack(Material.WOOD_SWORD, 1), player);
				e.getDrops().remove(getItemStack(player, Material.WOOD_SWORD));
			}
			
			if (player.getInventory().contains(Material.STONE_SWORD))
			{
				respawnItems.put(getItemStack(player, Material.STONE_SWORD).clone(), player);
				e.getDrops().remove(getItemStack(player, Material.STONE_SWORD));
			}
			
			if (player.getInventory().contains(Material.IRON_SWORD))
			{
				respawnItems.put(getItemStack(player, Material.IRON_SWORD).clone(), player);
				e.getDrops().remove(getItemStack(player, Material.IRON_SWORD));
			}
			
			if (player.getInventory().contains(Material.GOLD_SWORD))
			{
				respawnItems.put(getItemStack(player, Material.GOLD_SWORD).clone(), player);
				e.getDrops().remove(getItemStack(player, Material.GOLD_SWORD));
			}
			
			if (player.getInventory().contains(Material.DIAMOND_SWORD))
			{
				respawnItems.put(getItemStack(player, Material.DIAMOND_SWORD).clone(), player);
				e.getDrops().remove(getItemStack(player, Material.DIAMOND_SWORD));
			}

			if (player.getInventory().contains(Material.DIAMOND_AXE))
			{
				respawnItems.put(new ItemStack(Material.IRON_AXE, 1), player);
				e.getDrops().remove(getItemStack(player, Material.DIAMOND_AXE));
			}
			else if (player.getInventory().contains(Material.IRON_AXE))
			{
				respawnItems.put(new ItemStack(Material.STONE_AXE, 1), player);
				e.getDrops().remove(getItemStack(player, Material.IRON_AXE));
			}
			else if (player.getInventory().contains(Material.STONE_AXE))
			{
				respawnItems.put(new ItemStack(Material.STONE_AXE, 1), player);
				e.getDrops().remove(getItemStack(player, Material.STONE_AXE));
			}

			if (player.getInventory().contains(Material.COMPASS))
			{
				respawnItems.put(getItemStack(player, Material.COMPASS).clone(), player);
				e.getDrops().remove(getItemStack(player, Material.COMPASS));
			}

			if (player.getInventory().getHelmet() != null && !player.getInventory().getHelmet().getType().equals(Material.AIR))
			{
				respawnItems.put(player.getInventory().getHelmet().clone(), player);
				Iterator<ItemStack> iterator = e.getDrops().iterator();
				while (iterator.hasNext())
				{
					ItemStack c = iterator.next();
					if (c == null)
					{
						continue;
					}

					if (!player.getInventory().getHelmet().getType().equals(c.getType()))
					{
						continue;
					}

					iterator.remove();
				}
			}

			if (player.getInventory().getChestplate() != null && !player.getInventory().getChestplate().getType().equals(Material.AIR))
			{
				respawnItems.put(player.getInventory().getChestplate().clone(), player);
				Iterator<ItemStack> iterator = e.getDrops().iterator();
				while (iterator.hasNext())
				{
					ItemStack c = iterator.next();
					if (c == null)
					{
						continue;
					}

					if (!player.getInventory().getChestplate().getType().equals(c.getType()))
					{
						continue;
					}

					iterator.remove();
				}
			}

			if (player.getInventory().getLeggings() != null && !player.getInventory().getLeggings().getType().equals(Material.AIR))
			{
				respawnItems.put(player.getInventory().getLeggings().clone(), player);
				Iterator<ItemStack> iterator = e.getDrops().iterator();
				while (iterator.hasNext())
				{
					ItemStack c = iterator.next();
					if (c == null)
					{
						continue;
					}

					if (!player.getInventory().getLeggings().getType().equals(c.getType()))
					{
						continue;
					}

					iterator.remove();
				}
			}

			if (player.getInventory().getBoots() != null && !player.getInventory().getBoots().getType().equals(Material.AIR))
			{
				respawnItems.put(player.getInventory().getBoots().clone(), player);
				Iterator<ItemStack> iterator = e.getDrops().iterator();
				while (iterator.hasNext())
				{
					ItemStack c = iterator.next();
					if (c == null)
					{
						continue;
					}

					if (!player.getInventory().getBoots().getType().equals(c.getType()))
					{
						continue;
					}

					iterator.remove();
				}
			}

			respawnPlace = player.getTeam().teamData(BedWarsTeamData.class).getRespawnLocation();

			player.setMaxHealth(20 + player.getTeam().teamData(BedWarsTeamData.class).health);
			player.setHealth(20 + player.getTeam().teamData(BedWarsTeamData.class).health);
			if(killer != null){
				e.setWhileRespawnPlace(killer.getLocation());
			} else if(last == DamageCause.VOID){
				e.setWhileRespawnPlace(respawnPlace);
			}
		}

		if(killer != null && killer.getType() == EntityType.PLAYER){
			BadblockPlayer bKiller = (BadblockPlayer) killer;
			if (last.equals(DamageCause.VOID))
			{
				for (ItemStack c : player.getInventory().getContents())
				{
					if (c != null && c.getType() != null && (c.getType().equals(Material.IRON_INGOT) || c.getType().equals(Material.GOLD_INGOT) ||
							c.getType().equals(Material.DIAMOND) || c.getType().equals(Material.EMERALD)))
					{
						bKiller.getInventory().addItem(c);
						e.getDrops().remove(c);
					}
				}
			}
			bKiller.getPlayerData().incrementStatistic("bedwars", BedWarsScoreboard.KILLS);
			bKiller.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), BedWarsScoreboard.KILLS, 1);
			bKiller.inGameData(BedWarsData.class).kills++;
			if (bKiller.getCustomObjective() != null)
				bKiller.getCustomObjective().generate();
		}

		player.getCustomObjective().generate();
		e.setRespawnPlace(respawnPlace);
	}

	public static ItemStack getItemStack(Player player, Material material)
	{
		for (ItemStack content : player.getInventory().getContents())
		{
			if (content != null && content.getType() != null && content.getType().equals(material))
			{
				return content;
			}
		}
		return null;
	}

	@EventHandler
	public void onRespawn(PlayerFakeRespawnEvent e){
		if (e.getPlayer().getOpenInventory() != null && e.getPlayer().getOpenInventory().getCursor() != null)
			e.getPlayer().getOpenInventory().setCursor(null);
		PluginBedWars.getInstance().giveDefaultKit(e.getPlayer());

		if (shears.contains(e.getPlayer()))
		{
			e.getPlayer().getInventory().addItem(new ItemStack(Material.SHEARS, 1));
		}

		BadblockTeam team = e.getPlayer().getTeam();
		if (team == null)
		{
			return;
		}

		BedWarsTeamData teamData = team.teamData(BedWarsTeamData.class);

		if (teamData.speed > 0)
		{
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, teamData.speed - 1));
		}

		if (teamData.speedMining > 0)
		{
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, teamData.speedMining - 1));
		}

		if (teamData.strength > 0)
		{
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, teamData.strength - 1));
		}

		if (teamData.protection > 0)
		{
			e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, teamData.protection - 1));
		}

		Iterator<Entry<ItemStack, Player>> iterator = respawnItems.entrySet().iterator();

		while (iterator.hasNext())
		{
			Entry<ItemStack, Player> entry = iterator.next();

			if (!entry.getValue().equals(e.getPlayer()))
			{
				continue;
			}

			iterator.remove();

			boolean a = false;

			if (entry.getKey().getType().name().toLowerCase().contains("helmet"))
			{
				a = true;
				e.getPlayer().getInventory().setHelmet(entry.getKey());
			}

			if (entry.getKey().getType().name().toLowerCase().contains("chestplate"))
			{
				a = true;
				e.getPlayer().getInventory().setChestplate(entry.getKey());
			}

			if (entry.getKey().getType().name().toLowerCase().contains("leggings"))
			{
				a = true;
				e.getPlayer().getInventory().setLeggings(entry.getKey());
			}

			if (entry.getKey().getType().name().toLowerCase().contains("boots"))
			{
				a = true;
				e.getPlayer().getInventory().setBoots(entry.getKey());
			}

			if (!a)
			{
				e.getPlayer().getInventory().addItem(entry.getKey());
			}
		}
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
