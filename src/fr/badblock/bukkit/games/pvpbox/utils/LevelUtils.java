package fr.badblock.bukkit.games.pvpbox.utils;

public class LevelUtils
{

	public static int getXP(int nextLevel)
	{
		double rawLevel = 2.5D * (Math.pow(2.0D, nextLevel)) + 0;
	
		return (int) Math.ceil(rawLevel);
	}
	
}