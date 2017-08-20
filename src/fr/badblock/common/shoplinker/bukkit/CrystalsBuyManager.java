package fr.badblock.common.shoplinker.bukkit;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang3.StringEscapeUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.badblock.common.shoplinker.api.ShopLinkerAPI;
import fr.badblock.common.shoplinker.api.objects.ShopType;
import fr.badblock.common.shoplinker.bukkit.database.BadblockDatabase;
import fr.badblock.common.shoplinker.bukkit.database.Request;
import fr.badblock.common.shoplinker.bukkit.database.Request.RequestType;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.CustomItemAction;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryItemObject;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryShopObject;
import fr.badblock.common.shoplinker.bukkit.inventories.utils.ChatColorUtils;

public class CrystalsBuyManager {

	private static ShopLinkerAPI	 linkerAPI		   = new ShopLinkerAPI(ShopLinker.getInstance().getRabbitService());
	private static SimpleDateFormat  simpleDateFormat  = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void buy(Player player, CustomItemAction action, InventoryShopObject shopObject, InventoryItemObject inventoryItemObject) {
		player.sendMessage(ShopLinker.getInstance().getWebsiteConnectionMessage());
		BadblockDatabase.getInstance().addSyncRequest(new Request("SELECT id, ptsboutique FROM joueurs WHERE pseudo = '" + player.getName() + "'", RequestType.GETTER) {
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

	private static void searchOffer(Player player, InventoryShopObject shopObject, InventoryItemObject inventoryItemObject, ResultSet resultSet) throws Exception {
		player.sendMessage(ShopLinker.getInstance().getSearchOfferMessage());
		int playerId = resultSet.getInt("id");
		int shopPoints = resultSet.getInt("ptsboutique");
		String displayName = StringEscapeUtils.escapeHtml4(ChatColor.stripColor(inventoryItemObject.getName()));
		BadblockDatabase.getInstance().addSyncRequest(new Request("SELECT id FROM boutique_offers WHERE displayname = '" + displayName + "'", RequestType.GETTER) {
			@Override
			public void done(ResultSet resultSet) {
				try {
					int neededCoins = resultSet.getInt("price");
					shopObject.setNeededCoins(neededCoins);
					int depends = resultSet.getInt("needed_offer");
					int[] dependsI = shopObject.getDepends(); 
					if (depends != -1) dependsI = new int[] { depends };
					shopObject.setDepends(dependsI);
					checkDepends(player, shopObject, displayName, resultSet, playerId, shopPoints);
				}catch(Exception error) {
					ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to buy.");
					error.printStackTrace();
					player.sendMessage(ShopLinker.getInstance().getErrorMessage());
				}
			}
		});
	}

	private static void checkDepends(Player player, InventoryShopObject shopObject, String displayName, ResultSet resultSet, int playerId, int shopPoints) throws Exception {
		if (resultSet.next()) {
			player.sendMessage(ShopLinker.getInstance().getCheckTransactionMessage());
			int offerId = resultSet.getInt("id");
			BadblockDatabase.getInstance().addSyncRequest(new Request("SELECT COUNT(id) AS count FROM boutique_buy WHERE offer = '" + offerId + "' AND player = '" + playerId + "'", RequestType.GETTER) {
				@Override
				public void done(ResultSet resultSet) {
					try {
						if (resultSet.next()) {
							int count = resultSet.getInt("count");
							if (count > 0 && !shopObject.isMultibuy()) {
								player.sendMessage(ShopLinker.getInstance().getAlreadyBoughtMessage());
							}else{
								checkTransaction(player, shopObject, displayName, resultSet, playerId, shopPoints, offerId);
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
		}else {
			player.sendMessage(ShopLinker.getInstance().getUnknownOfferNameMessage());
			ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to buy.");
			ShopLinker.getConsole().sendMessage(ChatColor.RED + "Error: ID 2 / No resultSet/next();");
		}
	}

	private static void checkTransaction(Player player, InventoryShopObject shopObject, String displayName, ResultSet resultSet, int playerId, int shopPoints, int offerId) throws Exception {
		BadblockDatabase.getInstance().addSyncRequest(new Request("SELECT COUNT(id) AS count FROM boutique_buy WHERE offer = '" + offerId + "' AND player = '" + playerId + "'", RequestType.GETTER) {
			@Override
			public void done(ResultSet resultSet) {
				try {
					if (resultSet.next()) {
						int count = resultSet.getInt("count");
						if (count > 0 && !shopObject.isMultibuy()) {
							player.sendMessage(ShopLinker.getInstance().getAlreadyBoughtMessage());
						}else{
							if (shopObject.getNeededCoins() < shopPoints) {
								if (shopObject.getDepends() != null && shopObject.getDepends().length == 0) {
									buy(player, shopObject, displayName, resultSet, playerId, shopPoints, offerId);
								}else{
									String sqlBuilder = "";
									boolean first = true;
									for (int depend : shopObject.getDepends()) {
										if (!first) sqlBuilder += " OR ";
										else first = false;
										sqlBuilder += "id = '" + depend + "'";
									}
									checkOfferDepends(player, shopObject, displayName, resultSet, playerId, shopPoints, offerId, sqlBuilder);
								}
							}else {
								int diff = shopObject.getNeededCoins() - shopPoints;
								player.sendMessage(ShopLinker.getInstance().getNotEnoughCoinsMessage().replace("%0", Integer.toString(diff)).replace("%1", Integer.toString(shopObject.getNeededCoins())));
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

	private static void checkOfferDepends(Player player, InventoryShopObject shopObject, String displayName, ResultSet resultSet, int playerId, int shopPoints, int offerId, String sqlBuilder) throws Exception {
		BadblockDatabase.getInstance().addSyncRequest(new Request("SELECT COUNT(id) AS count FROM boutique_offers WHERE " + sqlBuilder, RequestType.GETTER) {
			@Override
			public void done(ResultSet resultSet) {
				try {
					if (resultSet.next()) {
						int count = resultSet.getInt("count");
						if (count < shopObject.getDepends().length) {
							int length = shopObject.getDepends().length;
							int diff  = length - count;
							int index = length - 1;
							index -= diff;
							if (index >= 0 && index <= length - 1) {
								int offer = shopObject.getDepends()[index];
								dependsError(player, offer);
							}else {
								player.sendMessage(ShopLinker.getInstance().getUnknownOfferNameMessage());
								ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to buy.");
								ShopLinker.getConsole().sendMessage(ChatColor.RED + "Error: Depends Error (OfferId: " + offerId + " / Depends: " + length + " / Diff: " + diff + " / Index: " + index + ")");
							}
						}else {
							buy(player, shopObject, displayName, resultSet, playerId, shopPoints, offerId);
						}
					}else {
						ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to buy.");
					}
				}catch(Exception error) {
					ShopLinker.getConsole().sendMessage(ChatColor.RED + "An error occurred while trying to buy.");
					error.printStackTrace();
					player.sendMessage(ShopLinker.getInstance().getErrorMessage());
				}
			}
		});
	}

	private static void dependsError(Player player, int offerId) throws Exception {
		BadblockDatabase.getInstance().addSyncRequest(new Request("SELECT displayname FROM boutique_offers WHERE id = '" + offerId + "'", RequestType.GETTER) {
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
	
	private static void buy(Player player, InventoryShopObject shopObject, String displayName, ResultSet resultSet, int playerId, int shopPoints, int offerId) throws Exception {
		BadblockDatabase.getInstance().addSyncRequest(new Request("UPDATE joueurs SET ptsboutique=ptsboutique-" + shopObject.getNeededCoins() + " WHERE pseudo = '" + player.getName() + "'", RequestType.SETTER));
		String date = simpleDateFormat.format(new Date());
		BadblockDatabase.getInstance().addSyncRequest(new Request("INSERT INTO boutique_buy(offer, player, price, day) VALUES('" + offerId + "', '" + playerId + "', '" + shopObject.getNeededCoins() + "', '" + date + "')", RequestType.SETTER));
		linkerAPI.sendShopData(ShopType.BUY, shopObject.getQueueName(), player.getName(), shopObject.getAction(), displayName, shopObject.getDepends(), shopObject.isMultibuy());
		player.sendMessage(ShopLinker.getInstance().getYouBoughtMessage().replace("%0", displayName));
		if (shopObject.getMessage() != null && !shopObject.getMessage().isEmpty()) {
			player.sendMessage(ChatColorUtils.translate(shopObject.getMessage()));
		}
	}
	
}
