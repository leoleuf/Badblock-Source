package fr.badblock.common.shoplinker.bukkit.listeners.rabbitmq;

import org.bukkit.ChatColor;

import com.google.gson.Gson;

import fr.badblock.api.common.tech.rabbitmq.RabbitService;
import fr.badblock.api.common.tech.rabbitmq.listener.RabbitListener;
import fr.badblock.common.shoplinker.api.ShopLinkerSettings;
import fr.badblock.common.shoplinker.api.objects.ShopData;
import fr.badblock.common.shoplinker.bukkit.ShopLinkWorker;
import fr.badblock.common.shoplinker.bukkit.ShopLinker;
import fr.badblock.common.shoplinker.bukkit.events.ReceivedRemoteCommandEvent;
import fr.badblock.common.shoplinker.bukkit.utils.FlagObject;
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
		this.load();
	}

	@Override
	public void onPacketReceiving(String body)
	{
		if (body == null)
		{
			return;
		}
		
		if (ShopLinker.getInstance().isUnloaded())
		{
			return;
		}

		// Flag
		if (FlagObject.isValid(body, "same"))
		{
			return;
		}
		FlagObject.setTemporaryFlag(body, "same", 1000);

		ShopData shopData = gson.fromJson(body, ShopData.class);
		if (shopData == null)
		{
			return;
		}
		// exécution
		if (!enabledCommands)
		{
			ShopLinker.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RED + "A command has been executed but commands aren't enabled on this server.");
			ShopLinker.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RED + "Command: " + shopData.getCommand());
			return;
		}
		
		ReceivedRemoteCommandEvent receivedRemoteCommand = new ReceivedRemoteCommandEvent(shopData);
		
		ShopLinker.getInstance().getServer().getPluginManager().callEvent(receivedRemoteCommand);
		
		if (receivedRemoteCommand.isCancelled()) return;
		
		ShopLinkWorker.workCommand(receivedRemoteCommand.getShopData(), false);
	}

}
