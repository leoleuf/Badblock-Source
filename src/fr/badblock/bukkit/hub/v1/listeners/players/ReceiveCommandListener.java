package fr.badblock.bukkit.hub.v1.listeners.players;

import fr.badblock.bukkit.hub.v1.listeners._HubListener;

public class ReceiveCommandListener extends _HubListener {

	/*@EventHandler
	public void onReceiveRemoteCommand(ReceivedRemoteCommandEvent event) {
		String playerName = event.getShopData().getPlayerName();
		Player player = Bukkit.getPlayer(playerName);
		if (player == null || !player.isOnline())
		{
			return;
		}
		BadblockPlayer bPlayer = (BadblockPlayer) player;
		bPlayer.refreshShopPoints();
	}*/

}
