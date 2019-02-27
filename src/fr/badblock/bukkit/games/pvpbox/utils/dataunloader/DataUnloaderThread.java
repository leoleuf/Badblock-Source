package fr.badblock.bukkit.games.pvpbox.utils.dataunloader;

import java.util.Queue;

import com.google.common.collect.Queues;

import fr.badblock.bukkit.games.pvpbox.players.BoxPlayer;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class DataUnloaderThread extends Thread
{

	private Queue<DataUnloaderFactory>	queue = Queues.newConcurrentLinkedQueue();
	
	public DataUnloaderThread(int id)
	{
		super ("BoxDataUnloaderThreead-" + id);
		this.start();
	}
	
	@Override
	public void run()
	{
			while (true)
			{
				while (!queue.isEmpty())
				{
					DataUnloaderFactory dataFactory = queue.poll();
					BoxPlayer boxPlayer = dataFactory.getBoxPlayer();
					boxPlayer.save();
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
