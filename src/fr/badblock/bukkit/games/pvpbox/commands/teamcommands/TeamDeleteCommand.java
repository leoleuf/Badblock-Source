package fr.badblock.bukkit.games.pvpbox.commands.teamcommands;

import org.bukkit.command.CommandSender;

import fr.badblock.bukkit.games.pvpbox.players.BoxPlayer;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class TeamDeleteCommand extends AbstractCommand
{

	public TeamDeleteCommand()
	{
		super("pvpbox", new TranslatableString("commands.pvpbox.usage"), GamePermission.ADMIN, GamePermission.ADMIN, GamePermission.ADMIN);
		this.allowConsole(false);
	}
	
	@Override
	public boolean executeCommand(CommandSender sender, String[] args)
	{
		BadblockPlayer player = (BadblockPlayer) sender;
		BoxPlayer boxp = (BoxPlayer) BoxPlayer.get(player);
		
		if(player.hasPermission("pvpbox.admin"))
		{
			//TODO: Allow that Player who have the permission can delete bad name team.
		}
		
		return false;
	}
	
}
