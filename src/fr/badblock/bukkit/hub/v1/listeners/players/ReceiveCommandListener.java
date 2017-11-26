package fr.badblock.bukkit.hub.v1.listeners.players;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import fr.badblock.bukkit.hub.v1.listeners._HubListener;
import fr.badblock.common.shoplinker.bukkit.events.ReceivedRemoteCommandEvent;
import fr.badblock.gameapi.players.BadblockPlayer;

public class ReceiveCommandListener extends _HubListener {

	@EventHandler
	public void onReceiveRemoteCommand(ReceivedRemoteCommandEvent event) {
		String playerName = event.getShopData().getPlayerName();
		Player player = Bukkit.getPlayer(playerName);
		if (player == null || !player.isOnline())
		{
			return;
		}
		BadblockPlayer bPlayer = (BadblockPlayer) player;
		bPlayer.refreshShopPoints();
	}

}
