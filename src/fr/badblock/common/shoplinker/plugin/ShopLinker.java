package fr.badblock.common.shoplinker.plugin;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import fr.badblock.common.shoplinker.api.ShopLinkerAPI;
import fr.badblock.common.shoplinker.plugin.database.BadblockDatabase;
import fr.badblock.common.shoplinker.plugin.listeners.ReceiveShopDataListener;
import fr.badblock.rabbitconnector.RabbitConnector;
import lombok.Getter;

public class ShopLinker extends JavaPlugin {

	@Getter private static ShopLinker			instance;
	@Getter private static ConsoleCommandSender console;
	@Getter private static String			 	message;
	
	@Override
	public void onEnable() {
		instance = this;
		this.reloadConfig();
		console = Bukkit.getConsoleSender();
		FileConfiguration configuration = this.getConfig();
		List<String> stockList = configuration.getStringList("rabbit.hostname");
		String[] stockArr = new String[stockList.size()];
		stockArr = stockList.toArray(stockArr);
		RabbitConnector.getInstance().newService("default", configuration.getInt("rabbit.port"), configuration.getString("rabbit.username"), configuration.getString("rabbit.password"), configuration.getString("rabbit.virtualhost"), stockArr);
		BadblockDatabase.getInstance().connect(configuration.getString("db.host"), configuration.getInt("db.port"), configuration.getString("db.user"), configuration.getString("db.pass"), configuration.getString("db.db"));
		ShopLinkerAPI.CURRENT_SERVER_NAME = configuration.getString("queueName");
		try {
			new ReceiveShopDataListener(configuration.getString("queueName"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		message = configuration.getString("boughtMessage", "%0 %1");
	}
	
}
