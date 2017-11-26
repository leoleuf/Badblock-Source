package fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.GameSelectorItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.bukkit.hub.v1.rabbitmq.listeners.SEntryInfosListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.i18n.Locale;
import fr.badblock.gameapi.utils.threading.TaskManager;
import fr.badblock.sentry.FullSEntry;

public abstract class SubGameSelectorItem extends CustomItem {

	private long lastRefresh;

	public SubGameSelectorItem(String name, Material material) {
		this(name, material, (byte) 0, 1, "");
	}

	public SubGameSelectorItem(String name, Material material, byte data, int amount, String lore) {
		super(name, material, data, amount, lore);
		TaskManager.scheduleSyncRepeatingTask("gameselector_" + name, new Runnable() {
			@Override
			public void run() {
				long timestamp = System.currentTimeMillis();
				int tempWaitingLinePlayers = 0;
				int tempInGamePlayers = 0;
				FullSEntry fullSEntry = SEntryInfosListener.sentries.get(getGame());
				if (fullSEntry == null) {
					return;
				}
				tempWaitingLinePlayers += fullSEntry.getWaitinglinePlayers();
				tempInGamePlayers += fullSEntry.getIngamePLayers();
				if (!(GameSelectorItem.waitingLinePlayers.containsKey(getGame())))
				{
					GameSelectorItem.waitingLinePlayers.put(getGame(), tempInGamePlayers);
				}
				if (!(GameSelectorItem.inGamePlayers.containsKey(getGame())))
				{
					GameSelectorItem.inGamePlayers.put(getGame(), tempInGamePlayers);
				}
				int waitingLinePlayersInt = GameSelectorItem.waitingLinePlayers.get(getGame());
				int inGamePlayersInt = GameSelectorItem.inGamePlayers.get(getGame());
				if (lastRefresh < timestamp)
				{
					if (waitingLinePlayersInt == tempWaitingLinePlayers && inGamePlayersInt == tempInGamePlayers)
						return;
					if (waitingLinePlayersInt > tempWaitingLinePlayers) waitingLinePlayersInt--;
					else if (waitingLinePlayersInt < tempWaitingLinePlayers) waitingLinePlayersInt++;
					if (inGamePlayersInt > tempInGamePlayers) inGamePlayersInt--;
					else if (inGamePlayersInt < tempInGamePlayers) inGamePlayersInt++;
					GameSelectorItem.waitingLinePlayers.put(getGame(), waitingLinePlayersInt);
					GameSelectorItem.inGamePlayers.put(getGame(), inGamePlayersInt);
					lastRefresh = timestamp + new Random().nextInt(1600) + 300;
				}
				Map<Locale, ItemStack> staticItems = new HashMap<>();
				for (Entry<Locale, ItemStack> entry : staticItem.entrySet())
					staticItems.put(entry.getKey(), rebuildLore(entry.getValue(), entry.getKey()));
				setStaticItem(staticItems);
				for (Player p : Bukkit.getOnlinePlayers()) {
					BadblockPlayer player = (BadblockPlayer) p;
					HubPlayer hubPlayer = HubPlayer.get(player);
					if (hubPlayer.getCurrentInventory() == null)
						continue;
					if (player.getOpenInventory() == null)
						continue;
					if (player.getOpenInventory().getTopInventory() == null)
						continue;
					if (hubPlayer.getCurrentInventory().getLines() * 9 != player.getOpenInventory().getTopInventory()
							.getSize())
						continue;
					if (!player.getTranslatedMessage(hubPlayer.getCurrentInventory().getName())[0]
							.equals(player.getOpenInventory().getTopInventory().getName()))
						continue;
					if (hubPlayer.getCurrentInventory().getItems().containsValue(SubGameSelectorItem.this)) {
						getKeysByValue(hubPlayer.getCurrentInventory().getItems(), SubGameSelectorItem.this).stream().forEach(
								slot -> player.getOpenInventory().getTopInventory().setItem(slot, toItemStack(player)));
					}
				}
			}
		}, 1, 1);
	}

	private static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
		return map.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), value)).map(Map.Entry::getKey)
				.collect(Collectors.toSet());
	}

	public SubGameSelectorItem(String name, Material material, byte data, String lore) {
		this(name, material, data, 1, lore);
	}

	public SubGameSelectorItem(String name, Material material, int amount, String lore) {
		this(name, material, (byte) 0, amount, lore);
	}

	public SubGameSelectorItem(String name, Material material, String lore) {
		this(name, material, (byte) 0, 1, lore);
	}

	public abstract boolean isMiniGame();

	public abstract String getGame();

	public ItemStack rebuildLore(ItemStack itemStack, Locale locale) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (this.getLore() != null && !this.getLore().isEmpty())
			itemMeta.setLore(
					Arrays.asList(GameAPI.i18n().get(locale, this.getLore(), GameSelectorItem.inGamePlayers.get(getGame()), GameSelectorItem.waitingLinePlayers.get(getGame()))));
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	@Override
	public ItemStack toItemStack(Locale locale) {
		ItemStack itemStack = new ItemStack(this.getMaterial(), this.getAmount(), this.getData());
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(GameAPI.i18n().get(locale, this.getName())[0]);
		if (this.getLore() != null && !this.getLore().isEmpty())
			itemMeta.setLore(
					Arrays.asList(GameAPI.i18n().get(locale, this.getLore(), GameSelectorItem.inGamePlayers.get(getGame()), GameSelectorItem.waitingLinePlayers.get(getGame()))));
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	@Override
	public ItemStack toItemStack(BadblockPlayer player) {
		return toItemStack(player.getPlayerData().getLocale());
	}

}
