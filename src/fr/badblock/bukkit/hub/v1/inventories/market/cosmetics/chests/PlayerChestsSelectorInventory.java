package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.chests;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Item;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomPlayerInventory;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.chests.objects.ChestLoader;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.chests.objects.ChestOpener;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.chests.objects.CustomChestType;
import fr.badblock.bukkit.hub.v1.inventories.market.properties.CustomProperty;
import fr.badblock.bukkit.hub.v1.inventories.market.properties.Properties;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.bukkit.hub.v1.objects.HubStoredPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.threading.TaskManager;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockAction;

public class PlayerChestsSelectorInventory extends CustomPlayerInventory {

	public PlayerChestsSelectorInventory() {
		super("hub.items.chestsinventory", 6);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void init(BadblockPlayer player) {
		this.getItems().clear();
		this.setAsLastItem(new PlayerQuitChestsItem());
		HubStoredPlayer hubStoredPlayer = HubStoredPlayer.get(player);
		BlueStainedGlassPaneItem blueStainedGlassPaneItem = new BlueStainedGlassPaneItem();
		for (int id = 0; id < this.getLines() * 9; id++)
			if ((id == 0 || id < 9 || id % 9 == 0 || id == 17 || id == 26 || id == 35 || id == 44 || id == 53 || id > (9 * (this.getLines() - 1)) - 1))
				this.setItem(blueStainedGlassPaneItem, id);
		if (hubStoredPlayer == null) return;
		Map<Integer, CustomChestType> chestsTypes = new HashMap<>();
		ChestLoader chestLoader = ChestLoader.getInstance();
		chestLoader.getChests().forEach(chest -> chestsTypes.put(chest.getId(), chest));
		hubStoredPlayer.getChests().stream().filter(chests -> !chests.isOpened()).forEach(chests -> {
			if (!chestsTypes.containsKey(chests.getTypeId())) {
				System.out.println("[HUB] Unknown chest type (id: " + chests.getTypeId() + " / " + player.getName() + ")");
				return;
			}
			CustomChestType chestType = chestsTypes.get(chests.getTypeId());
			this.addItem(new CustomItem("hub.chests." + chests.getTypeId() + ".name", chestType.getItemStack().getType(), chestType.getItemStack().getData().getData(), "hub.chests." + chests.getTypeId() + ".lore") {
				@Override
				public void onClick(BadblockPlayer player, ItemAction action, Block clickedBlock) {
					HubPlayer hubPlayer = HubPlayer.get(player);
					if (hubPlayer.getChestOpener() == null) {
						System.out.println("[HUB] Null chestOpener (id: " + chests.getTypeId() + " / " + player.getName() + ")");
						return;
					}
					ChestOpener chestOpener = hubPlayer.getChestOpener();
					// On close son inventaire
					player.closeInventory();
					if (chestType.getWinRates().size() < chestOpener.getChestLocations().size()) {
						player.sendMessage("§cNot enough winrates for dispatch all chest locations");
						return;
					}
					boolean hasEverything = true;
					for (String string : chestType.getWinRates().keySet()) {
						Properties property = Properties.get(string);
						if (property == null) {
							if (CustomProperty.isACustomProperty(string)) {
								hasEverything = false;
								break;
							}
							System.out.println("[BadBlockChest] Unknown property '" + string + "'");
							continue;
						}
						if (!property.getOwnableItem().has(player)) hasEverything = false;
					}
					if (hasEverything) {
						player.sendTranslatedMessage("hub.chests.youhaveallrewardsofthischest");
						return;
					}
					// On dit à son chest qu'il a déjà été ouvert
					chests.setOpened(true);
					player.saveGameData();
					// On le freeze temporairement
					hubPlayer.setChestFreezeLocation(player.getLocation());
					hubPlayer.setChestFreeze(true);
					// On créé un thread spécial pour le chest
					List<String> items = new ArrayList<>();
					List<Item> droppedItems = new ArrayList<>();
					new Thread() {
						@Override
						public void run() {
							if (!player.isOnline()) return;
							// TODO crystal custom property missing
							for (Entry<Location, Location> entry : chestOpener.getChestLocations().entrySet()) {
								// Téléportation au coffre qui va s'ouvrir
								hubPlayer.setChestFreezeLocation(entry.getValue());
								TaskManager.runTask(new Runnable() {
									@Override
									public void run() {
										if (!player.isOnline()) return;
										hubPlayer.setChestFreeze(false);
										player.teleport(entry.getValue());
										hubPlayer.setChestFreeze(true);
									}
								});
								Bukkit.getScheduler().runTaskLater(BadBlockHub.getInstance(), new Runnable() {
									@Override
									public void run() {
										// Animation d'ouverture du coffre
										if (!player.isOnline()) return;
										BlockPosition position = new BlockPosition(entry.getKey().getBlockX(), entry.getKey().getBlockY(), entry.getKey().getBlockZ());
										PacketPlayOutBlockAction blockActionPacket = new PacketPlayOutBlockAction(position, net.minecraft.server.v1_8_R3.Block.getById(entry.getKey().getBlock().getTypeId()), (byte) 1, (byte) 1);
										((CraftPlayer) player).getHandle().playerConnection.sendPacket(blockActionPacket);
										player.playEffect(entry.getKey(), Effect.EXPLOSION, 1);
										player.playSound(Sound.EXPLODE);
										new Thread() {
											@Override
											public void run() {
												// on va récupérer le winrate total sur l'opener
												int totalWinRate = 0;
												Map<Integer, String> maps = new HashMap<>();
												for (Entry<String, Long> entry : chestType.getWinRates().entrySet()) {
													totalWinRate += entry.getValue();
													maps.put(totalWinRate, entry.getKey());
												}
												boolean done = false;
												int repeater = 0;
												while (!done && repeater < 50) {
													repeater++;
													List<Entry<Integer, String>> entries = maps.entrySet().stream().sorted((e1, e2) -> { return Integer.compare(e2.getKey(), e1.getKey()); }).collect(Collectors.toList());
													int randomRate = new SecureRandom().nextInt(totalWinRate);
													Entry<Integer, String> maxEntry = null;
													for (Entry<Integer, String> entry : entries) {
														if (entry.getKey() > randomRate) {
															maxEntry = entry;
															continue;
														}
														break;
													}
													if (maxEntry.getValue() == null) continue;
													final Entry<Integer, String> finalMaxEntry = maxEntry;
													String fullLengthProperty = maxEntry.getValue();
													Properties property = Properties.get(fullLengthProperty);
													boolean isACustomProperty = CustomProperty.isACustomProperty(fullLengthProperty);
													if (property == null && !isACustomProperty) {
														System.out.println("[BadBlockChest] Unknown property '" + fullLengthProperty + "'");
														continue;
													}
													TaskManager.runTask(new Runnable() {
														@Override
														public void run() {
															if (!player.isOnline()) return;
															player.playSound(Sound.LEVEL_UP);
														}
													});
													if (isACustomProperty) {
														CustomProperty customProperty = CustomProperty.getPropertyTypeByName(fullLengthProperty).run(player, fullLengthProperty.split("_")[1]);
														items.add(customProperty.name().toLowerCase());
														TaskManager.runTask(new Runnable() {
															@Override
															public void run() {
																if (!player.isOnline()) return;
																player.saveGameData();
																player.sendTranslatedMessage("hub.chests.wincustom." + customProperty.name().toLowerCase(), customProperty.getCustomI18n(player, fullLengthProperty.split("_")[1]));
																Item item = entry.getKey().getWorld().dropItem(entry.getKey().clone().add(0.5, 1, 0.5), customProperty.getCustomPropertyRunnable().getItemStack(player, fullLengthProperty.split("_")[1]));
																droppedItems.add(item);
																player.showFloatingText(player.getTranslatedMessage("hub.chests.winfloatingtext",
																		player.getTranslatedMessage("hub.chests.wintypes." + customProperty.name().toLowerCase())[0],
																		customProperty.getCustomPropertyRunnable().getCustomI18n(player, fullLengthProperty.split("_")[1]))[0], entry.getKey().clone().add(0, 1.5, 0), 10 * 20, 0.0d);
															}
														});
														done = true;
													}else if (!items.contains(maxEntry.getValue()) && !hubStoredPlayer.getProperties().contains(fullLengthProperty)) {
														items.add(maxEntry.getValue());
														TaskManager.runTask(new Runnable() {
															@Override
															public void run() {
																if (!player.isOnline()) return;
																hubStoredPlayer.getProperties().add(finalMaxEntry.getValue());
																player.saveGameData();
																player.sendTranslatedMessage("hub.chests.win", player.getTranslatedMessage("hub.chests.wintypes." + property.getOwnableItem().getParent())[0], player.getTranslatedMessage(property.getOwnableItem().getName())[0]);
																Item item = entry.getKey().getWorld().dropItem(entry.getKey().clone().add(0.5, 1, 0.5), property.getOwnableItem().toItemStack(player));
																droppedItems.add(item);
																player.showFloatingText(player.getTranslatedMessage("hub.chests.winfloatingtext",
																		player.getTranslatedMessage(property.getOwnableItem().getName())[0],
																		player.getTranslatedMessage("hub.chests.wintypes." + property.getOwnableItem().getParent())[0])[0], entry.getKey().clone().add(0, 1.5, 0), 10 * 20, 0.0d);
															}
														});
														done = true;
													}
												}
											}
										}.start();
									}
								}, 20);
								try {
									Thread.sleep(2000L);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
							hubPlayer.setChestFreeze(false);
							try {
								Thread.sleep(5000L);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							TaskManager.runTask(new Runnable() {
								@Override
								public void run() {
									droppedItems.forEach(droppedItem -> droppedItem.remove());
									if (!player.isOnline()) return;
									// on referme les chests
									for (Entry<Location, Location> entry : chestOpener.getChestLocations().entrySet()) {
										BlockPosition position = new BlockPosition(entry.getKey().getBlockX(), entry.getKey().getBlockY(), entry.getKey().getBlockZ());
										PacketPlayOutBlockAction blockActionPacket = new PacketPlayOutBlockAction(position, net.minecraft.server.v1_8_R3.Block.getById(entry.getKey().getBlock().getTypeId()), (byte) 1, (byte) 0);
										((CraftPlayer) player).getHandle().playerConnection.sendPacket(blockActionPacket);
									}
								}
							});
						}
					}.start();
				}

				@Override
				public List<ItemAction> getActions() {
					return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
							ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
				}

			});
		});
	}

	static <K,V extends Comparable<? super V>> List<Entry<K, V>> entriesSortedByValues(Map<K,V> map) {

		List<Entry<K,V>> sortedEntries = new ArrayList<Entry<K,V>>(map.entrySet());

		Collections.sort(sortedEntries, 
				new Comparator<Entry<K,V>>() {
			@Override
			public int compare(Entry<K,V> e1, Entry<K,V> e2) {
				return e2.getValue().compareTo(e1.getValue());
			}
		});

		return sortedEntries;
	}

}