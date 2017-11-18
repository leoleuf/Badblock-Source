package fr.badblock.common.shoplinker.bukkit.listeners.bukkit;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import fr.badblock.common.shoplinker.api.ShopLinkerAPI;
import fr.badblock.common.shoplinker.bukkit.ShopLinker;
import fr.badblock.common.shoplinker.bukkit.database.BadblockDatabase;
import fr.badblock.common.shoplinker.bukkit.database.Request;
import fr.badblock.common.shoplinker.bukkit.database.Request.RequestType;
import fr.badblock.common.shoplinker.bukkit.inventories.utils.ChatColorUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class PlayerJoinListener implements Listener {

	@EventHandler
	public void onJoinEvent(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		BadblockDatabase.getInstance().addRequest(new Request("SELECT COUNT(command) AS count FROM cachedShop WHERE serverName = '" + ShopLinkerAPI.CURRENT_SERVER_NAME + "' AND playerName = '" + player.getName() + "'", RequestType.GETTER) {
			@Override
			public void done(ResultSet resultSet) {
				try {
					int count = 0;
					if (resultSet.next()) {
						count = resultSet.getInt("count");
						if (count == 0) return;

						ShopLinker shopLinker = ShopLinker.getInstance();

						Map<String, String> replace = new HashMap<>();
						replace.put("%0", Integer.toString(count));

						List<String> messageKey = count > 1 ? shopLinker.getPluralPendingMessage() : shopLinker.getSinglePendingMessage();
						String[] messageArray = messageKey.toArray(new String[messageKey.size()]);
						List<String> messages = ChatColorUtils.getTranslatedMessages(messageArray, replace);
						String hoverMessage = count > 1 ? shopLinker.getPluralHoverMessage().replace("%0", Integer.toString(count)) : shopLinker.getSingleHoverMessage();

						// Fix colors
						for (String message : messages)
						{
							TextComponent textComponent = new TextComponent();

							message = fixMessage(message);

							textComponent.setText(message);
							textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/sl shopexecute"));
							textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMessage).create()));
							player.sendMessage(textComponent);
						}
					}
				}catch(Exception error) {
					error.printStackTrace();
					ShopLinker shopLinker = ShopLinker.getInstance();
					player.sendMessage(shopLinker.getErrorMessage());
				}
			}
		});
	}

	private String fixMessage(String text)
	{
		int length = text.length();
		String coloredResult = "";
		String base = "";
		boolean wasColor = true;
		for (int index = 0; index < length; index++){
			char character = text.charAt(index);
			boolean b = false;
			if (character == '&' || character == '§') {
				wasColor = true;
				b = true;
			}
			base += character;
			if (!wasColor) coloredResult += getLastColors(base);
			coloredResult += character;
			if (!b)
				wasColor = false;
		}
		return coloredResult;
	}

	public static String getLastColors(String input)
	{
		String result = "";
		int length = input.length();
		for (int index = length - 1; index > -1; index--){
			char section = input.charAt(index);
			if ((section == '§' || section == '&') && (index < length - 1)){
				char c = input.charAt(index + 1);
				ChatColor color = ChatColor.getByChar(c);
				if (color != null) {
					result = color.toString() + result;
					if ((color.equals(ChatColor.RESET))){
						break;
					}
				}
			}
		}
		return result;
	}

}