package fr.badblock.bukkit.games.bedwars.listeners;

import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.events.PartyJoinEvent;
import org.bukkit.event.EventHandler;

/**
 * Ce listener consiste à balancer les joueurs dans une team quand ils arrivent en groupe
 * @author xMalware
 */
public class PartyJoinListener extends BadListener {

	@EventHandler
	public void onPartyJoin(PartyJoinEvent event) {
		if(inGame()) return;
		event.balancePlayersInTeam();
	}

}

