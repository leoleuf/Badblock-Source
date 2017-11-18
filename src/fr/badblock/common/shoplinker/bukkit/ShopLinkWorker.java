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
import fr.badblock.common.shoplinker.bukkit.utils.Flags;
import fr.badblock.rabbitconnector.RabbitPacketType;
import fr.badblock.utils.Encodage;

public class ShopLinkWorker {

	public static void workCommand(ShopData shopData, boolean onlyIfOnline) {
		if (shopData.getCommand().equals("-"))
		{
			return;
		}
		String playerName = shopData.getPlayerName();
		Player player = Bukkit.getPlayer(playerName);
		if (player == null) 
			if (onlyIfOnline) return;
			else cacheAction(shopData);
		else {
			String[] commands = shopData.getCommand().split(";");
			for (String command : commands)
			{
				command = command.replace("%player%", shopData.getPlayerName());
				final String finalCommand = command;
				Bukkit.getScheduler().runTask(ShopLinker.getInstance(), new Runnable() {
					@Override
					public void run() {
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), finalCommand);
					}
				});
				ShopLinker.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RESET + "Executed command to " + shopData.getPlayerName() + ".");
				ShopLinker.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RESET + "Command: " + command);
			}
			if (Flags.isValid(player, "work"))
			{
				return;
			}
			else
			{
				Flags.setTemporaryFlag(player, "work", 1000);
				broadcastCommand(shopData);			
			}
		}
	}

	private static void cacheAction(ShopData shopData) {
		if (shopData.getCommand().equals("-"))
		{
			return;
		}
		BadblockDatabase.getInstance().addSyncRequest(
				new Request("INSERT INTO cachedShop(serverName, playerName, displayName, command, type, ingame, price) VALUES('" + ShopLinkerAPI.CURRENT_SERVER_NAME 
						+ "', '" + shopData.getPlayerName() + "', '" + shopData.getDisplayName() + "', '" + shopData.getCommand() + "', '" + shopData.getDataType() + "', '" + shopData.isIngame() + "', '" + shopData.getPrice() + "')", RequestType.SETTER));
		ShopLinker.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RESET + "Added cached action to " + shopData.getPlayerName());
		ShopLinker.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RESET + "Data: " + shopData.getCommand());
	}

	public static void broadcastCommand(ShopData shopData) {
		ShopLinker shopLinker = ShopLinker.getInstance();
		String message = shopData.getDataType().equals(ShopType.BUY) ? shopLinker.getBoughtMessage() : shopLinker.getRewardMessage();
		message = message.replace("%0", shopData.getPlayerName()).replace("%1", shopData.getCommand()).replace("%2", shopData.getDisplayName());
		if (message != null && !message.isEmpty()) Bukkit.broadcastMessage(message);

		// En jeu
		if (shopData.isIngame())
		{
			// &6[Info] &b'.$joueur['pseudo'].' &aa acheté l\'offre '.parseHTML($offer["displayname"]).' &acontre '.$offer["price"].' Crystals sur le site !
			for (String broadcastMessage : ShopLinker.getInstance().getBroadcastMessage())
			{
				broadcastMessage = broadcastMessage.replace("%player%", shopData.getPlayerName());
				broadcastMessage = broadcastMessage.replace("%displayName%", shopData.getDisplayName());
				broadcastMessage = broadcastMessage.replace("%price%", Long.toString(shopData.getPrice()));
				broadcastMessage = ChatColor.translateAlternateColorCodes('&', broadcastMessage);
				ShopLinker.getInstance().getRabbitService().sendSyncPacket("guardian.broadcast", broadcastMessage, Encodage.UTF8, RabbitPacketType.MESSAGE_BROKER, 86400_000, false);
			}
		}

	}

}
