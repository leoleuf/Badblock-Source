package fr.badblock.game.core18R3.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import fr.badblock.game.core18R3.players.data.ChatData;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class ChatListener extends BadListener {
	public static boolean enabled = false;
	public static boolean team	  = false;
	public static String  custom  = null;
	public static Map<Integer, ChatData> messages = new HashMap<>();

	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
	public void onChat(AsyncPlayerChatEvent e){
		if(!enabled) return;

		e.setCancelled(true);

		BadblockPlayer player = (BadblockPlayer) e.getPlayer();

		protectColor(player, e);
		if(player.getBadblockMode() == BadblockMode.SPECTATOR){
			TranslatableString result = new TranslatableString("chat.spectator" + (custom == null ? "" : "." + custom), player.getName(), player.getGroupPrefix(), e.getMessage(), player.getPlayerData().getLevel(), player.getGroupSuffix());

			for(Player p : e.getRecipients()){
				BadblockPlayer bPlayer = (BadblockPlayer) p;
				if (player.hasPermission(GamePermission.MODERATOR)) result.send(p);
				else if(bPlayer.getBadblockMode() == BadblockMode.SPECTATOR) result.send(bPlayer);
			}
		} else if(team && player.getTeam() != null && e.getMessage().startsWith("$")){
			TranslatableString result = new TranslatableString("chat.team" + (custom == null ? "" : "." + custom), player.getName(), player.getGroupPrefix(), player.getTeam().getChatName(), e.getMessage().substring(1), player.getPlayerData().getLevel(), player.getGroupSuffix());

			for(BadblockPlayer p : player.getTeam().getOnlinePlayers()){
				if (e.getRecipients().contains(p))
					result.send(p);
			}
		} else {
			Object team = "";

			if(player.getTeam() != null)
				team = player.getTeam().getChatPrefix();

			int i = messages.size() + 1;
			messages.put(i, new ChatData(player.getName(), e.getMessage()));
			TranslatableString s = new TranslatableString("chat.player" + (custom == null ? "" : "." + custom), player.getName(), player.getGroupPrefix(), team, e.getMessage(), player.getPlayerData().getLevel(), player.getGroupSuffix());
			TextComponent message = new TextComponent( GameAPI.i18n().get("chat.report_icon")[0] );
			message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "creport " + i) );
			message.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(GameAPI.i18n().get("chat.report_hover")[0]).create() ) );
			player.spigot().sendMessage( message, new TextComponent() );
			for(Player pl : e.getRecipients())
				pl.sendMessage(message, new TextComponent(" " + s.get((BadblockPlayer) pl)[0]));
		}

	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority=EventPriority.HIGHEST,ignoreCancelled=true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e){
		if(!enabled) return;
		BadblockPlayer player = (BadblockPlayer) e.getPlayer();
		if((e.getMessage().startsWith("/t ") || e.getMessage().equalsIgnoreCase("/t") || e.getMessage().startsWith("/team ") || e.getMessage().equalsIgnoreCase("/team"))){
			e.setCancelled(true);
			if (!team) {
				player.sendTranslatedMessage("game.doesntexisthaveteams");
			}else if (player.getTeam() == null) {
				player.sendTranslatedMessage("game.youdonthaveteam");
			}else{
				if(!e.getMessage().contains(" ")) {
					player.sendTranslatedMessage("game.pleasespecifyamessage");
				}else{
					TranslatableString result = new TranslatableString("chat.team" + (custom == null ? "" : "." + custom), player.getName(), player.getGroupPrefix(), player.getTeam().getChatName(), e.getMessage().replace(e.getMessage().split(" ")[0], ""), player.getPlayerData().getLevel());
					for(BadblockPlayer p : player.getTeam().getOnlinePlayers()){
						if (e.getRecipients().contains(p))
							result.send(p);
					}
				}
			}
		}
	}

	public static void protectColor(BadblockPlayer player, AsyncPlayerChatEvent event) {
		if(player.hasPermission(GamePermission.ADMIN)){
			String temp = ChatColor.translateAlternateColorCodes('&', event.getMessage());
			event.setMessage(temp);
		}else event.setMessage(event.getMessage().replace("&", ""));
	}

}
