package fr.badblock.bukkit.hub.inventories.selector.submenus.items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.badblock.bukkit.hub.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.objects.HubPlayer;
import fr.badblock.bukkit.hub.rabbitmq.listeners.SEntryInfosListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.i18n.Locale;
import fr.badblock.gameapi.utils.threading.TaskManager;
import fr.badblock.sentry.FullSEntry;

public abstract class SubGameSelectorItem extends CustomItem {

	int inGamePlayers = 0;
	int waitingLinePlayers = 0;

	public SubGameSelectorItem(String name, Material material) {
		this(name, material, (byte) 0, 1, "");
	}

	public SubGameSelectorItem(String name, Material material, byte data, int amount, String lore) {
		super(name, material, data, amount, lore);
		SubGameSelectorItem item = this;
		TaskManager.scheduleSyncRepeatingTask("gameselector_" + name, new Runnable() {
			@Override
			public void run() {
				int tempWaitingLinePlayers = 0;
				int tempInGamePlayers = 0;
				for (String game : getGames()) {
					FullSEntry fullSEntry = SEntryInfosListener.sentries.get(game);
					if (fullSEntry == null) {
						continue;
					}
					tempWaitingLinePlayers += fullSEntry.getWaitinglinePlayers();
					tempInGamePlayers += fullSEntry.getIngamePLayers();
				}
				if (waitingLinePlayers == tempWaitingLinePlayers && inGamePlayers == tempInGamePlayers)
					return;
				waitingLinePlayers = tempWaitingLinePlayers;
				inGamePlayers = tempInGamePlayers;
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
					if (hubPlayer.getCurrentInventory().getItems().containsValue(item)) {
						getKeysByValue(hubPlayer.getCurrentInventory().getItems(), item).stream().forEach(
								slot -> player.getOpenInventory().getTopInventory().setItem(slot, toItemStack(player)));
					}
				}
			}
		}, 20, 20);
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

	public abstract List<String> getGames();

	public ItemStack rebuildLore(ItemStack itemStack, Locale locale) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (this.getLore() != null && !this.getLore().isEmpty())
			itemMeta.setLore(
					Arrays.asList(GameAPI.i18n().get(locale, this.getLore(), inGamePlayers, waitingLinePlayers)));
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
					Arrays.asList(GameAPI.i18n().get(locale, this.getLore(), inGamePlayers, waitingLinePlayers)));
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	@Override
	public ItemStack toItemStack(BadblockPlayer player) {
		return toItemStack(player.getPlayerData().getLocale());
	}

}
