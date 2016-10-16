package fr.badblock.survival.commands;

import java.io.File;

import org.bukkit.command.CommandSender;

import fr.badblock.gameapi.command.MapAbstractCommand;
import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.configuration.values.MapSelection;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.survival.PluginSurvival;

public class SurvivalCommand extends MapAbstractCommand {
	public SurvivalCommand(File folder) {
		super("survival", new TranslatableString("commands.survival.usage"), GamePermission.ADMIN, new String[]{"sg"}, folder);
		allowConsole(false);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args) {
		if(args.length == 0) return false;

		BadblockPlayer player = (BadblockPlayer) sender;
		PluginSurvival plug   = PluginSurvival.getInstance();


		switch(args[0].toLowerCase()){
			case "mainspawn":
				plug.getConfiguration().spawn = new MapLocation(player.getLocation());
				plug.saveJsonConfig();
				break;
			case "zombiezone":
				if (player.getSelection() == null) {
					player.sendTranslatedMessage("commands.noselection");
				} else {
					plug.getConfiguration().zombieGame = new MapSelection(player.getSelection());
					plug.saveJsonConfig();
				}
				break;
			case "mapbounds":
				if(args.length < 2)
					return false;
	
				setSelection(false, args[1], null, "mapBounds", player);
				break;
			case "spawnlocation":
				if(args.length < 2)
					return false;
	
				setLocation(false, args[1], null, "spawnLocation", player);
				break;
			case "spawns":
				if(args.length < 2)
					return false;
	
				setLocation(true, args[1], null, "spawns", player);
				break;
			case "deathmatchs":
				if(args.length < 2)
					return false;
	
				setLocation(true, args[1], null, "deathMatchs", player);
				break;
			case "specdeathmatch":
				if(args.length < 2)
					return false;
	
				setLocation(false, args[1], null, "specDeathmatch", player);
				break;
			default: return false;
		}

		player.sendTranslatedMessage("commands.survival.modified");

		return true;
	}
}
