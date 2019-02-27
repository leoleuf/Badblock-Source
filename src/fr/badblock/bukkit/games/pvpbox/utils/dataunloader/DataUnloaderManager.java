package fr.badblock.bukkit.games.pvpbox.utils.dataunloader;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.badblock.bukkit.games.pvpbox.PvPBox;
import fr.badblock.bukkit.games.pvpbox.config.BoxConfig;
import fr.badblock.bukkit.games.pvpbox.players.BoxPlayer;
import fr.badblock.gameapi.GameAPI;
import lombok.Getter;

public class DataUnloaderManager
{

	@Getter
	private static DataUnloaderManager		instance = new DataUnloaderManager();

	private List<DataUnloaderThread>				workers;

	public DataUnloaderManager()
	{
		PvPBox box = PvPBox.getInstance();
		BoxConfig boxConfig = box.getBoxConfig();

		workers = new ArrayList<>();

		for (int i = 0; i < boxConfig.getDataLoaderThreads(); i++)
		{
			workers.add(new DataUnloaderThread(i));
		}
	}

	public void send(BoxPlayer boxPlayer)
	{
		Optional<DataUnloaderThread> optional = workers.parallelStream().filter(thread -> thread.isAlive() && thread.getState().equals(State.WAITING)).findAny();

		DataUnloaderThread worker = null;
		
		if (!optional.isPresent())
		{
			DataUnloaderThread	lessOverload = null;
			
			for (DataUnloaderThread dataLoaderThread : workers)
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
	
		worker.getQueue().add(new DataUnloaderFactory(boxPlayer));
		
		synchronized (worker)
		{
			worker.notify();
		}
	}

}
