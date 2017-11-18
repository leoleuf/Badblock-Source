package fr.badblock.common.shoplinker.bukkit.utils;

import java.util.HashMap;
import java.util.Map;

public class FlagObject {

	private static Map<Object, Map<String, Long>>	flags;
	
	static
	{
		flags = new HashMap<>();
	}
	
	public static boolean isValid(Object object, String flag)
	{
		if (!hasFlag(object, flag))
		{
			return false;
		}
		Map<String, Long> map = !flags.containsKey(object) ? new HashMap<>() : flags.get(object);
		if (!map.containsKey(flag))
		{
			return false;
		}
		return map.get(flag) > System.currentTimeMillis();
	}
	
	public static boolean hasFlag(Object object, String flag)
	{
		if (!flags.containsKey(object))
		{
			return false;
		}
		Map<String, Long> map = !flags.containsKey(object) ? new HashMap<>() : flags.get(object);
		return map.containsKey(flag);
	}
	
	public static void setTemporaryFlag(Object object, String flag, long time)
	{
		Map<String, Long> map = !flags.containsKey(object) ? new HashMap<>() : flags.get(object);
		map.put(flag, System.currentTimeMillis() + time);
		flags.put(object, map);
	}
	
	public static void removeFlag(Object object, String flag)
	{
		if (!hasFlag(object, flag))
		{
			return;
		}
		Map<String, Long> map = !flags.containsKey(object) ? new HashMap<>() : flags.get(object);
		map.remove(flag);
		flags.put(object, map);
	}
	
}
