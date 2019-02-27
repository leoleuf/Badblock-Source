package fr.badblock.bukkit.games.pvpbox.utils.dataloader;

import java.util.Queue;

import com.google.common.collect.Queues;

import fr.badblock.bukkit.games.pvpbox.players.BoxPlayer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class DataLoaderThread extends Thread
{

	private Queue<DataLoaderFactory>	queue = Queues.newConcurrentLinkedQueue();
	
	public DataLoaderThread(int id)
	{
		super ("BoxDataLoaderThreead-" + id);
		this.start();
	}
	
	@Override
	public void run()
	{
			while (true)
			{
				while (!queue.isEmpty())
				{
					DataLoaderFactory dataFactory = queue.poll();
					BoxPlayer boxPlayer = dataFactory.getBoxPlayer();
					boxPlayer.fetch();
				}
				synchronized (this)
				{
					try
					{
						this.wait();
					}
					catch (InterruptedException exception)
					{
						exception.printStackTrace();
					}
				}
			}
	}
	
}
