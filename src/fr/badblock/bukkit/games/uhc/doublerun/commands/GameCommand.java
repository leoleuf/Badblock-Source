package fr.badblock.bukkit.games.uhc.doublerun.commands;

import org.bukkit.command.CommandSender;

import fr.badblock.bukkit.games.uhc.doublerun.runnables.StartRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class GameCommand extends AbstractCommand {
	public GameCommand() {
		super("game", new TranslatableString("commands.gspeeduhc.usage"), GamePermission.PLAYER);
		allowConsole(false);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args) {
		if(args.length == 0) {
			return false;
		}

		BadblockPlayer player = (BadblockPlayer) sender;

		switch(args[0].toLowerCase()){
		case "start":
			if (GameAPI.getAPI().isHoster(player))
			{
				String msg = "commands.grush.start";

				StartRunnable.startGame(true);
				player.sendTranslatedMessage(msg);
			}
			break;
		case "stop":
			if (player.hasPermission(GamePermission.ADMIN))
			{
				String msg = "commands.grush.stop";

				if(StartRunnable.started()){
					StartRunnable.stopGame();
				} else msg += "-fail";

				player.sendTranslatedMessage(msg);
			}
			else
			{
				player.sendMessage("§cVous n'avez pas la permission de faire cela.");
			}
			break;
		default:
			player.sendMessage("§cCommande inconnue. Tapez /game pour plus d'infos.");
			break;
		}

		return true;
	}
}