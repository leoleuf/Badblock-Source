package fr.badblock.bukkit.hub.v2.utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public class ChatColorUtils
{

	public static List<String> getTranslatedMessages(String[] messages)
	{
		List<String> result = new ArrayList<>();
		for (String message : messages)
		{
			result.add(ChatColor.translateAlternateColorCodes('&', message));
		}
		return result;
	}

}
