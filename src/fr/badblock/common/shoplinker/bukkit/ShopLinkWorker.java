package fr.badblock.common.shoplinker.bukkit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.badblock.common.shoplinker.api.ShopLinkerAPI;
import fr.badblock.common.shoplinker.api.objects.ShopData;
import fr.badblock.common.shoplinker.api.objects.ShopType;
import fr.badblock.common.shoplinker.bukkit.database.BadblockDatabase;
import fr.badblock.common.shoplinker.bukkit.database.Request;
import fr.badblock.common.shoplinker.bukkit.database.Request.RequestType;

public class ShopLinkWorker {

	public static void workCommand(ShopData shopData, boolean onlyIfOnline) {
		String playerName = shopData.getPlayerName();
		Player player = Bukkit.getPlayer(playerName);
		if (player == null) 
			if (onlyIfOnline) return;
			else cacheAction(shopData);
		else {
			String command = shopData.getObjectName().replace("%0", shopData.getPlayerName());
			Bukkit.getScheduler().runTaskLater(ShopLinker.getInstance(), new Runnable() {
				@Override
				public void run() {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
				}
			}, 1);
			ShopLinker.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RESET + "Executed command to " + shopData.getPlayerName() + ".");
			ShopLinker.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RESET + "Command: " + command);
			broadcastCommand(shopData);
		}
	}

	private static void cacheAction(ShopData shopData) {
		BadblockDatabase.getInstance().addRequest(
				new Request("INSERT INTO cachedShop(serverName, playerName, displayName, objectName, type) VALUES('" + ShopLinkerAPI.CURRENT_SERVER_NAME 
						+ "', '" + shopData.getPlayerName() + "', '" + shopData.getDisplayName() + "', '" + shopData.getObjectName() + "', '" + shopData.getDataType() + "')", RequestType.SETTER));
		ShopLinker.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RESET + "Added cached action to " + shopData.getPlayerName());
		ShopLinker.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RESET + "Data: " + shopData.getObjectName());
	}
	
	public static void broadcastCommand(ShopData shopData) {
		ShopLinker shopLinker = ShopLinker.getInstance();
		String message = shopData.getDataType().equals(ShopType.BUY) ? shopLinker.getBoughtMessage() : shopLinker.getRewardMessage();
		message = message.replace("%0", shopData.getPlayerName()).replace("%1", shopData.getObjectName()).replace("%2", shopData.getDisplayName());
		if (message != null && !message.isEmpty()) Bukkit.broadcastMessage(message);
	}

}
