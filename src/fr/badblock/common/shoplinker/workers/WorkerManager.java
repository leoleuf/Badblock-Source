package fr.badblock.common.shoplinker.workers;

import java.lang.Thread.State;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import fr.badblock.common.shoplinker.workers.objects.WorkerObject;

public class WorkerManager
{

	private static List<ShopLinkerAsyncWorker>	workers;
	
	public static void load()
	{
		workers = new ArrayList<>();
		for (int i = 0; i < 2; i++)
		{
			workers.add(new ShopLinkerAsyncWorker(i));
		}
	}

	public static void send(WorkerObject object)
	{
		Optional<ShopLinkerAsyncWorker> optional = workers.parallelStream().filter(thread -> thread.isAlive() && thread.getState().equals(State.WAITING)).findAny();

		ShopLinkerAsyncWorker worker = null;
		
		if (!optional.isPresent())
		{
			ShopLinkerAsyncWorker	lessOverload = null;
			
			for (ShopLinkerAsyncWorker asyncWorker : workers)
			{
				if (!asyncWorker.isAlive())
				{
					continue;
				}
				
				if (lessOverload == null || asyncWorker.getQueue().size() < lessOverload.getQueue().size())
				{
					lessOverload = asyncWorker;
				}
			}
			
			if (lessOverload == null)
			{
				System.out.println("Error. No available thread to load player data. Busy or interrupted?");
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
			System.out.println("Error. No available thread to load player data. Busy or interrupted?");
			return;
		}
	
		worker.getQueue().add(object);
		
		synchronized (worker)
		{
			worker.notify();
		}
	}
	
}
