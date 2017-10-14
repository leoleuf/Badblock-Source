package fr.badblock.bukkit.hub.inventories.selector.animhost.items;

import fr.badblock.bukkit.hub.objects.HubPlayer;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;

public class AnimHostManager {

	private static final char 		SEPARATOR = ';';
	private static final boolean	STATE	  = false;
	
	public static void openServer(BadblockPlayer player, String serverName) {
		GameAPI.getAPI().getRabbitSpeaker().sendAsyncUTF8Publisher("networkdocker.request.open", HubPlayer.getRealName(player) + SEPARATOR + serverName + SEPARATOR + STATE, 5000, false);
	}
	
}
