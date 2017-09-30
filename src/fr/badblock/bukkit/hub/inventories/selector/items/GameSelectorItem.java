package fr.badblock.bukkit.hub.inventories.selector.items;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

import fr.badblock.bukkit.hub.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.inventories.market.cosmetics.boosters.inventories.RealTimeBoosterManager;
import fr.badblock.bukkit.hub.objects.HubPlayer;
import fr.badblock.bukkit.hub.rabbitmq.listeners.SEntryInfosListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.data.boosters.PlayerBooster;
import fr.badblock.gameapi.run.BadblockGame;
import fr.badblock.gameapi.utils.general.TimeUnit;
import fr.badblock.gameapi.utils.i18n.Locale;
import fr.badblock.gameapi.utils.itemstack.ItemStackUtils;
import fr.badblock.gameapi.utils.threading.TaskManager;
import fr.badblock.sentry.FullSEntry;

public abstract class GameSelectorItem extends CustomItem {

	public static HashMap<String, Integer> inGamePlayers = new HashMap<>();
	public static HashMap<String, Integer> waitingLinePlayers = new HashMap<>();

	private long lastRefresh;

	public GameSelectorItem(String name, Material material) {
		this(name, material, (byte) 0, 1, "");
	}

	public GameSelectorItem(String name, Material material, byte data, int amount, String lore) {
		super(name, material, data, amount, lore);
		GameSelectorItem item = this;
		TaskManager.scheduleSyncRepeatingTask("gameselector_" + name, new Runnable() {
			@Override
			public void run() {
				long timestamp = System.currentTimeMillis();
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
				if (!(waitingLinePlayers.containsKey(getGamePrefix())))
				{
					waitingLinePlayers.put(getGamePrefix(), tempWaitingLinePlayers);
				}
				if (!(inGamePlayers.containsKey(getGamePrefix())))
				{
					inGamePlayers.put(getGamePrefix(), tempInGamePlayers);
				}
				int waitingLinePlayersInt = waitingLinePlayers.get(getGamePrefix());
				int inGamePlayersInt = inGamePlayers.get(getGamePrefix());
				if (waitingLinePlayersInt == tempWaitingLinePlayers && inGamePlayersInt == tempInGamePlayers) {
					if (!RealTimeBoosterManager.stockage.containsKey(getGamePrefix()) || (RealTimeBoosterManager.stockage.get(getGamePrefix()) != null && (!RealTimeBoosterManager.stockage.get(getGamePrefix()).isValid() || !RealTimeBoosterManager.stockage.get(getGamePrefix()).isEnabled()))) {
						return;
					}
				}
				if (lastRefresh < timestamp)
				{
					if (waitingLinePlayersInt > tempWaitingLinePlayers) waitingLinePlayersInt--;
					else if (waitingLinePlayersInt < tempWaitingLinePlayers) waitingLinePlayersInt++;
					if (inGamePlayersInt > tempInGamePlayers) inGamePlayersInt--;
					else if (inGamePlayersInt < tempInGamePlayers) inGamePlayersInt++;
					waitingLinePlayers.put(getGamePrefix(), waitingLinePlayersInt);
					inGamePlayers.put(getGamePrefix(), inGamePlayersInt);
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
					if (hubPlayer.getCurrentInventory().getLines() * 9 != player.getOpenInventory().getTopInventory().getSize())
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
		}, 1, 1);
	}

	private static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
		return map.entrySet().stream().filter(entry -> Objects.equals(entry.getValue(), value)).map(Map.Entry::getKey)
				.collect(Collectors.toSet());
	}

	public GameSelectorItem(String name, Material material, byte data, String lore) {
		this(name, material, data, 1, lore);
	}

	public GameSelectorItem(String name, Material material, int amount, String lore) {
		this(name, material, (byte) 0, amount, lore);
	}

	public GameSelectorItem(String name, Material material, String lore) {
		this(name, material, (byte) 0, 1, lore);
	}

	public abstract boolean isMiniGame();

	public abstract List<String> getGames();

	public abstract String getGamePrefix();

	public abstract BadblockGame getGame();

	public ItemStack rebuildLore(ItemStack itemStack, Locale locale) {
		ItemMeta itemMeta = itemStack.getItemMeta();
		if (this.isFakeEnchantment()) {
			itemStack = ItemStackUtils.fakeEnchant(itemStack);
		}
		if (this.getLore() != null && !this.getLore().isEmpty()) {
			String boosterLore = GameAPI.i18n().get(locale, "hub.items.booster.nobooster")[0]/*"§cAucun booster activé, on en a pas parlé avant."*/;
			if (RealTimeBoosterManager.stockage.containsKey(this.getGamePrefix())) {
				PlayerBooster playerBooster = RealTimeBoosterManager.stockage.get(this.getGamePrefix());
				if (playerBooster.isEnabled() && playerBooster.isValid()) {
					boosterLore = GameAPI.i18n().get(locale, "hub.items.booster.boost", playerBooster.getUsername(), (int) ((playerBooster.getBooster().getCoinsMultiplier() - 1) * 100), (int) ((playerBooster.getBooster().getXpMultiplier() - 1) * 100), TimeUnit.SECOND.toShort((playerBooster.getExpire() / 1000L) - (System.currentTimeMillis() / 1000L)))[0]; 
				}
			}
			itemMeta.setLore(Arrays.asList(GameAPI.i18n().get(locale, this.getLore(), inGamePlayers.get(getGamePrefix()), waitingLinePlayers.get(getGamePrefix()),
					(this.getGame() != null ? this.getGame().getDeveloper() : ""), boosterLore)));
		}
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	@Override
	public ItemStack toItemStack(Locale locale) {
		ItemStack itemStack = new ItemStack(this.getMaterial(), this.getAmount(), this.getData());
		if (this.isFakeEnchantment()) {
			itemStack = ItemStackUtils.fakeEnchant(itemStack);
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(GameAPI.i18n().get(locale, this.getName())[0]);
		if (this.getLore() != null && !this.getLore().isEmpty()) {
			String boosterLore = GameAPI.i18n().get(locale, "hub.items.booster.nobooster")[0]/*"§cAucun booster activé, on en a pas parlé avant."*/;
			if (RealTimeBoosterManager.stockage.containsKey(this.getGamePrefix())) {
				PlayerBooster playerBooster = RealTimeBoosterManager.stockage.get(this.getGamePrefix());
				if (playerBooster.isEnabled() && playerBooster.isValid()) {
					boosterLore = GameAPI.i18n().get(locale, "hub.items.booster.boost", playerBooster.getUsername(), (int) ((playerBooster.getBooster().getCoinsMultiplier() - 1) * 100), (int) ((playerBooster.getBooster().getXpMultiplier() - 1) * 100), TimeUnit.SECOND.toShort((playerBooster.getExpire() / 1000L) - (System.currentTimeMillis() / 1000L)))[0]; 
				}
			}
			itemMeta.setLore(Arrays.asList(GameAPI.i18n().get(locale, this.getLore(), inGamePlayers.get(getGamePrefix()), waitingLinePlayers.get(getGamePrefix()),
					(this.getGame() != null ? this.getGame().getDeveloper() : ""), boosterLore)));
		}
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	@Override
	public ItemStack toItemStack(BadblockPlayer player) {
		return toItemStack(player.getPlayerData().getLocale());
	}

}
