package fr.badblock.bukkit.hub.commands;

import org.bukkit.command.CommandSender;

import fr.badblock.bukkit.hub.inventories.market.properties.CustomProperty;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;

public class HubGiveCommand extends AbstractCommand {

	public HubGiveCommand() {
		super("hubgive", null, "hub.give");
		allowConsole(true);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args) {
		// object player data amount
		if (args.length != 4)
		{
			sender.sendMessage("§cUsage: /hubgive <object> <player> <data> <amount>");
			return true;
		}
		String object = args[0];
		String playerName = args[1];
		String dataString = args[2];
		int data = -1;
		try
		{
			data = Integer.parseInt(dataString);
		}
		catch (Exception error)
		{
			sender.sendMessage("§cData must be int.");
			return true;
		}
		String amountString = args[2];
		int amount = -1;
		try
		{
			amount = Integer.parseInt(amountString);
		}
		catch (Exception error)
		{
			sender.sendMessage("§cAmount must be int.");
			return true;
		}
		if (amount > 0)
		{
			sender.sendMessage("§cAmount must higher than 0.");
			return true;
		}
		String objectName = object + "_" + data;
		if (!CustomProperty.isACustomProperty(objectName)) {
			System.out.println("[HUB] " + objectName + " isn't a property.");
			return true;
		}
		CustomProperty customProperty = CustomProperty.getPropertyTypeByName(objectName);
		BadblockPlayer player = BukkitUtils.getPlayer(playerName);
		if (player == null) 
		{
			sender.sendMessage("§cPlayer offline.");
			return true;
		}
		customProperty.run(player, object);
		return true;
	}
}
