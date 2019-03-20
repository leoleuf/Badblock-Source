package fr.badblock.common.shoplinker.bukkit;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import fr.badblock.api.common.tech.rabbitmq.packet.RabbitPacket;
import fr.badblock.api.common.tech.rabbitmq.packet.RabbitPacketEncoder;
import fr.badblock.api.common.tech.rabbitmq.packet.RabbitPacketMessage;
import fr.badblock.api.common.tech.rabbitmq.packet.RabbitPacketType;
import fr.badblock.common.shoplinker.api.ShopLinkerAPI;
import fr.badblock.common.shoplinker.api.objects.ShopData;
import fr.badblock.common.shoplinker.bukkit.utils.Callback;
import fr.badblock.common.shoplinker.bukkit.utils.Flags;
import fr.badblock.common.shoplinker.mongodb.MongoService;
import fr.badblock.common.shoplinker.workers.WorkerManager;
import fr.badblock.common.shoplinker.workers.objects.GetPlayerShopPoints;

public class ShopLinkWorker {

	public static void getShopPoints(UUID uuid, Callback<Double> callback)
	{
		WorkerManager.send(new GetPlayerShopPoints(uuid, callback));
	}

	public static void workCommand(ShopData shopData, boolean onlyIfOnline) {
		if (shopData.getCommand().equals("-"))
		{
			return;
		}
		
		String playerName = shopData.getPlayerName().trim();
		Player player = Bukkit.getPlayer(playerName);
		
		System.out.println("[ShopLinker] Player " + shopData.getPlayerName() + " is "+ (player == null ? "offline" : "online")); 
		
		if (player == null && !shopData.isForceCommand()) 
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
			if (player != null)
			{
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
	}

	private static void cacheAction(ShopData shopData) {
		if (shopData.getCommand().equals("-"))
		{
			return;
		}

		new Thread()
		{
			@Override
			public void run()
			{
				BasicDBObject obj = new BasicDBObject();
				obj.put("serverName", ShopLinkerAPI.CURRENT_SERVER_NAME);
				obj.put("playerName", shopData.getPlayerName().toLowerCase());
				obj.put("displayName", shopData.getDisplayName());
				obj.put("command", shopData.getCommand());
				obj.put("type", shopData.getDataType().name());
				obj.put("ingame", shopData.isIngame());
				obj.put("price", shopData.getPrice());

				ShopLinker shopLinker = ShopLinker.getInstance();
				MongoService mongoService = shopLinker.getMongoService();
				DB db = mongoService.getDb();
				DBCollection collection = db.getCollection("cachedShop");
				collection.insert(obj);

				ShopLinker.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RESET + "Added cached action to " + shopData.getPlayerName());
				ShopLinker.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RESET + "Data: " + shopData.getCommand());
			}
		}.start();
	}

	public static void broadcastCommand(ShopData shopData) {
		ShopLinker shopLinker = ShopLinker.getInstance();
		String message = "";
		switch (shopData.getDataType())
		{
		case BUY:
			message = shopLinker.getBoughtMessage();
			break;
		case ANIMATION:
			message = shopLinker.getAnimationMessage();
			break;
		case VOTE:
			message = shopLinker.getRewardMessage();
			break;
		case WEBACTION_COMPLETE:
			message = shopLinker.getWebActionCompleteMessage();
			break;
		}
		message = message.replace("%0", shopData.getPlayerName()).replace("%1", shopData.getCommand()).replace("%2", shopData.getDisplayName());
		if (message != null && !message.isEmpty()) Bukkit.broadcastMessage(message);

		// En jeu
		if (shopData.isIngame())
		{
			// &6[Info] &b'.$joueur['pseudo'].' &aa achet� l\'offre '.parseHTML($offer["displayname"]).' &acontre '.$offer["price"].' Crystals sur le site !
			for (String broadcastMessage : shopLinker.getBroadcastMessage())
			{
				broadcastMessage = broadcastMessage.replace("%player%", shopData.getPlayerName());
				broadcastMessage = broadcastMessage.replace("%displayName%", shopData.getDisplayName());
				broadcastMessage = broadcastMessage.replace("%price%", Double.toString(shopData.getPrice()));
				broadcastMessage = ChatColor.translateAlternateColorCodes('&', broadcastMessage);
				RabbitPacketMessage rabbitPacketMessage = new RabbitPacketMessage(8640000, broadcastMessage);
				RabbitPacket rabbitPacket = new RabbitPacket(rabbitPacketMessage, "guardian.broadcast", false, RabbitPacketEncoder.UTF8, RabbitPacketType.MESSAGE_BROKER);
				ShopLinker.getInstance().getRabbitService().sendPacket(rabbitPacket);
			}
		}

	}

}
