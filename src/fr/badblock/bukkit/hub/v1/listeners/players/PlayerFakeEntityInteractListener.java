package fr.badblock.bukkit.hub.v1.listeners.players;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;

import com.google.gson.Gson;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.inventories.LinkedInventoryEntity;
import fr.badblock.bukkit.hub.v1.inventories.ServerJoinerVillager;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.inventories.BedWarsChooserInventory;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.inventories.SkyBlockChooserInventory;
import fr.badblock.bukkit.hub.v1.listeners._HubListener;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.bukkit.hub.v1.utils.pnj.NPCData;
import fr.badblock.gameapi.events.PlayerFakeEntityInteractEvent;
import fr.badblock.gameapi.fakeentities.FakeEntity;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.general.Flags;
import fr.badblock.rabbitconnector.RabbitPacketType;
import fr.badblock.rabbitconnector.RabbitService;
import fr.badblock.sentry.SEntry;
import fr.badblock.utils.Encodage;

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
		Map<Location, ServerJoinerVillager> data = LinkedInventoryEntity.getData();
		if (!data.containsKey(location))
			return;
		ServerJoinerVillager serverJoinerVillager = data.get(location);
		if (serverJoinerVillager == null)
		{
			return;
		}

		if (Flags.isValid(player, "fakeEntity"))
		{
			return;
		}
		
		Flags.setTemporaryFlag(player, "fakeEntity", 150);
		
		if (serverJoinerVillager.getServerName() != null && !serverJoinerVillager.getServerName().isEmpty())
		{
			if (serverJoinerVillager.getServerName().equalsIgnoreCase("skyb"))
			{
				CustomInventory.get(SkyBlockChooserInventory.class).open(player);
				return;
			}
			player.sendMessage("§aTéléportation (" + serverJoinerVillager.getServerName() + ")...");
			player.sendPlayer(serverJoinerVillager.getServerName());
		}
		else
		{
			if (serverJoinerVillager.getQueueName().equalsIgnoreCase("bedwars"))
			{
				CustomInventory.get(BedWarsChooserInventory.class).open(player);
				return;
			}
			player.sendMessage("§aTéléportation en jeu (" + serverJoinerVillager.getQueueName() + ")...");
			BadBlockHub instance = BadBlockHub.getInstance();
			RabbitService service = instance.getRabbitService();
			Gson gson = instance.getGson();
			service.sendAsyncPacket("networkdocker.sentry.join", gson.toJson(new SEntry(HubPlayer.getRealName(player), serverJoinerVillager.getQueueName(), false)),
					Encodage.UTF8, RabbitPacketType.PUBLISHER, 5000, false);
		}
	}

}
