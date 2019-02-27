package fr.badblock.bukkit.games.pvpbox.commands;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.badblock.bukkit.games.pvpbox.players.BoxPlayer;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class GiveKitCommand extends AbstractCommand
{

	public GiveKitCommand()
	{
		super("givekit", new TranslatableString("commands.givekit.usage"), GamePermission.ADMIN, GamePermission.ADMIN, GamePermission.ADMIN);
		this.allowConsole(true);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args)
	{
		// /givekit xMalware <kitName> <amount>

		if (args.length != 3)
		{
			return false;
		}

		if (!sender.hasPermission("pvpbox.admin"))
		{
			return false;
		}

		String playerName = args[0];
		String kitName = args[1];
		String rawAmount = args[2];
		long amount = 1;
		
		try
		{
			amount = Long.parseLong(rawAmount);
		}
		catch (Exception error)
		{
			GameAPI.i18n().sendMessage(sender, "commands.givekit.amount", rawAmount);
			return true;
		}

		Player plo = Bukkit.getPlayer(playerName);
		
		if (plo == null)
		{
			GameAPI.i18n().sendMessage(sender, "commands.givekit.offline", playerName);
			return true;
		}
		
		BoxPlayer bxp = BoxPlayer.get((BadblockPlayer) plo);
		
		if (bxp == null)
		{
			GameAPI.i18n().sendMessage(sender, "commands.givekit.offline", plo.getName());
			return true;
		}
		
		if (bxp.getKitUsages() == null)
		{
			bxp.setKitUsages(new HashMap<>());
		}
		
		if (!bxp.getKitUsages().containsKey(kitName))
		{
			bxp.getKitUsages().put(kitName, amount);
		}
		else
		{
			bxp.getKitUsages().put(kitName, bxp.getKitUsages().get(kitName) + amount);
		}
		
		GameAPI.i18n().sendMessage(sender, "commands.givekit.given", kitName, amount, plo.getName());
		
		return true;
	}
	
}