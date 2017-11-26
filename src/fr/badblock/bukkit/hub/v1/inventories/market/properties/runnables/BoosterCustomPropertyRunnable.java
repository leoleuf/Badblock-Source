package fr.badblock.bukkit.hub.v1.inventories.market.properties.runnables;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.inventories.market.properties.CustomPropertyRunnable;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.data.PlayerData;
import fr.badblock.gameapi.players.data.boosters.Booster;
import fr.badblock.gameapi.players.data.boosters.PlayerBooster;
import fr.badblock.gameapi.utils.ConfigUtils;

public class BoosterCustomPropertyRunnable extends CustomPropertyRunnable {

	/*
	 * private int    id;
	private double coinsMultiplier;
	private double xpMultiplier;
	private double minRandomBadcoins;
	private double maxRandomBadcoins;
	private double minRandomXp;
	private double maxRandomXp;
	private double maxBadcoins;
	private double maxXP;
	private long   length;
	
	
			double maxThanksBadcoins = ConfigUtils.get(main(), thanksPrefixKey + "maxbadcoins", 1.0D);
			double maxThanksXp = ConfigUtils.get(main(), thanksPrefixKey + "maxxp", 1.0D);
			double minEarnedXp = ConfigUtils.get(main(), randomEarnPrefixKey + "minXp", 1.0D);
			double maxEarnedXp = ConfigUtils.get(main(), randomEarnPrefixKey + "maxXp", 1.0D);
			double minEarnedBadcoins = ConfigUtils.get(main(), randomEarnPrefixKey + "minBadcoins", 1.0D);
			double maxEarnedBadcoins = ConfigUtils.get(main(), randomEarnPrefixKey + "minXp", 1.0D);
			String randomEarnPrefixKey = fullKey + "random_earn" + CONFIGURATION_SEPARATOR;
 */
	
	@Override
	public void work(BadblockPlayer player, String ownItem) {
		int id = Integer.parseInt(ownItem);
		PlayerData gamePlayerData = player.getPlayerData();
		Booster booster = new Booster(id, 
				ConfigUtils.getInt(BadBlockHub.getInstance(), "booster.data." + id + ".multipliers.badcoins"), 
				ConfigUtils.getInt(BadBlockHub.getInstance(), "booster.data." + id + ".multipliers.xp"), 
				ConfigUtils.getInt(BadBlockHub.getInstance(), "booster.data." + id + ".random_earn.minBadcoins"), 
				ConfigUtils.getInt(BadBlockHub.getInstance(), "booster.data." + id + ".random_earn.maxBadcoins"),
				ConfigUtils.getInt(BadBlockHub.getInstance(), "booster.data." + id + ".random_earn.minXp"), 
				ConfigUtils.getInt(BadBlockHub.getInstance(), "booster.data." + id + ".random_earn.maxXp"), 
				ConfigUtils.getInt(BadBlockHub.getInstance(), "booster.data." + id + ".thanks.maxbadcoins"), 
				ConfigUtils.getInt(BadBlockHub.getInstance(), "booster.data." + id + ".thanks.maxxp"), 
				(ConfigUtils.getInt(BadBlockHub.getInstance(), "booster.data." + id + ".effectiveTime") * 1000L));
		PlayerBooster playerBooster = new PlayerBooster(HubPlayer.getRealName(player), -1, false, 0, 0, null, booster);
		gamePlayerData.getBoosters().add(playerBooster);
		player.saveGameData();
	}

	@Override
	public String getCustomI18n(BadblockPlayer player, String ownItem) {
		return player.getTranslatedMessage("hub.boosters." + ownItem + ".name")[0];
	}

	@Override
	public ItemStack getItemStack(BadblockPlayer player, String ownItem) {
		return new ItemStack(Material.getMaterial(ConfigUtils.get(BadBlockHub.getInstance(), "booster.data." + ownItem + ".item.material", Material.GOLD_INGOT.name())), ConfigUtils.getInt(BadBlockHub.getInstance(), "booster.data." + ownItem + ".item.amount"), (byte) ConfigUtils.getInt(BadBlockHub.getInstance(), "booster.data." + ownItem + ".item.data"));
	}

}
