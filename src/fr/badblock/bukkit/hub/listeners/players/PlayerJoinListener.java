package fr.badblock.bukkit.hub.listeners.players;

import java.security.SecureRandom;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.badblock.bukkit.hub.BadBlockHub;
import fr.badblock.bukkit.hub.inventories.join.PlayerCustomInventory;
import fr.badblock.bukkit.hub.listeners._HubListener;
import fr.badblock.bukkit.hub.objects.HubPlayer;
import fr.badblock.bukkit.hub.objects.HubScoreboard;
import fr.badblock.bukkit.hub.objects.HubStoredPlayer;
import fr.badblock.game.core18R3.players.GameBadblockPlayer;
import fr.badblock.game.core18R3.players.ingamedata.CommandInGameData;
import fr.badblock.gameapi.events.api.PlayerLoadedEvent;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.ConfigUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.threading.TaskManager;

public class PlayerJoinListener extends _HubListener {

	public static void load(BadblockPlayer player) {
		Bukkit.getScheduler().runTask(BadBlockHub.getInstance(), new Runnable() {
			@Override
			public void run() {
				player.inGameData(HubPlayer.class);
				reload(player);
			}
		});
	}

	public static void reload(BadblockPlayer player) {
		player.changePlayerDimension(Environment.NETHER);
		player.clearInventory();
		player.setMaxHealth(20D);
		player.setHealth(20D);
		player.setWalkSpeed(0.4F);
		player.setGameMode(GameMode.SURVIVAL);
		player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, Integer.MAX_VALUE, 0));
		PlayerCustomInventory.give(player);
		System.out.println("[HUB] Loaded " + player.getName() + ".");
	}


	@EventHandler
	public void onDataLoad(PlayerLoadedEvent event) {
		BadblockPlayer player = event.getPlayer();
		load(player);
		TaskManager.runTaskLater(new Runnable() {
			@Override
			public void run() {
				if (player == null || !player.isOnline()) return;
				HubPlayer hubPlayer = HubPlayer.get(player);
				hubPlayer.lodad(player);
				HubStoredPlayer hubStoredPlayer = HubStoredPlayer.get(player);
				hubStoredPlayer.getMountConfigs().values().stream().forEach(mount -> mount.setBaby(false));
				Location location = ConfigUtils.getLocation(BadBlockHub.getInstance(), "worldspawn");
				location = location.clone();
				location.setX((-2)+new SecureRandom().nextInt(4));
				location.setZ((-2)+new SecureRandom().nextInt(4));
				Block block = location.getBlock();
				location = block.getWorld().getHighestBlockAt(location).getLocation();
				while (!location.getBlock().getType().equals(Material.AIR))
					location.setY(location.getY() + 1);
				player.teleport(location);
				hubPlayer.setScoreboard(new HubScoreboard(player));
				if (hubPlayer.getScoreboard() != null)
					hubPlayer.getScoreboard().generate();
				// Broadcast a join message
				if (player.hasPermission("hub.broadcastjoin"))
				{
					GameBadblockPlayer gbp = (GameBadblockPlayer) player;
					if (!gbp.getFakeMainGroup().equalsIgnoreCase("default"))
					{
						new TranslatableString("hub.joined", player.getGroupPrefix(), player.getName()).broadcast();
					}
				}
				// For sur tous les joueurs pour voir s'ils peuvent voir celui qui vient de se co
				for (Player pl : Bukkit.getOnlinePlayers()) {
					BadblockPlayer plo = (BadblockPlayer) player;
					HubStoredPlayer pls = HubStoredPlayer.get(plo);
					if (player.hasPermission("hub.bypasshide")) pl.showPlayer(player);
					else if (player.inGameData(CommandInGameData.class).vanish) pl.hidePlayer(player);
					else if (plo.inGameData(HubPlayer.class).getFriends().contains(player.getName())) pl.showPlayer(player);
					else if (pls.isHidePlayers()) pl.hidePlayer(player);
					else pl.showPlayer(player);
				}
				// For sur tous les joueurs pour voir si celui qui vient de se co peut les voir
				for (Player pl : Bukkit.getOnlinePlayers()) {
					BadblockPlayer plo = (BadblockPlayer) pl;
					if (plo.hasPermission("hub.bypasshide")) player.showPlayer(plo);
					else if (plo.inGameData(CommandInGameData.class).vanish) player.hidePlayer(plo); 
					else if (player.inGameData(HubPlayer.class).getFriends().contains(plo.getName())) player.showPlayer(plo);
					else if (hubStoredPlayer.isHidePlayers()) player.hidePlayer(plo);
					else player.showPlayer(pl);
				}

				//for (BadblockPlayer po : BukkitUtils.getPlayers())
				//	if (HubStoredPlayer.get(po).hidePlayers) po.hidePlayer(player);
				/*if (hubStoredPlayer.isHidePlayers())
					for (BadblockPlayer po : BukkitUtils.getPlayers()) {
						player.hidePlayer(po);
					}*/
				/*TaskManager.runTaskLater(new Runnable() {
					@Override
					public void run() {
						if (player == null || !player.isOnline()) return;
						if (hubStoredPlayer.isHidePlayers())
							for (BadblockPlayer po : BukkitUtils.getPlayers()) {
								player.hidePlayer(po);
							}
					}
				}, 5);*/
			}
		}, 1);
	}

	/*@EventHandler
	public void onPlayerPermissionLoaded(PlayerPermissionLoadedEvent event) {
		BadblockPlayer player = event.getPlayer();
		HubPlayer hubPlayer = HubPlayer.get(player);
		if (hubPlayer.getScoreboard() != null)
			hubPlayer.getScoreboard().generate();
	}*/

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		BadBlockHub.getInstance().hubPacketThread.sendPacket();
		event.setJoinMessage(null);
	}

}
