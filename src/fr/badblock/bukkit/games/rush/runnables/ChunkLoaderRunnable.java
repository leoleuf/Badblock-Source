package fr.badblock.bukkit.games.rush.runnables;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import org.bukkit.Chunk;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Queues;

public class ChunkLoaderRunnable extends BukkitRunnable
{

	public static Set<Chunk> loadedChunks = new HashSet<>();

	public static Map<Chunk, Long> toReload = new HashMap<>();
	public static Queue<Chunk> toLoad = Queues.newLinkedBlockingDeque();

	@Override
	public void run()
	{
		if (StartRunnable.gameTask != null)
		{
			cancel();
			return;
		}

		if (toLoad.isEmpty())
		{
			int i = 0;
			Map<Chunk, Long> toEdit = new HashMap<>();
			for (Entry<Chunk, Long> entry : toReload.entrySet())
			{
				if (entry.getValue() > System.currentTimeMillis())
				{
					continue;
				}
				
				if (i >= 25)
				{
					break;
				}

				Chunk chunk = entry.getKey();
				chunk.load(false);
				i++;
				
				toEdit.put(chunk, System.currentTimeMillis() + 9000L + new Random().nextInt(1000));
			}
			
			for (Entry<Chunk, Long> entry : toEdit.entrySet())
			{
				toReload.put(entry.getKey(), entry.getValue());
			}
		}
		else
		{
			int i = 0;
			while (!toLoad.isEmpty())
			{
				if (i >= 25)
				{
					break;
				}
				i++;

				Chunk chunk = toLoad.poll();

				chunk.load(false);
				toReload.put(chunk, System.currentTimeMillis() + 10_000L);
			}
		}
	}

	public static void put(Chunk chunk)
	{
		assert chunk != null;

		if (toLoad.contains(chunk))
		{
			return;
		}

		toLoad.add(chunk);
	}

	public static void put(Set<Chunk> chunks)
	{
		assert chunks != null;

		chunks.forEach(chunk -> put(chunk));
	}

}
