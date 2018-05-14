package fr.badblock.common.shoplinker.bukkit;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.badblock.common.shoplinker.api.ShopLinkerAPI;
import fr.badblock.common.shoplinker.api.objects.ObjectPrice;
import fr.badblock.common.shoplinker.api.objects.ShopType;
import fr.badblock.common.shoplinker.api.objects.TempBuyObject;
import fr.badblock.common.shoplinker.bukkit.database.BadblockDatabase;
import fr.badblock.common.shoplinker.bukkit.database.Request;
import fr.badblock.common.shoplinker.bukkit.database.Request.RequestType;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.CustomItemAction;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryItemObject;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryShopObject;
import fr.badblock.common.shoplinker.bukkit.utils.Callback;
import fr.badblock.common.shoplinker.bukkit.utils.Flags;

public class CrystalsBuyManager {

	private static ShopLinkerAPI	 linkerAPI		   = new ShopLinkerAPI(ShopLinker.getInstance().getRabbitService());
	private static SimpleDateFormat  simpleDateFormat  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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
		BadblockDatabase.getInstance().addRequest(new Request("SELECT id, ptsboutique FROM joueurs WHERE pseudo = '" + player.getName() + "'", RequestType.GETTER) {
			@Override
			public void done(ResultSet resultSet) {
				try {
					if (resultSet.next()) {
						searchOffer(player, shopObject, inventoryItemObject, resultSet);
					}else{
						player.sendMessage(ShopLinker.getInstance().getNotRegisteredMessage());
					}
				}catch(Exception error) {
					ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to buy.");
					error.printStackTrace();
					player.sendMessage(ShopLinker.getInstance().getErrorMessage());
				}
			}
		});
	}

	public static double getNeededCoins(int offerId)
	{
		ObjectPrice objectPrice = new ObjectPrice(-1);
		BadblockDatabase.getInstance().addSyncRequest(new Request("SELECT price FROM boutique_offers WHERE id = '" + offerId + "'", RequestType.GETTER) {
			@Override
			public void done(ResultSet resultSet) {
				try {
					if (resultSet.next())
					{
						objectPrice.setPrice(resultSet.getDouble("price"));
					}
				}catch(Exception error) {
					ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to get needed coins.");
					error.printStackTrace();
				}
			}
		});
		return objectPrice.getPrice();
	}

	public static void getAsyncNeededCoins(int offerId, Callback<Double> callback)
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

	public static void getCrystals(String playerName, Callback<Double> callback)
	{
		BadblockDatabase.getInstance().addRequest(new Request("SELECT ptsboutique FROM joueurs WHERE pseudo = '" + BadblockDatabase.getInstance().mysql_real_escape_string(playerName) + "'", RequestType.GETTER) {
			@Override
			public void done(ResultSet resultSet) {
				try {
					if (resultSet.next())
					{
						callback.done(resultSet.getDouble("ptsboutique"), null);
					}
					else
					{
						callback.done(-1D, null);
					}
				}catch(Exception error) {
					ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to get needed coins.");
					error.printStackTrace();
					callback.done(-1D, null);
				}
			}
		});
	}
	
	private static void searchOffer(Player player, InventoryShopObject shopObject, InventoryItemObject inventoryItemObject, ResultSet resultSet) throws Exception {
		player.sendMessage(ShopLinker.getInstance().getSearchOfferMessage());
		int playerId = resultSet.getInt("id");
		double shopPoints = resultSet.getDouble("ptsboutique");
		String displayName = StringEscapeUtils.escapeHtml4(ChatColor.stripColor(inventoryItemObject.getName()));
		BadblockDatabase.getInstance().addRequest(new Request("SELECT id, price, needed_offer, multyBuy, commande, server FROM boutique_offers WHERE id = '" + shopObject.getOfferId() + "'", RequestType.GETTER) {
			@Override
			public void done(ResultSet resultSet) {
				try {
					if (resultSet.next())
					{
						double neededCoins = resultSet.getDouble("price");
						int depends = resultSet.getInt("needed_offer");
						boolean multyBuy = resultSet.getInt("multyBuy") == 1 ? true : false;
						int[] dependsI = new int[] { }; 
						if (depends != -1) dependsI = new int[] { depends };
						String commands = resultSet.getString("commande");
						String server = resultSet.getString("server");
						checkDepends(player, shopObject, displayName, resultSet, playerId, shopPoints, neededCoins, dependsI, multyBuy, commands, server);
					}
					else
					{
						ShopLinker.getConsole().sendMessage(ChatColor.RED + "[ShopLinker] Unknown offerId: " + shopObject.getOfferId() + ".");
						player.sendMessage(ShopLinker.getInstance().getUnknownOfferNameMessage());
					}
				}catch(Exception error) {
					ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to buy.");
					error.printStackTrace();
					player.sendMessage(ShopLinker.getInstance().getErrorMessage());
				}
			}
		});
	}

	private static void checkDepends(Player player, InventoryShopObject shopObject, String displayName, ResultSet resultSet, int playerId, double shopPoints, double neededCoins, int[] depends, boolean multyBuy, String commands, String server) throws Exception {
		player.sendMessage(ShopLinker.getInstance().getCheckTransactionMessage());
		int offerId = resultSet.getInt("id");
		BadblockDatabase.getInstance().addRequest(new Request("SELECT COUNT(id) AS count FROM boutique_buy WHERE offer = '" + offerId + "' AND player = '" + playerId + "'", RequestType.GETTER) {
			@Override
			public void done(ResultSet resultSet) {
				try {
					if (resultSet.next()) {
						int count = resultSet.getInt("count");
						if (count > 0 && !multyBuy) {
							player.sendMessage(ShopLinker.getInstance().getAlreadyBoughtMessage());
						}else{
							checkTransaction(player, shopObject, displayName, resultSet, playerId, shopPoints, offerId, neededCoins, depends, multyBuy, commands, server);
						}
					}else{
						player.sendMessage(ShopLinker.getInstance().getErrorMessage());
						ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to buy.");
						ShopLinker.getConsole().sendMessage(ChatColor.RED + "Error: ID 1 / No resultSet/next();");
					}
				}catch(Exception error) {
					ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to buy.");
					error.printStackTrace();
					player.sendMessage(ShopLinker.getInstance().getErrorMessage());
				}
			}
		});
	}

	private static void checkTransaction(Player player, InventoryShopObject shopObject, String displayName, ResultSet resultSet, int playerId, double shopPoints, int offerId, double neededCoins, int[] depends, boolean multyBuy, String commands, String server) throws Exception {
		BadblockDatabase.getInstance().addRequest(new Request("SELECT COUNT(id) AS count FROM boutique_buy WHERE offer = '" + offerId + "' AND player = '" + playerId + "'", RequestType.GETTER) {
			@Override
			public void done(ResultSet resultSet) {
				try {
					if (resultSet.next()) {
						int count = resultSet.getInt("count");
						if (count > 0 && !multyBuy) {
							player.sendMessage(ShopLinker.getInstance().getAlreadyBoughtMessage());
						}else{
							if (neededCoins < shopPoints) {
								System.out.println(ShopLinker.getInstance().getNotRestrictiveGson().toJson(depends));
								if (depends != null && depends.length == 0) {
									buy(player, shopObject, displayName, resultSet, playerId, shopPoints, offerId, neededCoins, depends, multyBuy, commands, server);
								}else{
									String sqlBuilder = "";
									boolean first = true;
									for (int depend : depends) {
										if (!first) sqlBuilder += " OR ";
										else first = false;
										sqlBuilder += "offer = '" + depend + "'";
									}
									checkOfferDepends(player, shopObject, displayName, resultSet, playerId, shopPoints, offerId, sqlBuilder, neededCoins, depends, multyBuy, commands, server);
								}
							}else {
								double diff = neededCoins - shopPoints;
								player.sendMessage(ShopLinker.getInstance().getNotEnoughCoinsMessage().replace("%0", Double.toString(diff)).replace("%1", Double.toString(neededCoins)));
							}
						}
					}else {
						player.sendMessage(ShopLinker.getInstance().getErrorMessage());
						ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to buy.");
						ShopLinker.getConsole().sendMessage(ChatColor.RED + "Error: ID 2 / No resultSet/next();");
					}
				}catch(Exception error) {
					ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to buy.");
					error.printStackTrace();
					player.sendMessage(ShopLinker.getInstance().getErrorMessage());
				}
			}
		});
	}

	private static void checkOfferDepends(Player player, InventoryShopObject shopObject, String displayName, ResultSet resultSet, int playerId, double shopPoints, int offerId, String sqlBuilder, double neededCoins, int[] depends, boolean multyBuy, String commands, String server) throws Exception {
		BadblockDatabase.getInstance().addRequest(new Request("SELECT COUNT(id) AS count FROM boutique_buy WHERE player = '" + playerId + "' AND (" + sqlBuilder + ")", RequestType.GETTER) {
			@Override
			public void done(ResultSet resultSet) {
				try {
					if (resultSet.next())
					{
						int count = resultSet.getInt("count");
						if (count < depends.length)
						{
							int offer = depends[0];
							dependsError(player, offer);
						}
						else
						{
							buy(player, shopObject, displayName, resultSet, playerId, shopPoints, offerId, neededCoins, depends, multyBuy, commands, server);
						}
					}
					else 
					{
						ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to buy.");
					}
				}
				catch(Exception error)
				{
					ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to buy.");
					error.printStackTrace();
					player.sendMessage(ShopLinker.getInstance().getErrorMessage());
				}
			}
		});
	}

	private static void dependsError(Player player, int offerId) throws Exception {
		BadblockDatabase.getInstance().addRequest(new Request("SELECT displayname FROM boutique_offers WHERE id = '" + offerId + "'", RequestType.GETTER) {
			@Override
			public void done(ResultSet resultSet) {
				try {
					if (resultSet.next()) {
						String displayname = resultSet.getString("displayname");
						player.sendMessage(ShopLinker.getInstance().getDependNeededMessage().replace("%0", displayname));
					}else {
						player.sendMessage(ShopLinker.getInstance().getUnknownDependOfferNameMessage());
						ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to buy.");
						ShopLinker.getConsole().sendMessage(ChatColor.RED + "Error: Count error on dependsError (offerId: " + offerId + ").");
					}
				}catch(Exception error) {
					ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to buy.");
					error.printStackTrace();
					player.sendMessage(ShopLinker.getInstance().getErrorMessage());
				}
			}
		});
	}

	private static void buy(Player player, InventoryShopObject shopObject, String displayName, ResultSet resultSet, int playerId, double shopPoints, int offerId, double neededCoins, int[] depends, boolean multyBuy, String commands, String server) throws Exception {
		if (Flags.isValid(player, "current_buy"))
		{
			player.sendRawMessage(ShopLinker.getInstance().getPleaseWaitMessage());
			return;
		}
		Flags.setTemporaryFlag(player, "current_buy", 1100);
		BadblockDatabase.getInstance().addRequest(new Request("UPDATE joueurs SET ptsboutique=ptsboutique-" + neededCoins + " WHERE pseudo = '" + player.getName() + "'", RequestType.SETTER));
		String date = simpleDateFormat.format(new Date());
		BadblockDatabase.getInstance().addRequest(new Request("INSERT INTO boutique_buy(offer, player, price, day) VALUES('" + offerId + "', '" + playerId + "', '" + neededCoins + "', '" + date + "')", RequestType.SETTER));
		linkerAPI.sendShopData(ShopType.BUY, server, player.getName(), commands, displayName, depends, multyBuy, true, neededCoins);
		player.sendMessage(ShopLinker.getInstance().getYouBoughtMessage().replace("%0", displayName));
	}

}
