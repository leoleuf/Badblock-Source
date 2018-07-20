package fr.badblock.bukkit.games.bedwars.entities;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.bedwars.PluginBedWars;

public class PathwayHandler
{
	public static HashMap<Projectile, Pathway> pathways = new HashMap<>();

	public PathwayHandler()
	{
		new BukkitRunnable()
		{
			@SuppressWarnings("deprecation")
			public void run()
			{
				if (!PathwayHandler.pathways.isEmpty()) {
					for (Projectile proj : PathwayHandler.pathways.keySet())
					{
						Player pl = (Player)proj.getShooter();
						Location loc = proj.getLocation().clone();
						if (pl.getLocation().distance(loc) > 4.0D)
						{
							Pathway pathway = PathwayHandler.pathways.get(proj);
							byte data = pathway.getData();
							loc.clone().subtract(0.0D, 2.0D, 0.0D).getBlock().setType(pathway.getMaterial());

							loc.clone().subtract(0.0D, 2.0D, 0.0D).getBlock().setData(data);

							pl.playSound(loc.clone().subtract(0.0D, 2.0D, 0.0D), Sound.CHICKEN_EGG_POP, 10.0F, 1.0F);

							loc.clone().subtract(0.0D, 2.0D, 0.0D).subtract(0.0D, 0.0D, 1.0D).getBlock().setType(pathway.getMaterial());
							loc.clone().subtract(0.0D, 2.0D, 0.0D).subtract(1.0D, 0.0D, 0.0D).getBlock().setType(pathway.getMaterial());
							loc.clone().subtract(0.0D, 2.0D, 0.0D).add(0.0D, 0.0D, 1.0D).getBlock().setType(pathway.getMaterial());
							loc.clone().subtract(0.0D, 2.0D, 0.0D).add(1.0D, 0.0D, 0.0D).getBlock().setType(pathway.getMaterial());

							loc.clone().subtract(0.0D, 2.0D, 0.0D).subtract(0.0D, 0.0D, 1.0D).getBlock().setData(data);
							loc.clone().subtract(0.0D, 2.0D, 0.0D).subtract(1.0D, 0.0D, 0.0D).getBlock().setData(data);
							loc.clone().subtract(0.0D, 2.0D, 0.0D).add(0.0D, 0.0D, 1.0D).getBlock().setData(data);
							loc.clone().subtract(0.0D, 2.0D, 0.0D).add(1.0D, 0.0D, 0.0D).getBlock().setData(data);
						}
					}
				}
			}
		}.runTaskTimer(PluginBedWars.getInstance(), 0L, 1L);
	}
}
