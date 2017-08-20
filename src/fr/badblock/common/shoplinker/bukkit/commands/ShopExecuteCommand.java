package fr.badblock.common.shoplinker.bukkit.commands;

import java.sql.ResultSet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.badblock.common.shoplinker.api.ShopLinkerAPI;
import fr.badblock.common.shoplinker.api.objects.ShopData;
import fr.badblock.common.shoplinker.api.objects.ShopType;
import fr.badblock.common.shoplinker.bukkit.ShopLinkWorker;
import fr.badblock.common.shoplinker.bukkit.ShopLinker;
import fr.badblock.common.shoplinker.bukkit.database.BadblockDatabase;
import fr.badblock.common.shoplinker.bukkit.database.Request;
import fr.badblock.common.shoplinker.bukkit.database.Request.RequestType;
import net.md_5.bungee.api.ChatColor;

public class ShopExecuteCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "[ShopLinker] You must be a player to execute this.");
			return true;
		}
		Player player = (Player) sender;
		ShopLinker shopLinker = ShopLinker.getInstance();
		BadblockDatabase.getInstance().addRequest(new Request("SELECT * FROM cachedShop WHERE serverName = '" + ShopLinkerAPI.CURRENT_SERVER_NAME + "' AND playerName = '" + player.getName() + "'", RequestType.GETTER) {
			@Override
			public void done(ResultSet resultSet) {
				try {
					int count = 0;
					while (resultSet.next()) {
						count++;
						String type = resultSet.getString("type");
						String playerName = resultSet.getString("playerName");
						String objectName = resultSet.getString("objectName");
						String displayName = resultSet.getString("displayName");
						ShopLinkWorker.workCommand(new ShopData(ShopType.getFrom(type), playerName, displayName, objectName, new int[] {}, false), true);
					}
					String message = count > 1 ? shopLinker.getPluralClaimMessage().replace("%0", Integer.toString(count)) : shopLinker.getSingleClaimMessage();
					if (count > 0) {
						BadblockDatabase.getInstance().addRequest(new Request("DELETE FROM cachedShop WHERE serverName = '" + ShopLinkerAPI.CURRENT_SERVER_NAME + "' AND playerName = '" + player.getName() + "'", RequestType.SETTER));
						player.sendMessage(message);
					}else player.sendMessage(ShopLinker.getInstance().getNothingToClaimMessage());
				}catch(Exception error) {
					error.printStackTrace();
					ShopLinker shopLinker = ShopLinker.getInstance();
					player.sendMessage(shopLinker.getErrorMessage());
				}
			}
		});
		return true;
	}

}
