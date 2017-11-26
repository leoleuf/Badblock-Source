package fr.badblock.bukkit.hub.v1.listeners.battle;

import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.listeners._HubListener;
import fr.badblock.bukkit.hub.v1.listeners.players.PlayerJoinListener;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.ConfigUtils;

public class BattleListener extends _HubListener {

	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {
		Entity entity = event.getEntity();
		if (Battle.entity.equals(entity)) {
			Battle.spawn(Battle.battleMobs.get(Battle.id));
			return;
		}
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		//BadblockPlayer player = (BadblockPlayer) event.getEntity(); 
		event.getDrops().clear();
		event.setDroppedExp(0);
		/*if (Battle.players.contains(player)) {
			Battle.players.parallelStream().filter(playerZ -> playerZ.isOnline() && playerZ.isValid() && BadBlockHub.getInstance().getBattleArena().isInSelection(playerZ)).forEach(playerZ -> playerZ.sendTranslatedMessage("hub.battle.killed", player.getName()));
			return;
		}*/
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		BadblockPlayer player = (BadblockPlayer) event.getPlayer();
		event.setRespawnLocation(ConfigUtils.getLocation(BadBlockHub.getInstance(), "worldspawn"));
		PlayerJoinListener.reload(player);
	}
	
}
