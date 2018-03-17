package fr.badblock.bukkit.games.shootflag.flags;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;

import fr.badblock.bukkit.games.shootflag.PluginShootFlag;
import fr.badblock.bukkit.games.shootflag.commands.GameCommand;
import fr.badblock.bukkit.games.shootflag.configuration.ShootFlagMapConfiguration;
import lombok.Data;

@Data
public class FlagCreator
{

	// Some config
	private static int flagId		 = 0;
	private static int flagMaxRadius = 25;
	private static int itemFrames	 = 4;
	
	private Location location;
	
	public FlagCreator(Location location)
	{
		setLocation(location);
	}
	
	protected boolean isNearby(Material material)
	{
		return !getNearbyBlocks(material).isEmpty();
	}
	
	protected List<Location> getNearbyEntitiesLocation(EntityType entitType)
	{
		final List<Location> locations = new ArrayList<>();
		for (Entity entity : getLocation().getWorld().getEntities())
		{
			if (entity instanceof ItemFrame && entity.getLocation().distance(getLocation()) <= flagMaxRadius)
			{
				locations.add(entity.getLocation());
			}
		}
		return locations;
	}
	
	protected List<Block> getNearbyBlocks(Material material)
	{
		final List<Block> blocks = new ArrayList<>(); 
		for (double x = -flagMaxRadius; x <= flagMaxRadius; x++)
		{
			for (double y = -flagMaxRadius; y <= flagMaxRadius; y++)
			{
				for (double z = -flagMaxRadius; z <= flagMaxRadius; z++)
				{
					Location location = getLocation().clone().add(x, y, z);
					if (location.getBlock().getType().equals(material))
					{
						blocks.add(location.getBlock());
					}
				}	
			}
		}
		return blocks;
	}
	
	protected List<Location> getNearbyBlocksLocation(Material material)
	{
		return getNearbyBlocks(material).stream().map(block -> block.getLocation()).collect(Collectors.toList());
	}
	
	protected boolean containsItemframe()
	{
		int i = 0;
		for (Entity entity : getLocation().getWorld().getEntities())
		{
			if (entity instanceof ItemFrame && entity.getLocation().distance(getLocation()) <= flagMaxRadius)
			{
				i++;
			}
		}
		return i >= itemFrames;
	}
	
	protected boolean isMakeable()
	{
		return isNearby(Material.BEACON)
				&& isNearby(Material.STAINED_GLASS)
				&& isNearby(Material.WOOL)
				&& containsItemframe();
	}
	
	public boolean setup()
	{
		if (!isMakeable())
		{
			return false;
		}
		if (flagId >= 6)
		{
			return false;
		}
		List<Location> itemFrames = getNearbyEntitiesLocation(EntityType.ITEM_FRAME);
		List<Location> wools = getNearbyBlocksLocation(Material.WOOL);
		List<Location> glass = getNearbyBlocksLocation(Material.BEACON);
		flagId = flagId + 1 < 6 ? flagId + 1 : 1;
		String name = FlagType.values()[flagId - 1].name();
		ShootFlagMapConfiguration shConfig = PluginShootFlag.getInstance().getMapConfiguration();
		Flag flag = new Flag(name, glass, itemFrames, wools, true);
		shConfig.getFlags().add(flag);
		shConfig.save(GameCommand.generatedFile);
		return true;
	}
	
}
