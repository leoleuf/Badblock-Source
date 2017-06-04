package fr.badblock.bukkit.games.badsoup.commands;

import org.bukkit.command.CommandSender;

import fr.badblock.bukkit.games.badsoup.PluginSoup;
import fr.badblock.bukkit.games.badsoup.runnables.StartRunnable;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class GameCommand extends AbstractCommand {
	public GameCommand() {
		super("game", new TranslatableString("commands.gsurvival.usage"), "animation.gamecommand");
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
			case "players":
				if(args.length != 2)
					return false;
				
				int maxPlayers = 24;
				
				try {
					maxPlayers = Integer.parseInt(args[1]);
				} catch(Exception e){
					return false;
				}
				
				PluginSoup plug = PluginSoup.getInstance();
				plug.getConfiguration().maxPlayers = maxPlayers;
				plug.getConfiguration().minPlayers = maxPlayers / 2;
				plug.setMaxPlayers(maxPlayers);
				try {
					BukkitUtils.setMaxPlayers(maxPlayers);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				player.sendTranslatedMessage("commands.grush.modifycount");
			break;
			default: return false;
		}
		
		return true;
	}
}