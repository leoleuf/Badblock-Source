package fr.badblock.spaceballs.commands;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import fr.badblock.gameapi.command.MapAbstractCommand;
import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.spaceballs.PluginSB;

public class SBCommand extends MapAbstractCommand {
	public SBCommand(File folder) {
		super("spaceballs", new TranslatableString("commands.spaceballs.usage"), GamePermission.ADMIN, new String[]{"sb"}, folder);
		allowConsole(false);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args) {
		if(args.length == 0) return false;
		
		BadblockPlayer player = (BadblockPlayer) sender;
		// commande : /spaceballs [type] [map] (team)
		PluginSB plug = PluginSB.getInstance();

		
		switch(args[0].toLowerCase()){
			case "mainspawn":
				plug.getConfiguration().spawn = new MapLocation(player.getLocation());
				plug.saveJsonConfig();
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
			case "towerbounds":
				if(args.length < 3)
					return false;
				
				setSelection(false, args[1], null, "towerBounds", player);
			break;
			case "spawnselection":
				if(args.length < 3)
					return false;
				
				setSelection(false, args[1], args[2], "spawnSelection", player);
			break;
			case "chests":
				if(args.length < 3)
					return false;
				
				setLookedBlock(true, args[1], args[2], "chests", player, Material.CHEST);
			break;
			case "respawnlocation":
				if(args.length < 3)
					return false;
				
				setLocation(false, args[1], args[2], "respawnLocation", player);
			break;
			default: return false;
		}
		
		player.sendTranslatedMessage("commands.spaceballs.modified");
		
		return true;
	}
}
