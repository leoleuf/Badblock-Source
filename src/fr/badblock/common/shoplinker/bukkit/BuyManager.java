package fr.badblock.common.shoplinker.bukkit;

import java.util.Map.Entry;
import java.util.UUID;

import org.bson.types.ObjectId;
import org.bukkit.entity.Player;

import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import fr.badblock.common.shoplinker.api.objects.TempBuyObject;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.CustomItemAction;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryItemObject;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryShopObject;
import fr.badblock.common.shoplinker.bukkit.utils.Callback;
import fr.badblock.common.shoplinker.bukkit.utils.Flags;
import fr.badblock.common.shoplinker.bukkit.utils.NetworkUtils;
import fr.badblock.common.shoplinker.mongodb.MongoService;

public class BuyManager {

	public static void buy(Player player, TempBuyObject buy) {
		buy(player, buy.getAction(), buy.getShopObject(), buy.getInventoryItemObject());
	}

	public static void buy(Player player, CustomItemAction action, InventoryShopObject shopObject, InventoryItemObject inventoryItemObject) {
		if (Flags.isValid(player, "buy"))
		{
			player.sendRawMessage(ShopLinker.getInstance().getPleaseWaitMessage());
			return;
		}
		Flags.setTemporaryFlag(player, "buy", 1500);
		player.sendMessage(ShopLinker.getInstance().getWebsiteConnectionMessage());

		String offerId = shopObject.getOfferId();

		new Thread()
		{
			@Override
			public void run()
			{
				try
				{
					Entry<Integer, String> entry = NetworkUtils.getURLSource("https://badblock.fr/shop/achat/" + offerId, player.getName());
					
					if (entry == null)
					{
						player.sendMessage("§cUn problème est survenu lors de votre achat. Code d'erreur #1");
						return;
					}

					if (entry.getKey() == 500)
					{
						player.sendMessage("§cUn problème est survenu lors de votre achat. Code d'erreur #2-" + offerId);
						return;
					}

					if (entry.getKey() == 401)
					{
						player.sendMessage("§cUn problème est survenu lors de votre achat. Code d'erreur #3-401-" + offerId);
						return;
					}

					if (entry.getKey() > 199 && entry.getKey() < 300)
					{
						if (entry.getValue() != null && entry.getValue().equalsIgnoreCase("Lolnope."))
						{
							player.sendMessage("§cProduit inexistant. Code d'erreur #6-403-" + offerId + "-WHITELIST");
							return;
						}
						player.sendMessage("§aVous avez obtenu cette offre.");
						return;
					}

					if (entry.getKey() == 403)
					{
						player.sendMessage("§cUn problème est survenu lors de votre achat. Code d'erreur #4-403-" + offerId + "-" + player.getName());
						return;
					}

					if (entry.getKey() == 404)
					{
						player.sendMessage("§cProduit inexistant. Code d'erreur #5-404-" + offerId);
						return;
					}

					if (entry.getKey() == 400)
					{
						player.sendMessage("§c" + entry.getValue());
						return;
					}

					if (entry.getKey() == 405)
					{
						player.sendMessage("§cTu n'as pas assez de points boutique pour obtenir ça.");
						player.sendMessage("§eFais /code pour avoir + de points boutique.");
						return;
					}
				}
				catch (Exception error)
				{
					error.printStackTrace();
					String randomId = UUID.randomUUID().toString().replace("-", "");
					System.out.println("[ShopLinker] Please see the error below. ID " + randomId);
					player.sendMessage("§cUne erreur est survenue lors de l'achat. Code d'erreur #5[" + randomId + "/" + player.getName() + "]");
				}
			}
		}.start();
	}

	public static double getNeededCoins(String offerId)
	{
		ShopLinker shopLinker = ShopLinker.getInstance();

		MongoService mongoService = shopLinker.getMongoService();
		DB db = mongoService.getDb();
		DBCollection collection = db.getCollection("product_list");

		BasicDBObject query = new BasicDBObject();
		query.put("_id", new ObjectId(offerId));
		Cursor cursor = collection.find(query);

		if (!cursor.hasNext())
		{
			return -1;
		}

		DBObject obj = cursor.next();

		String proc = (String) obj.get("price");
		
		return Integer.parseInt(proc);
	}

	public static void getAsyncNeededCoins(String offerId, Callback<Double> callback)
	{
		new Thread()
		{
			@Override
			public void run()
			{
				callback.done(getNeededCoins(offerId), null);
			}
		}.start();
	}

}
