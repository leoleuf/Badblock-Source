package fr.badblock.spaceballs.commands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.general.StringUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.spaceballs.PluginSB;
import fr.badblock.spaceballs.rockets.Rockets;
import fr.badblock.spaceballs.runnables.StartRunnable;

public class GameCommand extends AbstractCommand {
	public GameCommand() {
		super("game", new TranslatableString("commands.gsb.usage"), GamePermission.BMODERATOR);
		allowConsole(false);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args) {
		if(args.length == 0) {
			return false;
		}
		
		BadblockPlayer player = (BadblockPlayer) sender;
		PluginSB plug = PluginSB.getInstance();

		switch(args[0].toLowerCase()){
			case "start":
				String msg = "commands.gsb.start";
				
				if(!StartRunnable.started()){
					StartRunnable.startGame(true);
				} else msg += "-fail";
				
				player.sendTranslatedMessage(msg);
			break;
			case "stop":
				msg = "commands.gsb.stop";
				
				if(StartRunnable.started()){
					StartRunnable.stopGame();
				} else msg += "-fail";
				
				player.sendTranslatedMessage(msg);
			break;
			case "playersperteam":
				if(args.length != 2)
					return false;
				
				int perTeam = 4;
				
				try {
					perTeam = Integer.parseInt(args[1]);
				} catch(Exception e){
					return false;
				}
				
				for(BadblockTeam team : GameAPI.getAPI().getTeams())
					team.setMaxPlayers(perTeam);
				
				plug.getConfiguration().maxPlayersInTeam = perTeam;
				plug.setMaxPlayers(GameAPI.getAPI().getTeams().size() * perTeam);
				
				player.sendTranslatedMessage("commands.gsb.modifycount");
			break;
			case "rocket":
				if(args.length != 3)
					return false;
				
				if(GameAPI.getAPI().getGameServer().getGameState() != GameState.RUNNING){
					player.sendTranslatedMessage("commands.gsb.notingame");
				} else {
					Rockets rocket = null;
					
					try {
						rocket = Rockets.valueOf(args[1].toUpperCase());
					} catch(Exception e){
						
					} finally {
						if(rocket == null){
							player.sendTranslatedMessage("commands.gsb.unknowrocket", StringUtils.join(Arrays.asList(Rockets.values()), "&b, ", "&7, "));
							return true;
						}
					}
					
					int count = 0;
					
					try {
						count = Integer.parseInt(args[2]);
					} catch(Exception e){
						
					} finally {
						if(count <= 0){
							player.sendTranslatedMessage("commands.nan", args[2]);
							return true;
						}
					}
					
					ItemStack item = Rockets.createRocket(rocket, count);
					
					if(item == null)
						return false;
					
					if(player.getInventory().addItem(item).size() > 0)
						player.sendTranslatedMessage("commands.gsb.notenoughplace");
					else
						player.sendTranslatedMessage("commands.gsb.given");
				}
			break;
			default: return false;
		}
		
		return true;
	}
}