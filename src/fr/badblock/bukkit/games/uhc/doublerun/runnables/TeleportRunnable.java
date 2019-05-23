package fr.badblock.bukkit.games.uhc.doublerun.runnables;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Queues;

import fr.badblock.bukkit.games.uhc.doublerun.PluginUHC;
import fr.badblock.bukkit.games.uhc.doublerun.configuration.UHCConfiguration;
import fr.badblock.bukkit.games.uhc.doublerun.players.UHCScoreboard;
import fr.badblock.bukkit.games.uhc.doublerun.runnables.game.ChunkLoaderRunnable;
import fr.badblock.bukkit.games.uhc.doublerun.runnables.game.GameRunnable;
import fr.badblock.bukkit.games.uhc.doublerun.runnables.game.UHCTimeManager;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.selections.CuboidSelection;
import net.minecraft.server.v1_8_R3.ChunkCoordIntPair;
import net.minecraft.server.v1_8_R3.EntityPlayer;

public class TeleportRunnable extends BukkitRunnable {

	public static boolean teleporting;
	private Queue<Queue<Entry<BadblockPlayer, Location>>> toTeleport = Queues.newLinkedBlockingDeque();
	public static List<Location> teleportLocations = new ArrayList<>();
	public static HashSet<Chunk> teleportChunks = new HashSet<>();

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
			p.getInventory().setItem(8, new ItemStack(Material.COOKED_BEEF, 16));
		}

		GameAPI.getAPI().getJoinItems().doClearInventory(false);
		GameAPI.getAPI().getJoinItems().end();

		UHCConfiguration config = PluginUHC.getInstance().getConfiguration();

		long start = System.currentTimeMillis();
		int i = 0;
		Map<BadblockPlayer, HashSet<Chunk>> chunksToSend = new HashMap<>();

		if (!config.allowTeams)
		{
			for (BadblockPlayer player : BukkitUtils.getPlayers())
			{
				Location location = teleportLocations.get(i);
				ChunkLoaderRunnable.put(location.getChunk());

				int radius = 4;
				for (int x = -radius; x < radius; x++)
					for (int y = -radius; y < radius; y++)
						for (int z = -radius; z < radius; z++)
						{
							Block block = location.clone().add(x, y, z).getBlock().getRelative(BlockFace.DOWN);
							block.setTypeIdAndData(Material.STAINED_GLASS.getId(), (byte) 0, false);
							blocksToRemove.add(block);
						}

				Queue<Entry<BadblockPlayer, Location>> queue = Queues.newConcurrentLinkedQueue();
				queue.add(new SimpleEntry<>(player, location.clone().add(0, 2, 0)));

				HashSet<Chunk> chunks = new HashSet<>();

				Location loc1 = location.clone().add(-50, 0, 50);
				Location loc2 = location.clone().add(50, 0, -50);
				CuboidSelection cuboidSelection = new CuboidSelection(loc1, loc2);
				for (Block block : cuboidSelection.getBlocks())
				{
					if (chunks.contains(block.getChunk()))
					{
						continue;
					}

					chunks.add(block.getChunk());
				}

				chunksToSend.put(player, chunks);

				toTeleport.add(queue);
				i++;
			}
		}
		else
		{
			for (BadblockTeam team : GameAPI.getAPI().getTeams())
			{
				if (team.getOnlinePlayers().isEmpty())
				{
					continue;
				}

				Queue<Entry<BadblockPlayer, Location>> queue = Queues.newConcurrentLinkedQueue();

				Location location = teleportLocations.get(i);

				int radius = 5;
				for (int x = -radius; x < radius; x++)
					for (int z = -radius; z < radius; z++)
					{
						Block block = location.clone().add(x, -1, z).getBlock();
						block.setTypeIdAndData(Material.STAINED_GLASS.getId(), team.getDyeColor().getWoolData(), false);
						blocksToRemove.add(block);
					}

				HashSet<Chunk> chunks = new HashSet<>();

				int mapSize = 100;
				for (int x = -(mapSize / 2); x < (mapSize / 2); x += 2)
					for (int z = -(mapSize / 2); z < (mapSize / 2); z += 2)
					{
						Location l = location.clone().add(x, 0, z);
						Chunk c = l.getChunk();
						if (!chunks.contains(c))
						{
							chunks.add(c);
						}
					}

				List<Location> able = new ArrayList<>();
				for (int x = -2; x < 2; x++)
					for (int z = -2; z < 2; z++)
					{
						Location l = location.clone().add(x, 2, z);
						boolean can = true;

						for (Location lo : able)
						{
							if (lo.distance(l) < 2)
							{
								can = false;
							}
						}

						if (can)
						{
							able.add(l);
						}
					}

				int index = 0;

				for (BadblockPlayer player : team.getOnlinePlayers())
				{
					chunksToSend.put(player, chunks);
					Location finalLocation = able.get(index);
					queue.add(new SimpleEntry<>(player, finalLocation));
					index++;
				}

				toTeleport.add(queue);
				i++;
			}
		}

		Bukkit.getScheduler().runTaskTimerAsynchronously(PluginUHC.getInstance(), new Runnable()
		{
			int ticks = 0;
			@Override
			public void run() {
				if (ticks >= 100)
				{
					return;
				}
				ticks++;

				for (BadblockPlayer player : BukkitUtils.getPlayers())
				{
					if (!chunksToSend.containsKey(player))
					{
						continue;
					}

					Iterator<Chunk> iterator = chunksToSend.get(player).iterator();

					for (int i = 0; i < 10; i++)
					{
						if (iterator.hasNext())
						{
							Chunk next = iterator.next();

							EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
							entityPlayer.chunkCoordIntPairQueue.add(new ChunkCoordIntPair(next.getX(), next.getZ()));

							iterator.remove();
						}
					}

				}
			}

		}, 1, 1);
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

		Set<Chunk> chunks = new HashSet<Chunk>();
		int radius = 80;
		for (int x = -radius; x < radius; x += 2)
			for (int z = -radius; z < radius; z += 2)
			{
				Location l = found.clone().add(x, 0, z);
				Chunk c = l.getChunk();
				if (!chunks.contains(c))
				{
					chunks.add(c);
				}
			}

		teleportChunks.addAll(chunks);

		Bukkit.getScheduler().runTaskAsynchronously(PluginUHC.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				ChunkLoaderRunnable.put(chunks);
			}
		});
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
				if(u.distance(loc) < 75.0d){
					good = false;
				}
			}
		}

		return loc;
	}

}
