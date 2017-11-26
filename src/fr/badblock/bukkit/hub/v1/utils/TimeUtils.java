package fr.badblock.bukkit.hub.v1.utils;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

	public static String getStringTime(long seconds) {
		if (seconds == 0) return "Instantané";
		int day = (int) TimeUnit.SECONDS.toDays(seconds);
		long hours = TimeUnit.SECONDS.toHours(seconds) - TimeUnit.DAYS.toHours(day);
		long minute = TimeUnit.SECONDS.toMinutes(seconds) - TimeUnit.HOURS.toMinutes(TimeUnit.SECONDS.toHours(seconds));
		long second = TimeUnit.SECONDS.toSeconds(seconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(seconds));
		return (day > 0 ? day + "j": "") + 
				(hours > 0 ? hours + "h": "") + 
				(minute > 0 ? minute + "m" : "") + 
				(second > 0 ? second + "s" : "");
	}
	
}
