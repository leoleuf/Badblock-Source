package fr.badblock.survival.commands;

import org.bukkit.command.CommandSender;

import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.survival.runnables.StartRunnable;

public class GameCommand extends AbstractCommand {
	public GameCommand() {
		super("game", new TranslatableString("commands.gsurvival.usage"), GamePermission.BMODERATOR, GamePermission.BMODERATOR, GamePermission.BMODERATOR);
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
				String msg = "commands.grush.start";
				
				if(!StartRunnable.started()){
					StartRunnable.startGame();
				} else msg += "-fail";
				
				player.sendTranslatedMessage(msg);
			break;
			case "stop":
				msg = "commands.grush.stop";
				
				if(StartRunnable.started()){
					StartRunnable.stopGame();
				} else msg += "-fail";
				
				player.sendTranslatedMessage(msg);
			break;
			default: return false;
		}
		
		return true;
	}
}