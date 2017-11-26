package fr.badblock.bukkit.hub.v1.commands;

import org.bukkit.command.CommandSender;

import fr.badblock.bukkit.hub.v1.listeners.vipzone.RaceCell;
import fr.badblock.bukkit.hub.v1.listeners.vipzone.RaceListener;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;

public class RaceCommand extends AbstractCommand {

	public RaceCommand() {
		super("race", null, GamePermission.PLAYER, GamePermission.PLAYER, GamePermission.PLAYER);
		allowConsole(false);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args) {
		BadblockPlayer player = (BadblockPlayer) sender;
		if (!player.hasPermission("hub.vipzone"))
			return true;
		if (RaceListener.racePlayers.containsKey(player)) {
			player.sendTranslatedMessage("hub.race.already_in");
			return true;
		}
		for (RaceCell raceCell : RaceListener.raceEnterFences)
			if (!RaceListener.racePlayers.containsValue(raceCell)) {
				RaceListener.join(player, raceCell);
				return true;
			}
		player.sendTranslatedMessage("hub.race.full");
		return true;
	}
}
