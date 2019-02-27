package fr.badblock.bukkit.games.pvpbox.utils;

import org.bukkit.Material;

public class MaterialUtil
{

	public static Material getMaterial(String name)
	{
		for (Material material : Material.values())	
		{
			if (material.name().equalsIgnoreCase(name))
			{
				return material;
			}
		}
		
		return null;
	}
	
}