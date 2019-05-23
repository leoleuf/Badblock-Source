package fr.badblock.bukkit.games.uhc.biomes;

import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.plugin.java.JavaPlugin;

import net.minecraft.server.v1_8_R3.BiomeBase;

public class UHCBiomes extends JavaPlugin
{

	@Override
	public void onLoad()
	{
		int i = 0;
		for (Biome biome : Biome.values())
		{
			if (biome.equals(Biome.FOREST))
			{
				setBiomeBase(biome, Biome.PLAINS);
				continue;
			}

			i++;

			if (i % 5 == 0)
			{
				setBiomeBase(biome, Biome.FOREST);
			}
			else
			{
				setBiomeBase(biome, Biome.PLAINS);
			}
		}
	}

	public void setBiomeBase(Biome from, Biome to)
	{
		BiomeBase[] biomes = BiomeBase.getBiomes();

		BiomeBase fromBB = CraftBlock.biomeToBiomeBase(from);
		BiomeBase toBB = CraftBlock.biomeToBiomeBase(to);

		biomes[(fromBB.id)] = toBB;
	}

}
