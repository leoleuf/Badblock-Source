package fr.badblock.bukkit.hub.v1.listeners.vipzone;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Effect;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.meta.FireworkMeta;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.listeners._HubListener;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.ConfigUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.selections.CuboidSelection;
import fr.badblock.gameapi.utils.threading.TaskManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class RaceListener extends _HubListener {

	public static List<RaceCell> raceEnterFences = new ArrayList<>();
	public static Map<BadblockPlayer, RaceCell> racePlayers = new HashMap<>();
	public static Map<BadblockPlayer, Double> winnerOrder = new HashMap<>();
	public static List<CuboidSelection> voidCuboids = new ArrayList<>();
	public static Map<BadblockPlayer, Location> lastWinLoc = new HashMap<>();
	public static Map<BadblockPlayer, Location> noCuboidLoc = new HashMap<>();
	public static RaceState raceState = RaceState.WAITING;
	private int seconds = -1;
	private int minPlayers = 2;
	private int waitingCountdownConfig = 30;
	private int launchingCountdownConfig = 10;
	private int runningCountdownConfig = 10;
	private int finishedCountdownConfig = 10;
	private long start;
	private List<Integer> notifySeconds = new ArrayList<>();
	private Location frontLocation;
	private Location lineLocation1;
	private Location lineLocation2;
	private CuboidSelection lineCuboid;
	private float b;

	public RaceListener(BadBlockHub hub) {
		FileConfiguration configuration = hub.getConfig();
		ConfigurationSection configurationSection = configuration.getConfigurationSection("race.cells");
		raceEnterFences.clear();
		winnerOrder.clear();
		raceState = RaceState.WAITING;
		racePlayers.clear();
		lastWinLoc.clear();
		minPlayers = ConfigUtils.get(hub, "race.minPlayers", 2);
		notifySeconds = ConfigUtils.getIntList(hub, "race.notifySeconds");
		frontLocation = ConfigUtils.getLocation(hub, "race.front");
		lineLocation1 = ConfigUtils.getLocation(hub, "race.line.loc1");
		lineLocation2 = ConfigUtils.getLocation(hub, "race.line.loc2");
		lineCuboid = new CuboidSelection(lineLocation1, lineLocation2);
		waitingCountdownConfig = ConfigUtils.get(hub, "race.waitingCountdownConfig", 30);
		launchingCountdownConfig = ConfigUtils.get(hub, "race.launchCountdownConfig", 10);
		finishedCountdownConfig = ConfigUtils.get(hub, "race.finishedCountdownConfig", 10);
		runningCountdownConfig = ConfigUtils.get(hub, "race.runningCountdownConfig", 90);
		configurationSection.getKeys(false).forEach(key -> {
			Location blockLocation = ConfigUtils.getLocation(hub, "race.cells." + key + ".blockLocation");
			Location cellLocation = ConfigUtils.getLocation(hub, "race.cells." + key + ".cellLocation");
			Location aheadLocation = ConfigUtils.getLocation(hub, "race.cells." + key + ".aheadLocation");
			Location breakableBlockLocation = ConfigUtils.getLocation(hub,
					"race.cells." + key + ".breakableBlockLocation");
			RaceCell raceCell = new RaceCell(blockLocation, cellLocation, aheadLocation, breakableBlockLocation);
			raceEnterFences.add(raceCell);
		});
		configurationSection = configuration.getConfigurationSection("race.voids");
		configurationSection.getKeys(false).forEach(key -> {
			Location location1 = ConfigUtils.getLocation(hub, "race.voids." + key + ".location1");
			Location location2 = ConfigUtils.getLocation(hub, "race.voids." + key + ".location2");
			voidCuboids.add(new CuboidSelection(location1, location2));
		});
		TaskManager.scheduleSyncRepeatingTask("raceTask", new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				Iterator<Entry<BadblockPlayer, RaceCell>> iterator = racePlayers.entrySet().iterator();
				while (iterator.hasNext()) {
					Entry<BadblockPlayer, RaceCell> entry = iterator.next();
					if (entry.getKey() == null || !entry.getKey().isOnline() || !entry.getKey().isValid())
						iterator.remove();
				}
				if (raceState.equals(RaceState.WAITING)) {
					raceEnterFences.forEach(raceCell -> {
						Location breakableBlockLocation = raceCell.breakableBlockLocation;
						breakableBlockLocation.getBlock().setType(Material.getMaterial(107));
					});
					if (racePlayers.size() >= minPlayers) {
						if (seconds == -1) {
							seconds = waitingCountdownConfig;
							racePlayers.keySet()
									.forEach(player -> player.sendTranslatedMessage(
											"hub.race.launched_countdown_there_is_enough_players", racePlayers.size(),
											raceEnterFences.size()));
						} else if (seconds <= 0) {
							seconds = launchingCountdownConfig;
							raceState = RaceState.LAUNCHING;
							racePlayers.keySet().forEach(player -> player.sendTranslatedMessage("hub.race.preparation",
									racePlayers.size(), raceEnterFences.size()));
						} else {
							if (notifySeconds.contains(seconds)) {
								racePlayers.keySet().forEach(player -> player.sendTranslatedMessage(
										"hub.race.waiting_time_to_be_launched_" + (seconds >= 86400 ? "days"
												: seconds >= 3600 ? "hours" : seconds >= 60 ? "minutes" : "seconds"),
										seconds / (seconds >= 86400 ? 86400
												: seconds >= 3600 ? 3600 : seconds >= 60 ? 60 : 1)));
							}
							seconds--;
						}
					} else if (racePlayers.size() < minPlayers && seconds != -1) {
						racePlayers.keySet().forEach(player -> {
							player.sendTranslatedMessage("hub.race.stopped_countdown_there_isnt_enough_players",
									racePlayers.size(), raceEnterFences.size());
						});
						seconds = -1;
					}
				} else if (raceState.equals(RaceState.LAUNCHING)) {
					if (racePlayers.size() < minPlayers) {
						seconds = waitingCountdownConfig;
						raceState = RaceState.WAITING;
						raceEnterFences.forEach(raceCell -> {
							Location breakableBlockLocation = raceCell.breakableBlockLocation;
							breakableBlockLocation.getBlock().setType(Material.getMaterial(107));
						});
						racePlayers.keySet().forEach(player -> {
							player.sendTranslatedMessage("hub.race.stopped_countdown_there_isnt_enough_players",
									racePlayers.size(), raceEnterFences.size());
						});
					} else {
						if (seconds <= 0) {
							raceState = RaceState.RUNNING;
							seconds = runningCountdownConfig;
							racePlayers.keySet().forEach(player -> player.sendTranslatedMessage("hub.race.go"));
							raceEnterFences.forEach(raceCell -> {
								Location breakableBlockLocation = raceCell.breakableBlockLocation;
								racePlayers.keySet().forEach(racePlayer -> racePlayer.playEffect(breakableBlockLocation,
										Effect.EXPLOSION_HUGE, 1));
								racePlayers.keySet().forEach(racePlayer -> racePlayer.playSound(Sound.EXPLODE));
								breakableBlockLocation.getBlock().setType(Material.AIR);
							});
							start = System.currentTimeMillis();
						} else {
							if (seconds == 1) {
								racePlayers.entrySet().forEach(entry -> entry.getKey()
										.teleport(entry.getValue().getCellLocation(), TeleportCause.NETHER_PORTAL));
							}
							if (notifySeconds.contains(seconds)) {
								racePlayers.keySet().forEach(player -> player.sendTranslatedMessage(
										"hub.race.launching_time_to_be_able_to_run_" + (seconds >= 86400 ? "days"
												: seconds >= 3600 ? "hours" : seconds >= 60 ? "minutes" : "seconds"),
										seconds / (seconds >= 86400 ? 86400
												: seconds >= 3600 ? 3600 : seconds >= 60 ? 60 : 1)));
							}
							seconds--;
						}
					}
				} else if (raceState.equals(RaceState.RUNNING)) {
					if (racePlayers.size() < minPlayers) {
						win(RaceFinishType.NO_ENOUGH_PLAYERS);
					} else {
						boolean finished = true;
						for (BadblockPlayer plo : racePlayers.keySet())
							if (!winnerOrder.containsKey(plo)) {
								finished = false;
								break;
							}
						if (finished || seconds <= 0) {
							seconds = -1;
							win(RaceFinishType.ALL_PLAYERS_FINISHED);
						}else seconds--;
					}
				} else if (raceState.equals(RaceState.FINISHED)) {
					if (seconds <= 0) {
						seconds = -1;
						racePlayers.keySet()
								.forEach(player -> player.teleport(frontLocation, TeleportCause.NETHER_PORTAL));
						racePlayers.keySet().forEach(player -> {
							player.setLevel(0);
							player.setExp(0);
						});
						racePlayers.clear();
						winnerOrder.clear();
						raceState = RaceState.WAITING;
					} else
						seconds--;
				}
				if (seconds <= -1) {
					racePlayers.keySet().forEach(player -> {
						player.setLevel(0);
						player.setExp(0);
					});
				} else {
					b = 1;
					racePlayers.keySet().forEach(player -> {
						player.setLevel(seconds);
						player.setExp(b);
						player.playSound(Sound.NOTE_STICKS);
					});
				}
			}
		}, 20, 20);
		TaskManager.scheduleSyncRepeatingTask("raceTask_xp", new Runnable() {
			@Override
			public void run() {
				if (b < 0)
					b = 0;
				else
					b -= 0.05;
				if (seconds <= 0)
					racePlayers.keySet().forEach(player -> {
						player.setLevel(0);
						player.setExp(0);
					});
				else
					racePlayers.keySet().forEach(player -> {
						player.setLevel(seconds);
						player.setExp(b);
					});
			}
		}, 1, 1);
	}

	@SuppressWarnings("deprecation")
	private void win(Map<BadblockPlayer, Double> winnerOrder, RaceFinishType finishType) {
		seconds = finishedCountdownConfig;
		raceState = RaceState.FINISHED;
		raceEnterFences.forEach(raceCell -> {
			Location breakableBlockLocation = raceCell.breakableBlockLocation;
			breakableBlockLocation.getBlock().setType(Material.getMaterial(107));
		});
		if (winnerOrder.isEmpty()) {
			racePlayers.keySet().forEach(player -> player.sendTranslatedMessage("hub.race.win_nowinner"));
			return;
		}
		String winner0Name = null, winner1Name = null, winner2Name = null, winner3Name = null, winner4Name = null;
		TranslatableString winner0Prefix = null, winner1Prefix = null, winner2Prefix = null, winner3Prefix = null,
				winner4Prefix = null;
		double winner0Time = 0, winner1Time = 0, winner2Time = 0, winner3Time = 0, winner4Time = 0;
		Iterator<Entry<BadblockPlayer, Double>> iterator = winnerOrder.entrySet().stream().sorted((e1, e2) -> { return Double.compare(e1.getValue(), e2.getValue()); }).collect(Collectors.toList()).iterator();
		int i = 0;
		while (iterator.hasNext()) {
			Entry<BadblockPlayer, Double> entry = iterator.next();
			if (i == 0) {
				winner0Prefix = entry.getKey().getGroupPrefix();
				winner0Name = entry.getKey().getName();
				winner0Time = entry.getValue();
				if (finishType.equals(RaceFinishType.ALL_PLAYERS_FINISHED))
					Bukkit.getOnlinePlayers()
							.forEach(racePlayer -> ((BadblockPlayer) racePlayer).sendTranslatedMessage(
									"hub.race.broadcast_won", entry.getKey().getGroupPrefix().getAsLine(racePlayer)
											+ entry.getKey().getName(),
									entry.getValue()));
			} else if (i == 1) {
				winner1Prefix = entry.getKey().getGroupPrefix();
				winner1Name = entry.getKey().getName();
				winner1Time = entry.getValue();
			} else if (i == 2) {
				winner2Prefix = entry.getKey().getGroupPrefix();
				winner2Name = entry.getKey().getName();
				winner2Time = entry.getValue();
			} else if (i == 3) {
				winner3Prefix = entry.getKey().getGroupPrefix();
				winner3Name = entry.getKey().getName();
				winner3Time = entry.getValue();
			} else if (i == 4) {
				winner4Prefix = entry.getKey().getGroupPrefix();
				winner4Name = entry.getKey().getName();
				winner4Time = entry.getValue();
			}
			i++;
		}
		String fWinner0Name = winner0Name, fWinner1Name = winner1Name, fWinner2Name = winner2Name,
				fWinner3Name = winner3Name, fWinner4Name = winner4Name;
		double fWinner0Time = winner0Time, fWinner1Time = winner1Time, fWinner2Time = winner2Time,
				fWinner3Time = winner3Time, fWinner4Time = winner4Time;
		TranslatableString fWinner0Prefix = winner0Prefix, fWinner1Prefix = winner1Prefix,
				fWinner2Prefix = winner2Prefix, fWinner3Prefix = winner3Prefix, fWinner4Prefix = winner4Prefix;
		racePlayers.keySet().forEach(player -> {
			player.sendTranslatedMessage("hub.race.win",
					(finishType.equals(RaceFinishType.ALL_PLAYERS_FINISHED)
							? player.getTranslatedMessage("hub.race.wintype_allplayersfinished")[0]
							: finishType.equals(RaceFinishType.NO_ENOUGH_PLAYERS)
									? player.getTranslatedMessage("hub.race.wintype_noenoughplayers")[0]
									: player.getTranslatedMessage("hub.race.wintype_unknown")[0]),
					(fWinner0Name != null ? fWinner0Prefix.getAsLine(player) + fWinner0Name
							: player.getTranslatedMessage("hub.race.nullwinner")[0]),
					(fWinner0Name != null ? fWinner0Time : player.getTranslatedMessage("hub.race.nullwinnertime")[0]),
					(fWinner1Name != null ? fWinner1Prefix.getAsLine(player) + fWinner1Name
							: player.getTranslatedMessage("hub.race.nullwinner")[0]),
					(fWinner1Name != null ? fWinner1Time : player.getTranslatedMessage("hub.race.nullwinnertime")[0]),
					(fWinner2Name != null ? fWinner2Prefix.getAsLine(player) + fWinner2Name
							: player.getTranslatedMessage("hub.race.nullwinner")[0]),
					(fWinner2Name != null ? fWinner2Time : player.getTranslatedMessage("hub.race.nullwinnertime")[0]),
					(fWinner3Name != null ? fWinner3Prefix.getAsLine(player) + fWinner3Name
							: player.getTranslatedMessage("hub.race.nullwinner")[0]),
					(fWinner3Name != null ? fWinner3Time : player.getTranslatedMessage("hub.race.nullwinnertime")[0]),
					(fWinner4Name != null ? fWinner4Prefix.getAsLine(player) + fWinner4Name
							: player.getTranslatedMessage("hub.race.nullwinner")[0]),
					(fWinner4Name != null ? fWinner4Time : player.getTranslatedMessage("hub.race.nullwinnertime")[0]));
			player.setWalkSpeed(0.4F);
		});
		TaskManager.scheduleSyncRepeatingTask("race_finish", new Runnable() {

			@Override
			public void run() {
				if (!raceState.equals(RaceState.FINISHED)) {
					TaskManager.cancelTaskByName("race_finish");
					return;
				}
				Location location = lineCuboid.getRandomLocation();
				FireworkEffect.Builder builder = FireworkEffect.builder();
				FireworkEffect effect = builder.flicker(false).trail(false).with(getFireWorkType())
						.withColor(getColor()).withColor(getColor()).withColor(getColor()).withFade(getColor()).build();
				Firework fw = location.getWorld().spawn(location, Firework.class);
				FireworkMeta meta = fw.getFireworkMeta();
				meta.addEffect(effect);
				fw.setFireworkMeta(meta);
			}
		}, 8, 8);
	}

	private static Type getFireWorkType() {
		int r = new SecureRandom().nextInt(5) + 1;
		if (r == 1)
			return FireworkEffect.Type.BALL;
		else if (r == 2)
			return FireworkEffect.Type.BALL_LARGE;
		else if (r == 3)
			return FireworkEffect.Type.BURST;
		else if (r == 4)
			return FireworkEffect.Type.CREEPER;
		else if (r == 5)
			return FireworkEffect.Type.STAR;
		else
			return FireworkEffect.Type.BALL;
	}

	private static Color getColor() {
		int r = new SecureRandom().nextInt(15) + 1;
		if (r == 1)
			return Color.AQUA;
		else if (r == 2)
			return Color.BLUE;
		else if (r == 3)
			return Color.FUCHSIA;
		else if (r == 4)
			return Color.GRAY;
		else if (r == 5)
			return Color.GREEN;
		else if (r == 6)
			return Color.LIME;
		else if (r == 7)
			return Color.MAROON;
		else if (r == 8)
			return Color.NAVY;
		else if (r == 9)
			return Color.OLIVE;
		else if (r == 10)
			return Color.ORANGE;
		else if (r == 11)
			return Color.PURPLE;
		else if (r == 12)
			return Color.RED;
		else if (r == 13)
			return Color.SILVER;
		else if (r == 14)
			return Color.TEAL;
		else if (r == 15)
			return Color.YELLOW;
		else
			return Color.BLUE;
	}

	private void win(RaceFinishType finishType) {
		win(winnerOrder, finishType);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		BadblockPlayer player = (BadblockPlayer) event.getPlayer();
		Action action = event.getAction();
		if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
			Block block = event.getClickedBlock();
			Location location = block.getLocation();
			for (RaceCell raceCell : raceEnterFences)
				if (equals(location, raceCell.getBlockLocation())) {
					event.setCancelled(true);
					join(player, raceCell);
					break;
				}
			return;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandpreprocess(PlayerCommandPreprocessEvent event) {
		BadblockPlayer player = (BadblockPlayer) event.getPlayer();
		if (racePlayers.containsKey(player) && event.getMessage().startsWith("/")) {
			player.sendTranslatedMessage("hub.race.youcannotdothatinrace");
			event.setCancelled(true);
			return;
		}
	}

	public static void join(BadblockPlayer player, RaceCell raceCell) {
		if (racePlayers.containsKey(player)) {
			// sorti
			player.sendTranslatedMessage("hub.race.you_left", racePlayers.size() - 1, raceEnterFences.size());
			if (raceState.equals(RaceState.WAITING))
				racePlayers.keySet()
						.forEach(racePlayer -> racePlayer.sendTranslatedMessage("hub.race.has_left",
								player.getGroupPrefix().getAsLine(racePlayer) + player.getName(),
								racePlayers.size() - 1, raceEnterFences.size()));
			else if (raceState.equals(RaceState.RUNNING) || raceState.equals(RaceState.LAUNCHING))
				racePlayers.keySet()
						.forEach(racePlayer -> racePlayer.sendTranslatedMessage("hub.race.has_abandoned",
								player.getGroupPrefix().getAsLine(racePlayer) + player.getName(),
								racePlayers.size() - 1, raceEnterFences.size()));
			racePlayers.remove(player);
			player.setWalkSpeed(0.4F);
			player.teleport(raceCell.getAheadCellLocaton(), TeleportCause.NETHER_PORTAL);
		} else {
			if (racePlayers.containsValue(raceCell)) {
				player.sendTranslatedMessage("hub.race.already_used");
				return;
			}
			if (!raceState.equals(RaceState.WAITING)) {
				if (raceState.equals(RaceState.LAUNCHING))
					player.sendTranslatedMessage("hub.race.cannot_join_just_launch");
				else if (raceState.equals(RaceState.RUNNING))
					player.sendTranslatedMessage("hub.race.cannot_join_running");
				else
					player.sendTranslatedMessage("hub.race.cannot_join_finished");
				return;
			}
			if (player.isDisguised())
				player.undisguise();
			if (player.isInsideVehicle())
				player.getVehicle().remove();
			if (racePlayers.size() == 0) {
				Bukkit.getOnlinePlayers().parallelStream()
						.filter(plo -> !plo.getUniqueId().equals(player.getUniqueId()))
						.filter(plo -> plo.hasPermission("hub.vipzone")).forEach(plo -> {
							BadblockPlayer ploz = (BadblockPlayer) plo;
							TextComponent message = new TextComponent("");
							StringBuilder stringBuilder = new StringBuilder();
							Iterator<String> iterator = Arrays
									.asList(new TranslatableString("hub.race.invite",
											player.getGroupPrefix().getAsLine(plo) + player.getName()).get(ploz))
									.iterator();
							while (iterator.hasNext()) {
								String msg = iterator.next();
								stringBuilder.append(msg + (iterator.hasNext() ? System.lineSeparator() : ""));
							}
							message.setText(stringBuilder.toString());
							message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/race"));
							message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new ComponentBuilder(new TranslatableString("hub.race.invite.hover",
											player.getGroupPrefix().getAsLine(plo) + player.getName()).getAsLine(plo))
													.create()));
							plo.spigot().sendMessage(message);
						});
			}
			HubPlayer hubPlayer = HubPlayer.get(player);
			hubPlayer.getParticles().forEach(particle -> particle.cancel());
			hubPlayer.getParticles().clear();
			player.teleport(raceCell.getCellLocation(), TeleportCause.NETHER_PORTAL);
			player.setWalkSpeed(0.2F);
			racePlayers.put(player, raceCell);
			player.sendTranslatedMessage("hub.race.you_join", racePlayers.size(), raceEnterFences.size());
			if (raceState.equals(RaceState.WAITING))
				racePlayers.keySet()
						.forEach(racePlayer -> racePlayer.sendTranslatedMessage("hub.race.has_join",
								player.getGroupPrefix().getAsLine(racePlayer) + player.getName(), racePlayers.size(),
								raceEnterFences.size()));
		}
	}

	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		BadblockPlayer player = (BadblockPlayer) event.getPlayer();
		if (racePlayers.containsKey(player)) {
			if (event.getCause().equals(TeleportCause.NETHER_PORTAL))
				return;
			if (!raceState.equals(RaceState.FINISHED) && seconds != -1)
				event.setCancelled(true);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event) {
		BadblockPlayer player = (BadblockPlayer) event.getPlayer();
		if (racePlayers.containsKey(player)) {
			if (raceState.equals(RaceState.WAITING) || raceState.equals(RaceState.LAUNCHING)) {
				if (!equals(event.getFrom(), event.getTo())) {
					Location loc = racePlayers.get(player).getCellLocation();
					loc.setPitch(event.getTo().getPitch());
					loc.setYaw(event.getTo().getPitch());
					player.teleport(loc, TeleportCause.NETHER_PORTAL);
				}
			} else if (raceState.equals(RaceState.RUNNING)) {
				if (lineCuboid.isInSelection(event.getTo())) {
					lastWinLoc.put(player, event.getTo());
					if (!winnerOrder.containsKey(player)) {
						double time = ((System.currentTimeMillis() - start) * 1.0D) / 1000.0D;
						double formatTime = Double.parseDouble(String.format("%.2f", time));
						winnerOrder.put(player, formatTime);
						player.sendTranslatedMessage("hub.race.you_finish", formatTime);
						racePlayers.keySet()
								.forEach(racePlayer -> racePlayer.sendTranslatedMessage("hub.race.has_finished",
										player.getGroupPrefix().getAsLine(racePlayer) + player.getName(), formatTime));
						boolean finished = true;
						for (BadblockPlayer plo : racePlayers.keySet())
							if (!winnerOrder.containsKey(plo)) {
								finished = false;
								break;
							}
						player.playSound(Sound.LEVEL_UP);
						if (finished)
							win(RaceFinishType.ALL_PLAYERS_FINISHED);
					}

				}
				if (winnerOrder.containsKey(player) && !lineCuboid.isInSelection(event.getTo()))
					player.teleport(lastWinLoc.get(player), TeleportCause.NETHER_PORTAL);
				boolean oopsy = false;
				for (CuboidSelection cuboidSelection : voidCuboids)
					if (cuboidSelection.isInSelection(event.getTo()))
						oopsy = true;
				boolean oops = false;
				for (CuboidSelection cuboidSelection : voidCuboids)
					if (cuboidSelection.isInSelection(event.getFrom()) && cuboidSelection.isInSelection(event.getTo()))
						oops = true;
				if (oopsy) {
					if (noCuboidLoc.containsKey(player))
						player.teleport(noCuboidLoc.get(player).clone().add(0, 1, 0), TeleportCause.NETHER_PORTAL);
					else
						player.teleport(event.getFrom().clone().add(0, 1, 0), TeleportCause.NETHER_PORTAL);
					player.sendTranslatedMessage("hub.race.failure");
				} else if (!oops) {
					Block block = event.getFrom().clone().add(0, -1, 0).getBlock();
					if (block != null && !block.getType().equals(Material.AIR) && player.isOnGround())
						noCuboidLoc.put(player, event.getFrom());
				}
			}
		}
	}

	public boolean equals(Location location, Location location2) {
		return location.getBlockX() == location2.getBlockX() && location.getBlockY() == location2.getBlockY()
				&& location.getBlockZ() == location2.getBlockZ();
	}

	public enum RaceState {
		WAITING, LAUNCHING, RUNNING, FINISHED;
	}

	public enum RaceFinishType {
		NO_ENOUGH_PLAYERS, ALL_PLAYERS_FINISHED;
	}

}
