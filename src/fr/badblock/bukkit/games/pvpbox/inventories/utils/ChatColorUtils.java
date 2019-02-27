package fr.badblock.bukkit.games.pvpbox.inventories.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;

public class ChatColorUtils {

	private static final char CHAR = '&';

	public static List<String> getTranslatedMessages(String... messages) {
		List<String> result = new ArrayList<>();
		for (String message : messages) result.add(translate(message));
		return result;
	}

	public static List<String> getTranslatedMessages(List<String> messages) {
		List<String> result = new ArrayList<>();
		for (String message : messages) result.add(translate(message));
		return result;
	}

	public static List<String> getTranslatedMessages(String[] messages, Map<String, String> replace) {
		List<String> result = new ArrayList<>();
		for (String message : messages) {
			message = translate(message);
			for (Entry<String, String> entry : replace.entrySet())
				message = message.replace(entry.getKey(), entry.getValue());
			result.add(message);
		}
		return result;
	}

	public static String translate(String message) {
		return ChatColor.translateAlternateColorCodes(CHAR, message);
	}

}
