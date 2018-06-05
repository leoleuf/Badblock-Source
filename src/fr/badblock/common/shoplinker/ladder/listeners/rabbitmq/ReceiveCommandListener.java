package fr.badblock.common.shoplinker.ladder.listeners.rabbitmq;


import com.google.gson.Gson;

import fr.badblock.api.common.tech.rabbitmq.RabbitService;
import fr.badblock.api.common.tech.rabbitmq.listener.RabbitListener;
import fr.badblock.common.shoplinker.api.ShopLinkerSettings;
import fr.badblock.common.shoplinker.api.objects.ShopData;
import fr.badblock.common.shoplinker.ladder.ShopLinkWorker;
import fr.badblock.common.shoplinker.ladder.ShopLinkerLadder;
import fr.badblock.common.shoplinker.ladder.events.ReceivedRemoteCommandEvent;
import fr.badblock.ladder.api.Ladder;
import fr.badblock.ladder.api.chat.ChatColor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode(callSuper=false)
public class ReceiveCommandListener extends RabbitListener
{

	@Getter public static Gson gson	= new Gson();

	public static boolean enabledCommands;

	public ReceiveCommandListener(RabbitService rabbitService, String queueName)
	{
		super(rabbitService, ShopLinkerSettings.QUEUE_PREFIX + queueName, ShopLinkerSettings.LISTENER_TYPE, ShopLinkerSettings.DEBUG);
	}

	@Override
	public void onPacketReceiving(String body) {
		if (body == null) return;
		ShopData shopData = gson.fromJson(body, ShopData.class);
		if (shopData == null) return;
		// exécution
		if (!enabledCommands) {
			ShopLinkerLadder.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RED + "A command has been executed but commands aren't enabled on this server.");
			ShopLinkerLadder.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RED + "Command: " + shopData.getCommand());
			return;
		}
		ReceivedRemoteCommandEvent receivedRemoteCommand = new ReceivedRemoteCommandEvent(shopData);
		Ladder.getInstance().getPluginsManager().dispatchEvent(receivedRemoteCommand);
		if (receivedRemoteCommand.isCancelled()) return;
		ShopLinkWorker.workCommand(receivedRemoteCommand.getShopData());
	}

}
