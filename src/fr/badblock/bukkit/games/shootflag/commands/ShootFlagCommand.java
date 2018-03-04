package fr.badblock.bukkit.games.shootflag.commands;

import java.io.File;

import org.bukkit.command.CommandSender;

import fr.badblock.bukkit.games.shootflag.PluginShootFlag;
import fr.badblock.gameapi.command.MapAbstractCommand;
import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class ShootFlagCommand extends MapAbstractCommand {
	public ShootFlagCommand(File folder) {
		super("shootflag", new TranslatableString("commands.shootflag.usage"), GamePermission.ADMIN, new String[]{}, folder);
		allowConsole(false);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args) {
		if(args.length == 0) return false;
		
		BadblockPlayer player = (BadblockPlayer) sender;
		PluginShootFlag plug = PluginShootFlag.getInstance();

		
		switch(args[0].toLowerCase()){
			case "mainspawn":
				plug.getConfiguration().spawn = new MapLocation(player.getLocation());
				plug.saveJsonConfig();
			break;
			case "spawnlocation":
				if(args.length < 2)
					return false;
				
				setLocation(false, args[1], null, "spawnLocation", player);
			break;
			default: return false;
		}
		
		player.sendTranslatedMessage("commands.shootflag.modified");
		
		return true;
	}
}
