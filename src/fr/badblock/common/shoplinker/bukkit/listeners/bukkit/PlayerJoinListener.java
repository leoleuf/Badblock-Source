package fr.badblock.common.shoplinker.bukkit.listeners.bukkit;

import java.sql.ResultSet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.badblock.common.shoplinker.api.ShopLinkerAPI;
import fr.badblock.common.shoplinker.bukkit.ShopLinker;
import fr.badblock.common.shoplinker.bukkit.database.BadblockDatabase;
import fr.badblock.common.shoplinker.bukkit.database.Request;
import fr.badblock.common.shoplinker.bukkit.database.Request.RequestType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerJoinListener implements Listener {

	@EventHandler
	public void onJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		BadblockDatabase.getInstance().addRequest(new Request("SELECT COUNT(objectName) AS count FROM cachedShop WHERE serverName = '" + ShopLinkerAPI.CURRENT_SERVER_NAME + "' AND playerName = '" + player.getName() + "'", RequestType.GETTER) {
			@Override
			public void done(ResultSet resultSet) {
				try {
					int count = 0;
					if (resultSet.next()) {
						count = resultSet.getInt("count");
						if (count == 0) return;
						ShopLinker shopLinker = ShopLinker.getInstance();
						TextComponent textComponent = new TextComponent();
						String message = count > 1 ? shopLinker.getPluralPendingMessage().replace("%0", Integer.toString(count)) : shopLinker.getSinglePendingMessage();
						String hoverMessage = count > 1 ? shopLinker.getPluralHoverMessage().replace("%0", Integer.toString(count)) : shopLinker.getSingleHoverMessage();
						textComponent.setText(message);
						textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/shopexecute"));
						textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
						player.sendMessage(textComponent);
					}
				}catch(Exception error) {
					error.printStackTrace();
					ShopLinker shopLinker = ShopLinker.getInstance();
					player.sendMessage(shopLinker.getErrorMessage());
				}
			}
		});
	}

}