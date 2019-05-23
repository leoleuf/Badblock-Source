package fr.badblock.bukkit.games.uhc.meetup.runnables;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Queues;

import fr.badblock.bukkit.games.uhc.meetup.PluginUHC;
import fr.badblock.bukkit.games.uhc.meetup.configuration.UHCConfiguration;
import fr.badblock.bukkit.games.uhc.meetup.players.UHCScoreboard;
import fr.badblock.bukkit.games.uhc.meetup.runnables.game.GameRunnable;
import fr.badblock.bukkit.games.uhc.meetup.runnables.game.UHCTimeManager;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;

public class TeleportRunnable extends BukkitRunnable {

	public static boolean teleporting;
	private Queue<Queue<Entry<BadblockPlayer, Location>>> toTeleport = Queues.newLinkedBlockingDeque();
	public static List<Location> teleportLocations = new ArrayList<>();

	public static List<Block> blocksToRemove = new ArrayList<>();

	public static Map<BadblockPlayer, Location> freezeLoc = new HashMap<>();

	private Queue<Entry<BadblockPlayer, Location>> currentTeleportQueue = null;

	@SuppressWarnings("deprecation")
	public TeleportRunnable()
	{
		teleporting = true;

		GameAPI.getAPI().getGameServer().setGameState(GameState.RUNNING);
		GameAPI.getAPI().getGameServer().saveTeamsAndPlayersForResult();

		UHCScoreboard.setTimeProvider(new UHCTimeManager());

		Bukkit.getWorlds().forEach(world -> {
			world.getEntities().forEach(entity -> {
				if(entity.getType() != EntityType.PLAYER)
					entity.remove();
			});
		});

		for(BadblockPlayer p : GameAPI.getAPI().getOnlinePlayers()){
			p.clearInventory();
		}

		GameAPI.getAPI().getJoinItems().doClearInventory(false);
		GameAPI.getAPI().getJoinItems().end();

		UHCConfiguration config = PluginUHC.getInstance().getConfiguration();

		int i = 0;

		if (!config.allowTeams)
		{
			for (BadblockPlayer player : BukkitUtils.getPlayers())
			{
				Location location = teleportLocations.get(i);

				int o = new Random().nextInt(14);
				int radius = 1;
				for (int x = -radius; x < radius; x++)
					for (int z = -radius; z < radius; z++)
					{
						Block block = location.clone().add(x, 0, z).getBlock().getRelative(BlockFace.DOWN);
						block.setTypeIdAndData(Material.STAINED_GLASS.getId(), (byte) o, false);
						blocksToRemove.add(block);
					}

				Queue<Entry<BadblockPlayer, Location>> queue = Queues.newConcurrentLinkedQueue();
				queue.add(new SimpleEntry<>(player, location.clone().add(0, 2, 0)));

				toTeleport.add(queue);
				i++;
			}
		}
	}

	@Override
	public void run()
	{
		if (toTeleport.isEmpty())
		{
			teleporting = false;
			PluginUHC.getInstance().removeSpawn();

			if(PluginUHC.getInstance().getConfiguration().allowTeams)
				new FriendsActionBarRunnable().runTaskTimer(GameAPI.getAPI(), 0, 10L);

			StartRunnable.gameTask = new GameRunnable();
			StartRunnable.gameTask.runTaskTimer(GameAPI.getAPI(), 0, 20L);
			cancel();
			return;
		}

		if (currentTeleportQueue != null)
		{
			if (!currentTeleportQueue.isEmpty())
			{
				Entry<BadblockPlayer, Location> entry = currentTeleportQueue.poll();
				BadblockPlayer player = entry.getKey();

				if (player.isOnline())
				{
					player.teleport(entry.getValue());
					player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 13, 5));
					freezeLoc.put(player, entry.getValue().clone().add(0, -2, 0));
				}

				if (currentTeleportQueue.isEmpty())
				{
					currentTeleportQueue = null;
				}
			}
			return;
		}

		Queue<Entry<BadblockPlayer, Location>> queue = toTeleport.poll();

		if (queue.size() > 1)
		{
			currentTeleportQueue = queue;
		}
		else
		{
			currentTeleportQueue = null;
		}

		if (!queue.isEmpty())
		{
			Entry<BadblockPlayer, Location> entry = queue.poll();
			BadblockPlayer player = entry.getKey();
			Location location = entry.getValue();

			if (player.isOnline())
			{
				player.teleport(location);
				player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 13, 5));
				freezeLoc.put(player, entry.getValue().clone().add(0, -2, 0));
			}
		}
	}

	public static void ensureLocations()
	{
		int neededLocations = PluginUHC.getInstance().getConfiguration().allowTeams ? GameAPI.getAPI().getTeams().size() : Bukkit.getMaxPlayers();
		if (teleportLocations.size() > neededLocations)
		{
			return;
		}

		Location found = findTeleportableLocation(PluginUHC.getInstance().getConfiguration().map.overworldSize);

		teleportLocations.add(found);
	}

	public static Location findTeleportableLocation(int size)
	{
		Random random = new Random();

		int dsize = size * 2;

		boolean good = false;
		Location loc = null;

		while(!good){
			loc = new Location(Bukkit.getWorlds().get(0), random.nextInt(dsize) - size, 256, random.nextInt(dsize) - size);
			good = true;
			for(Location u : teleportLocations){
				if(u.distance(loc) < 15.0d){
					good = false;
				}
			}
		}

		return loc;
	}

}
