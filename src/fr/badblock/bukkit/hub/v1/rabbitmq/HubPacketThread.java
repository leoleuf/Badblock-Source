package fr.badblock.bukkit.hub.v1.rabbitmq;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.rabbitmq.factories.HubAliveFactory;
import fr.badblock.game.core18R3.players.ingamedata.CommandInGameData;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.threading.TaskManager;
import fr.badblock.permissions.PermissionManager;
import fr.badblock.rabbitconnector.RabbitPacketType;
import fr.badblock.rabbitconnector.RabbitService;
import fr.badblock.utils.Encodage;

public class HubPacketThread implements Runnable {

	public static boolean opened = true;
	private RabbitService rabbitService;
	public static int hubId = 0;

	public HubPacketThread(RabbitService rabbitService) {
		this.rabbitService = rabbitService;
		TaskManager.scheduleSyncRepeatingTask("hubPacketThread", this, 0, 20 * 30);
	}

	@Override
	public void run() {
		sendPacket();
	}

	private static int id = 0;

	@SuppressWarnings("deprecation")
	public void sendPacket() {
		Map<String, Integer> ranks = new HashMap<>();
		id = 0;
		Map<String, String> order = new HashMap<>();
		PermissionManager.getInstance().getGroups().stream().sorted((a, b) -> { return Integer.compare(b.getPower(), a.getPower()); }).forEach(group -> {
			String d = generateForId(id) + "";
			order.put(group.getName(), d);
			id++;
		});
		for (Player player : Bukkit.getOnlinePlayers()) {
			BadblockPlayer bPlayer = (BadblockPlayer) player;
			if (!bPlayer.isVisible()) continue;
			if (bPlayer.inGameData(CommandInGameData.class).vanish) continue;
			if (!ranks.containsKey(order.get(bPlayer.getMainGroup())))
				ranks.put(order.get(bPlayer.getMainGroup()), 1);
			else
				ranks.put(order.get(bPlayer.getMainGroup()), ranks.get(order.get(bPlayer.getMainGroup())) + 1);
		}
		// Cached SEntry players (in queue)
		/*for (NPC npc : SEntryInfosListener.tempNPCs.values()) {
			if (!ranks.containsKey(order.get(npc.rank)))
				ranks.put(order.get(npc.rank), 1);
			else
				ranks.put(order.get(npc.rank), ranks.get(order.get(npc.rank)) + 1);
		}*/
		int playersWorker = Bukkit.getOnlinePlayers().size();
		HubAliveFactory hubAliveFactory = new HubAliveFactory(Bukkit.getServerName(), playersWorker, Bukkit.getMaxPlayers(), opened, ranks);
		rabbitService.sendAsyncPacket("hub",
				BadBlockHub.getInstance().getGson()
				.toJson(hubAliveFactory),
				Encodage.UTF8, RabbitPacketType.PUBLISHER, 5000, false);
		hubId = hubAliveFactory.getId();
	}

	private char generateForId(int id){
		int A = 'A';

		if(id > 26){
			A   = 'a';
			id -= 26;

			return (char) (A + id);
		} else {
			return (char) (A + id);
		}
	}

}
