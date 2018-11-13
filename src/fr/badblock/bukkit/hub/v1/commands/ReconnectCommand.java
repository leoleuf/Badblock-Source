package fr.badblock.bukkit.hub.v1.commands;

import org.bukkit.command.CommandSender;

import fr.badblock.bukkit.hub.v1.rabbitmq.listeners.ReconnectionInvitationsListener;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;

public class ReconnectCommand extends AbstractCommand {

	public ReconnectCommand() {
		super("reconnect", null, GamePermission.PLAYER, GamePermission.PLAYER, GamePermission.PLAYER);
		allowConsole(false);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args) {
		BadblockPlayer player = (BadblockPlayer) sender;
		
		if (!ReconnectionInvitationsListener.getReconnections().containsKey(player.getName().toLowerCase()))
		{
			player.sendTranslatedMessage("hub.reconnectserver.noserver");
			return true;
		}
		
		String serverName = ReconnectionInvitationsListener.getReconnections().get(player.getName().toLowerCase());
		
		player.sendTranslatedMessage("hub.reconnectserver.reconnect", serverName);
		player.sendPlayer(serverName);
		
		return true;
	}
}
