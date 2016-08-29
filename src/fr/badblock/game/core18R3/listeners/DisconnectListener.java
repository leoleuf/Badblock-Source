package fr.badblock.game.core18R3.listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.badblock.game.core18R3.GamePlugin;
import fr.badblock.game.core18R3.gameserver.GameServer;
import fr.badblock.game.core18R3.players.GameBadblockPlayer;
import fr.badblock.game.core18R3.players.ingamedata.GameBadblockPlayerData;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.events.api.PlayerReconnectionPropositionEvent;
import fr.badblock.gameapi.game.GameServer.WhileRunningConnectionTypes;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;

public class DisconnectListener extends BadListener {
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		BadblockPlayer player = (BadblockPlayer) e.getPlayer();

		player.undisguise();

		if (GameAPI.getAPI().getGameServer().getGameState() != GameState.RUNNING) {
			if (player.getTeam() != null)
				player.getTeam().leaveTeam(player);
		} else {
			GameServer server = GamePlugin.getInstance().getGameServer();

			if (server.isSaving() && server.getPlay().contains(player.getUniqueId())) {
				server.getSavedPlayers().add(new GameBadblockPlayerData((GameBadblockPlayer) e.getPlayer()));
			}

			boolean backup = server.getType() == WhileRunningConnectionTypes.BACKUP;

			if (backup) {
				PlayerReconnectionPropositionEvent event = new PlayerReconnectionPropositionEvent(player);
				Bukkit.getPluginManager().callEvent(event);
				backup = !event.isCancelled();
			}

			if (!backup) {
				if (player.getTeam() != null) {
					player.getTeam().leaveTeam(player);
				}

				return;
			}

			if (backup)
				server.remember(player);
		}
	}
}