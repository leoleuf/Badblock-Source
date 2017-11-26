package fr.badblock.bukkit.hub.v1.listeners.players;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;

import fr.badblock.bukkit.hub.v1.inventories.LinkedInventoryEntity;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.listeners._HubListener;
import fr.badblock.bukkit.hub.v1.utils.pnj.NPCData;
import fr.badblock.gameapi.events.PlayerFakeEntityInteractEvent;
import fr.badblock.gameapi.fakeentities.FakeEntity;
import fr.badblock.gameapi.players.BadblockPlayer;

public class PlayerFakeEntityInteractListener extends _HubListener {

	@EventHandler
	public void onPlayerFakeEntityInteract(PlayerFakeEntityInteractEvent event) {
		FakeEntity<?> entity = event.getEntity();
		Location location = entity.getLocation();
		BadblockPlayer player = event.getPlayer();
		// Vérification des NPC spéciaux
		for (NPCData npcData : NPCData.stockage.values()) {
			if (npcData.getFakeEntity() == null)
				continue;
			if (!npcData.getFakeEntity().equals(entity))
				continue;
			npcData.onClick(player);
			break;
		}
		/*if (BadBlockHub.getInstance().getBattleNpc().equals(entity)) {
			Battle.npcClick(player);
			return;
		}*/
		Map<Location, CustomInventory> data = LinkedInventoryEntity.getData();
		if (!data.containsKey(location))
			return;
		CustomInventory customInventory = data.get(location);
		CustomInventory.get(customInventory.getClass()).open(player);
	}

}
