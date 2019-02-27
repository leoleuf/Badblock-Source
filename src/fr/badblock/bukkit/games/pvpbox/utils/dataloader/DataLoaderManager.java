package fr.badblock.bukkit.games.pvpbox.utils.dataloader;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.badblock.bukkit.games.pvpbox.PvPBox;
import fr.badblock.bukkit.games.pvpbox.config.BoxConfig;
import fr.badblock.bukkit.games.pvpbox.players.BoxPlayer;
import fr.badblock.gameapi.GameAPI;
import lombok.Getter;

public class DataLoaderManager
{

	@Getter
	private static DataLoaderManager		instance = new DataLoaderManager();

	private List<DataLoaderThread>				workers;

	public DataLoaderManager()
	{
		PvPBox box = PvPBox.getInstance();
		BoxConfig boxConfig = box.getBoxConfig();

		workers = new ArrayList<>();

		for (int i = 0; i < boxConfig.getDataLoaderThreads(); i++)
		{
			workers.add(new DataLoaderThread(i));
		}
	}

	public void send(BoxPlayer boxPlayer)
	{
		Optional<DataLoaderThread> optional = workers.parallelStream().filter(thread -> thread.isAlive() && thread.getState().equals(State.WAITING)).findAny();

		DataLoaderThread worker = null;
		
		if (!optional.isPresent())
		{
			DataLoaderThread	lessOverload = null;
			
			for (DataLoaderThread dataLoaderThread : workers)
			{
				if (!dataLoaderThread.isAlive())
				{
					continue;
				}
				
				if (lessOverload == null || dataLoaderThread.getQueue().size() < lessOverload.getQueue().size())
				{
					lessOverload = dataLoaderThread;
				}
			}
			
			if (lessOverload == null)
			{
				GameAPI.logError("§c[PvPBox] Error. No available thread to load player data. Busy or interrupted?");
				return;
			}
			
			worker = lessOverload;
		}
		else
		{
			worker = optional.get();
		}
		
		if (worker == null)
		{
			GameAPI.logError("§c[PvPBox] Error. No available thread to load player data. Optional error?");
			return;
		}
	
		worker.getQueue().add(new DataLoaderFactory(boxPlayer));
		
		synchronized (worker)
		{
			worker.notify();
		}
	}

}
