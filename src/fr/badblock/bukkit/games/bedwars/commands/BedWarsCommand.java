package fr.badblock.bukkit.games.bedwars.commands;

import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.gameapi.command.MapAbstractCommand;
import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import java.io.File;

public class BedWarsCommand extends MapAbstractCommand {
	public BedWarsCommand(File folder) {
		super("bedwars", new TranslatableString("commands.bedwars.usage"), GamePermission.ADMIN, new String[]{}, folder);
		allowConsole(false);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args) {
		if(args.length == 0) return false;
		BadblockPlayer player = (BadblockPlayer) sender;
		// commande : /bedwars [type] [map] (team)
		PluginBedWars plug = PluginBedWars.getInstance();
		switch(args[0].toLowerCase()){
			case "mainspawn":
				plug.getConfiguration().spawn = new MapLocation(player.getLocation());
				plug.saveJsonConfig();
			break;
			case "addsheep":
				plug.getConfiguration().sheeps.add(new MapLocation(player.getLocation()));
				plug.saveJsonConfig();
			break;
			case "mapbounds":
				if(args.length < 2) return false;
				setSelection(false, args[1], null, "mapBounds", player);
			break;
			case "spawnlocation":
				if(args.length < 2) return false;
				setLocation(false, args[1], null, "spawnLocation", player);
			break;
			case "bed":
				if(args.length < 3) return false;
				setLookedBlock(false, args[1], args[2], "bed", player, Material.BED_BLOCK);
			break;
			case "itemspawnlocations":
				if(args.length < 3) return false;
				setLocation(true, args[1], args[2], "itemSpawnLocations", player);
			break;
			case "spawnselection":
				if(args.length < 3) return false;
				setSelection(false, args[1], args[2], "spawnSelection", player);
			break;
			case "respawnlocation":
				if(args.length < 3) return false;
				setLocation(false, args[1], args[2], "respawnLocation", player);
			break;
			default: return false;
		}
		
		player.sendTranslatedMessage("commands.bedwars.modified");
		
		return true;
	}
}
