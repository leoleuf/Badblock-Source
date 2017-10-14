package fr.badblock.bukkit.hub.inventories.market.cosmetics.boosters.inventories;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.hub.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.inventories.market.cosmetics.boosters.items.PlayerBoostersSelectorItem;
import fr.badblock.bukkit.hub.inventories.market.cosmetics.inventoryitems.BackCosmeticsItem;
import fr.badblock.bukkit.hub.inventories.market.ownitems.OwnableItem;
import fr.badblock.bukkit.hub.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.inventories.settings.settings.LightBlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.data.PlayerData;
import fr.badblock.gameapi.players.data.boosters.Booster;
import fr.badblock.gameapi.players.data.boosters.PlayerBooster;
import fr.badblock.gameapi.utils.ConfigUtils;
import fr.badblock.gameapi.utils.general.TimeUnit;

public class BoosterInventory extends CustomInventory {

	private static char CONFIGURATION_SEPARATOR = '.';

	public BoosterInventory() {
		super("hub.items.boosterinventory", ConfigUtils.getInt(main(),
				"booster" + CONFIGURATION_SEPARATOR + "inventory" + CONFIGURATION_SEPARATOR + "lines"));
		BlueStainedGlassPaneItem blueStainedGlassPaneItem = new BlueStainedGlassPaneItem();
		for (int id = 0; id < this.getLines() * 9; id++)
			if ((id == 0 || id < 9 || id % 9 == 0 || id == 17 || id == 26 || id == 35 || id == 44 || id == 53
					|| id > (9 * (this.getLines() - 1)) - 1))
				this.setItem(blueStainedGlassPaneItem, id);
		/*
		 * MAN: Each booster will contains a list of specified servers who are
		 * effective for the booster (* for everything), these must be in the
		 * game dictionary [who is BadblockGame enum] The configuration purpose
		 * also an effective booster duration time (in seconds) XP & BadCoins
		 * multiplier fields are also queryable in configura Also, the ItemStack
		 * will be able to be configurable (name/lore to I18N files) and shown
		 * item (material/data/amount) & where it will stay (slot)
		 */
		FileConfiguration configuration = main().getConfig();
		ConfigurationSection configurationSection = configuration
				.getConfigurationSection("booster" + CONFIGURATION_SEPARATOR + "data");
		configurationSection.getKeys(false).forEach(key -> {
			ConfigurationSection boosterConfiguration = configurationSection.getConfigurationSection(key);
			String fullKey = boosterConfiguration.getCurrentPath() + CONFIGURATION_SEPARATOR;
			String multiplierPrefixKey = fullKey + "multipliers" + CONFIGURATION_SEPARATOR;
			String thanksPrefixKey = fullKey + "thanks" + CONFIGURATION_SEPARATOR;;
			String randomEarnPrefixKey = fullKey + "random_earn" + CONFIGURATION_SEPARATOR;
			String itemPrefixKey = fullKey + "item" + CONFIGURATION_SEPARATOR;
			int id = Integer.parseInt(key);
			long time = ConfigUtils.get(main(), fullKey + "effectiveTime", 0);
			boolean buyable = ConfigUtils.get(main(), fullKey + "price.buyable", false);
			int badcoinsNeeded = ConfigUtils.get(main(), fullKey + "price.badcoins", 0);
			double badcoinsMultiplier = ConfigUtils.get(main(), multiplierPrefixKey + "badcoins", 1.0D);
			double xpMultiplier = ConfigUtils.get(main(), multiplierPrefixKey + "xp", 1.0D);
			double maxThanksBadcoins = ConfigUtils.get(main(), thanksPrefixKey + "maxbadcoins", 1.0D);
			double maxThanksXp = ConfigUtils.get(main(), thanksPrefixKey + "maxxp", 1.0D);
			double minEarnedXp = ConfigUtils.get(main(), randomEarnPrefixKey + "minXp", 1.0D);
			double maxEarnedXp = ConfigUtils.get(main(), randomEarnPrefixKey + "maxXp", 1.0D);
			double minEarnedBadcoins = ConfigUtils.get(main(), randomEarnPrefixKey + "minBadcoins", 1.0D);
			double maxEarnedBadcoins = ConfigUtils.get(main(), randomEarnPrefixKey + "maxBadcoins", 1.0D);
			Material material = Material
					.getMaterial(ConfigUtils.get(main(), itemPrefixKey + "material", Material.STONE.name()));
			byte data = (byte) ConfigUtils.getInt(main(), itemPrefixKey + "data");
			int amount = ConfigUtils.get(main(), itemPrefixKey + "amount", 1);
			int slot = ConfigUtils.get(main(), itemPrefixKey + "slot", 1);
			this.setItem(slot, new OwnableItem("boosters", Integer.toString(id), material, data, amount, false) {

				@Override
				public void onClick(BadblockPlayer player, ItemAction action, Block clickedBlock) {
					if (hasPermission() && !player.hasPermission("hub" + CONFIGURATION_SEPARATOR + getConfigPrefix())) {
						player.sendTranslatedMessage("hub.boosters.nopermission");
						return;
					}
					if (!buyable) {
						player.sendTranslatedMessage("hub.boosters." + id + ".youcannotbuyitlikethat");
						return;
					}
					PlayerData playerData = player.getPlayerData();
					int badcoinsa = playerData.getBadcoins();
					if (badcoinsa < getNeededBadcoins()) {
						player.sendTranslatedMessage("hub.boosters.notenoughbadcoins", getNeededBadcoins() - badcoinsa);
						return;
					}
					playerData.removeBadcoins(getNeededBadcoins());
					HubPlayer hubPlayer = HubPlayer.get(player);
					hubPlayer.updateScoreboard();
					Booster booster = new Booster(id, badcoinsMultiplier, xpMultiplier, minEarnedBadcoins, maxEarnedBadcoins, minEarnedXp, maxEarnedXp, maxThanksBadcoins, maxThanksXp, (time * 1000L));
					PlayerBooster playerBooster = new PlayerBooster(HubPlayer.getRealName(player), -1, false, 0, 0, null, booster);
					playerData.getBoosters().add(playerBooster);
					if (playerData.getBoosters().size() >= 7) {
						player.sendTranslatedMessage("hub.boosters.reachedLimit");
						return;
					}
					player.saveGameData();
					player.sendTranslatedMessage("hub.boosters.bought", player.getTranslatedMessage("hub.boosters." + id + ".name")[0]);
					player.playSound(Sound.LEVEL_UP);
				}

				@Override
				public ItemStack toItemStack(BadblockPlayer player) {
					return build(this.getMaterial(), this.getAmount(), this.getData(),
							player.getTranslatedMessage("hub.boosters." + id + ".name")[0],
							player.getTranslatedMessage("hub.boosters." + id + ".lore", getNeededBadcoins(),
									TimeUnit.SECOND.toFrench(time), ((badcoinsMultiplier - 1) * 100), ((xpMultiplier - 1) * 100)));
				}

				@Override
				public int getNeededBadcoins() {
					return badcoinsNeeded;
				}

			});
		});
		this.setAsLastItem(new PlayerBoostersSelectorItem(), 8);
		this.setAsLastItem(new BackCosmeticsItem());
		this.setNoFilledItem(new LightBlueStainedGlassPaneItem());
	}

}
