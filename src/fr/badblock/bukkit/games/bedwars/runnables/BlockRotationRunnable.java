package fr.badblock.bukkit.games.bedwars.runnables;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.bedwars.configuration.npc.BreakableBlock;
import fr.badblock.bukkit.games.bedwars.configuration.npc.MapBreakableBlock;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.utils.ConfigUtils;

public class BlockRotationRunnable extends BukkitRunnable {

	private Map<ArmorStand, Material> armorStands = new HashMap<>();

	public BlockRotationRunnable(List<MapBreakableBlock> blocks)
	{	
		for (MapBreakableBlock block : blocks)
		{
			BreakableBlock handle = block.getHandle();
			Location location = ConfigUtils.convertStringToLocation(handle.location);
			ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND); //Spawn the ArmorStands
			
			as.setGravity(false); //Make sure it doesn't fall
			as.setCanPickupItems(false); //I'm not sure what happens if you leave this as it is, but you might as well disable it
			as.setVisible(false); //Makes the ArmorStand invisible
			
			Material material = getMaterial(handle.material);
			ItemStack item = new ItemStack(material, 1);
			as.setHelmet(item);
			
			this.armorStands.put(as, material);
		}

		this.runTaskTimer(GameAPI.getAPI(), 1, 1);
	}
	
	private Material getMaterial(String string)
	{
		for (Material material : Material.values())
		{
			if (material.name().equalsIgnoreCase(string))
			{
				return material;
			}
		}
		
		return null;
	}

	@Override
	public void run()
	{
		for (Entry<ArmorStand, Material> entry : armorStands.entrySet())
		{
			ArmorStand armorStand = entry.getKey();
			Location location = armorStand.getLocation();
			if (location.getYaw() >= 180)
			{
				location.setYaw(-180);
			}
			location.setYaw(location.getYaw() + 4);
			armorStand.teleport(location);
		}
	}

}
