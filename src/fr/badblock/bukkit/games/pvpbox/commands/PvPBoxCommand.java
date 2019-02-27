package fr.badblock.bukkit.games.pvpbox.commands;

import org.bukkit.command.CommandSender;

import fr.badblock.bukkit.games.pvpbox.PvPBox;
import fr.badblock.bukkit.games.pvpbox.config.BoxConfig;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class PvPBoxCommand extends AbstractCommand
{

	public PvPBoxCommand()
	{
		super("pvpbox", new TranslatableString("commands.pvpbox.usage"), GamePermission.ADMIN, GamePermission.ADMIN, GamePermission.ADMIN);
		this.allowConsole(false);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args)
	{
		try
		{
			GameAPI.i18n().sendMessage(sender, "commands.pvpbox.reload");
			BoxConfig.reload(PvPBox.getInstance());
			GameAPI.i18n().sendMessage(sender, "commands.pvpbox.reloaded");
		}
		catch (Exception error)
		{
			error.printStackTrace();
			GameAPI.i18n().sendMessage(sender, "commands.pvpbox.error");
		}
		
		return true;
	}

}
