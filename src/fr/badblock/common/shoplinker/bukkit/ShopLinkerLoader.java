package fr.badblock.common.shoplinker.bukkit;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginManager;

import com.google.gson.GsonBuilder;

import fr.badblock.common.shoplinker.api.ShopLinkerAPI;
import fr.badblock.common.shoplinker.bukkit.commands.OPICommand;
import fr.badblock.common.shoplinker.bukkit.commands.RInvCommand;
import fr.badblock.common.shoplinker.bukkit.commands.ShopExecuteCommand;
import fr.badblock.common.shoplinker.bukkit.database.BadblockDatabase;
import fr.badblock.common.shoplinker.bukkit.inventories.InventoriesLoader;
import fr.badblock.common.shoplinker.bukkit.inventories.utils.ChatColorUtils;
import fr.badblock.common.shoplinker.bukkit.listeners.bukkit.InventoryCloseListener;
import fr.badblock.common.shoplinker.bukkit.listeners.bukkit.PlayerInventoryClickListener;
import fr.badblock.common.shoplinker.bukkit.listeners.bukkit.PlayerJoinListener;
import fr.badblock.common.shoplinker.bukkit.listeners.rabbitmq.ReceiveCommandListener;
import fr.badblock.rabbitconnector.RabbitConnector;

public class ShopLinkerLoader {

	public ShopLinkerLoader(ShopLinker shopLinker) {
		loadEverything(shopLinker);
	}

	private void loadEverything(ShopLinker shopLinker) {
		loadConfiguration(shopLinker);
		loadFields(shopLinker);
		loadInventories(shopLinker);
		loadRabbitMQ(shopLinker);
		loadDatabase(shopLinker);
		loadListeners(shopLinker);
		loadCommands(shopLinker);
		saveConfiguration(shopLinker);
	}

	private void loadConfiguration(ShopLinker shopLinker) {
		shopLinker.reloadConfig();
	}

	private void loadFields(ShopLinker shopLinker) {
		ShopLinker.setInstance(shopLinker);
		FileConfiguration configuration = shopLinker.getConfig();
		shopLinker.setNotRestrictiveGson(new GsonBuilder().setPrettyPrinting().create());
		ShopLinker.setConsole(Bukkit.getConsoleSender());
		ShopLinkerAPI.CURRENT_SERVER_NAME = getString(configuration, "queueName");
		ReceiveCommandListener.enabledCommands = getBoolean(configuration, "enabledCommands");
		shopLinker.setBoughtMessage(ChatColorUtils.translate(getString(configuration, "messages.bought", "%0 %1")));
		shopLinker.setRewardMessage(ChatColorUtils.translate(getString(configuration, "messages.reward", "%0 %1")));
		shopLinker.setNothingToClaimMessage(ChatColorUtils.translate(getString(configuration, "messages.nothingtoclaim", "Nothing to claim for now.")));
		shopLinker.setSinglePendingMessage(ChatColorUtils.translate(getString(configuration, "messages.pending.single", "You've one pending purchase. Get your bought feature by clicking on this message. Be careful of your inventory space.")));
		shopLinker.setPluralPendingMessage(ChatColorUtils.translate(getString(configuration, "messages.pending.plural", "You've %0 pending purchases. Get your bought features by clicking on this message. Be careful of your inventory space.")));
		shopLinker.setSingleClaimMessage(ChatColorUtils.translate(getString(configuration, "messages.claimed.single", "You've claimed your purchase. Enjoy!")));
		shopLinker.setPluralClaimMessage(ChatColorUtils.translate(getString(configuration, "messages.claimed.plural", "You've claimed your purchases. Enjoy!")));
		shopLinker.setSingleHoverMessage(ChatColorUtils.translate(getString(configuration, "messages.pending.hover.single", "Click here to claim your pending purchase.")));
		shopLinker.setSingleHoverMessage(ChatColorUtils.translate(getString(configuration, "messages.pending.hover.plural", "Click here to claim your %0 pending purchases.")));
		shopLinker.setErrorMessage(ChatColorUtils.translate(getString(configuration, "messages.error", "Error occurred while trying to fetch potential pending purchases.")));
		shopLinker.setDependNeededMessage(ChatColorUtils.translate(getString(configuration, "messages.buy.dependNeeded", "You need the %0 offer.")));
		shopLinker.setUnknownDependOfferNameMessage(ChatColorUtils.translate(getString(configuration, "messages.buy.unknownDependOfferName", "A depend offer doesn't exist on the website.")));
		shopLinker.setAlreadyBoughtMessage(ChatColorUtils.translate(getString(configuration, "messages.buy.alreadyBought", "You already bought this offer.")));
		shopLinker.setYouBoughtMessage(ChatColorUtils.translate(getString(configuration, "messages.buy.youBought", "You just bought %0 offer.")));
		shopLinker.setUnknownOfferNameMessage(ChatColorUtils.translate(getString(configuration, "messages.buy.unknownOffer", "This offer doesn't exist.")));
		// que des ING pourris
		shopLinker.setNotEnoughCoinsMessage(ChatColorUtils.translate(getString(configuration, "messages.buy.notEnoughCoins", "Not enough coins. You have %0 but you need %1")));
		shopLinker.setCheckTransactionMessage(ChatColorUtils.translate(getString(configuration, "messages.buy.checkTransaction", "Checking transaction...")));
		shopLinker.setWebsiteConnectionMessage(ChatColorUtils.translate(getString(configuration, "messages.buy.websiteConnection", "Connecting to the transaction server...")));
		shopLinker.setSearchOfferMessage(ChatColorUtils.translate(getString(configuration, "messages.buy.searchOffer", "Searching the offer...")));
		shopLinker.setNotRegisteredMessage(ChatColorUtils.translate(getString(configuration, "messages.buy.notregistered", "You must be registered on the website to buy.")));
	}

