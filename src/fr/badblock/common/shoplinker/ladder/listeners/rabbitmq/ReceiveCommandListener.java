package fr.badblock.common.shoplinker.ladder.listeners.rabbitmq;


import com.google.gson.Gson;

import fr.badblock.common.shoplinker.api.ShopLinkerSettings;
import fr.badblock.common.shoplinker.api.objects.ShopData;
import fr.badblock.common.shoplinker.ladder.ShopLinkWorker;
import fr.badblock.common.shoplinker.ladder.ShopLinkerLadder;
import fr.badblock.common.shoplinker.ladder.events.ReceivedRemoteCommandEvent;
import fr.badblock.ladder.api.Ladder;
import fr.badblock.ladder.api.chat.ChatColor;
import fr.badblock.rabbitconnector.RabbitConnector;
import fr.badblock.rabbitconnector.RabbitListener;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data @EqualsAndHashCode(callSuper=false)
public class ReceiveCommandListener extends RabbitListener {

	@Getter public static Gson gson	= new Gson();

	public static boolean enabledCommands;

	public ReceiveCommandListener(String queueName) {
		super(RabbitConnector.getInstance().getService("default"), ShopLinkerSettings.QUEUE_PREFIX + queueName, ShopLinkerSettings.DEBUG, ShopLinkerSettings.LISTENER_TYPE);
	}

	@Override
	public void onPacketReceiving(String body) {
		if (body == null) return;
		ShopData shopData = gson.fromJson(body, ShopData.class);
		if (shopData == null) return;
		// exécution
		if (!enabledCommands) {
			ShopLinkerLadder.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RED + "A command has been executed but commands aren't enabled on this server.");
			ShopLinkerLadder.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RED + "Command: " + shopData.getObjectName());
			return;
		}
		ReceivedRemoteCommandEvent receivedRemoteCommand = new ReceivedRemoteCommandEvent(shopData);
		Ladder.getInstance().getPluginsManager().dispatchEvent(receivedRemoteCommand);
		if (receivedRemoteCommand.isCancelled()) return;
		ShopLinkWorker.workCommand(receivedRemoteCommand.getShopData());
	}

}
