package fr.badblock.common.shoplinker.workers;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import fr.badblock.common.shoplinker.bukkit.ShopLinker;
import fr.badblock.common.shoplinker.mongodb.MongoService;
import fr.badblock.common.shoplinker.workers.objects.GetPlayerShopPoints;
import fr.badblock.common.shoplinker.workers.objects.WorkerObject;
import lombok.Getter;

@Getter
public class ShopLinkerAsyncWorker extends Thread
{

	private Queue<WorkerObject>	queue = new ConcurrentLinkedQueue<>();

	public ShopLinkerAsyncWorker(int id)
	{
		super("ShopLinkerAsyncWorker-" + id);
		this.start();
	}

	@Override
	public void run()
	{
		while (true)
		{
			while (!queue.isEmpty())
			{
				WorkerObject workerObject = queue.poll();

				if (workerObject instanceof GetPlayerShopPoints)
				{
					GetPlayerShopPoints getter = (GetPlayerShopPoints) workerObject;

					ShopLinker shopLinker = ShopLinker.getInstance();
					MongoService mongoService = shopLinker.getMongoService();
					DB db = mongoService.getDb();
					DBCollection dbCollection = db.getCollection("fund_list");

					BasicDBObject dbObject = new BasicDBObject();
					dbObject.put("uniqueId", getter.getPlayer().toString());

					Cursor cursor = dbCollection.find(dbObject);
					
					if (cursor.hasNext())
					{
						DBObject obj = cursor.next();
						
						Object ob = obj.get("points");
						double funds = 0;
						
						if 	(ob instanceof Double)
						{
							funds = (double) ob;
						}
						else
						{
							funds = (int) ob;
						}
						
						getter.getCallback().done(funds, null);
						continue;
					}

					getter.getCallback().done(null, null);
				}
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