	private void loadInventories(ShopLinker shopLinker) {
		InventoriesLoader.loadInventories(shopLinker);
	}

	private void loadRabbitMQ(ShopLinker shopLinker) {
		FileConfiguration configuration = shopLinker.getConfig();
		List<String> stockList = configuration.getStringList("rabbit.hostname");
		if (stockList == null | stockList.isEmpty()) configuration.set("rabbit.hostname", Arrays.asList("example.com"));
		String[] stockArr = new String[stockList.size()];
		stockArr = stockList.toArray(stockArr);
		shopLinker.setRabbitService(
				RabbitConnector.getInstance().newService("default", getInt(configuration, "rabbit.port"),
						getString(configuration, "rabbit.username"), getString(configuration, "rabbit.password"),
						getString(configuration, "rabbit.virtualhost"), stockArr)
				);
	}

	private void loadDatabase(ShopLinker shopLinker) {
		FileConfiguration configuration = shopLinker.getConfig();
		BadblockDatabase.getInstance().connect(getString(configuration, "db.host"), getInt(configuration, "db.port"), 
				getString(configuration, "db.user"), getString(configuration, "db.pass"), getString(configuration, "db.db"));
	}

	private void loadListeners(ShopLinker shopLinker) {
		loadRabbitListeners(shopLinker);
		loadBukkitListeners(shopLinker);
	}

	private void loadRabbitListeners(ShopLinker shopLinker) {
		FileConfiguration configuration = shopLinker.getConfig();
		try {
			new ReceiveCommandListener(getString(configuration, "queueName"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadBukkitListeners(ShopLinker shopLinker) {
		PluginManager pluginManager = shopLinker.getServer().getPluginManager();
		pluginManager.registerEvents(new PlayerJoinListener(), shopLinker);
		pluginManager.registerEvents(new PlayerInventoryClickListener(), shopLinker);
		pluginManager.registerEvents(new InventoryCloseListener(), shopLinker);
	}

	private void loadCommands(ShopLinker shopLinker) {
		shopLinker.getCommand("opi").setExecutor(new OPICommand());
		shopLinker.getCommand("rinv").setExecutor(new RInvCommand());
		shopLinker.getCommand("shopexecute").setExecutor(new ShopExecuteCommand());
	}

	private void saveConfiguration(ShopLinker shopLinker) {
		shopLinker.saveConfig();
	}
	
	private boolean getBoolean(FileConfiguration fileConfiguration, String key, boolean value) {
		if (!fileConfiguration.contains(key)) {
			fileConfiguration.set(key, value);
			ShopLinker.getInstance().saveConfig();
		}
		return fileConfiguration.getBoolean(key);
	}
	
	private boolean getBoolean(FileConfiguration fileConfiguration, String key) {
		return getBoolean(fileConfiguration, key, false);
	}
	
	private String getString(FileConfiguration fileConfiguration, String key, String value) {
		if (!fileConfiguration.contains(key)) {
			fileConfiguration.set(key, value);
			ShopLinker.getInstance().saveConfig();
		}
		return fileConfiguration.getString(key);
	}
	
	private String getString(FileConfiguration fileConfiguration, String key) {
		return getString(fileConfiguration, key, "");
	}
	
	private int getInt(FileConfiguration fileConfiguration, String key, int value) {
		if (!fileConfiguration.contains(key)) {
			fileConfiguration.set(key, value);
			ShopLinker.getInstance().saveConfig();
		}
		return fileConfiguration.getInt(key);
	}
	
	private int getInt(FileConfiguration fileConfiguration, String key) {
		return getInt(fileConfiguration, key, 0);
	}

}
