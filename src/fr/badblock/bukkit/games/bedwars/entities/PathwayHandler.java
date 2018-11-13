package fr.badblock.bukkit.games.bedwars.entities;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.bukkit.games.bedwars.listeners.BedWarsMapProtector;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;

public class PathwayHandler
{
	public static HashMap<Projectile, Pathway> pathways = new HashMap<>();
	public static HashMap<Projectile, Long> t = new HashMap<>();

	public PathwayHandler()
	{
		new BukkitRunnable()
		{
			public void run()
			{
				if (!PathwayHandler.pathways.isEmpty()) {
					for (Projectile proj : PathwayHandler.pathways.keySet())
					{
						if (!t.containsKey(proj))
						{
							t.put(proj, 10L);
						}
						else
						{
							long ticks = t.get(proj);
							ticks--;
							if (ticks == 0)
							{
								continue;
							}
						}
						Player pl = (Player)proj.getShooter();
						Location loc = proj.getLocation().clone();
						BadblockPlayer player = (BadblockPlayer) pl.getPlayer();
						BadblockTeam team = player.getTeam();
						if (team == null) continue;
						Location location = team.teamData(BedWarsTeamData.class).getRespawnLocation();
						if (loc.getY() - location.getY() >= 30) {
							continue;
						}
						if (pl.getLocation().distance(loc) > 4.0D)
						{
							Pathway pathway = PathwayHandler.pathways.get(proj);
							byte data = pathway.getData();
							
							setMaterial(player, loc.clone().subtract(0.0D, 2.0D, 0.0D), pathway.getMaterial());

							setData(player, loc.clone().subtract(0.0D, 2.0D, 0.0D), data);

							pl.playSound(loc.clone().subtract(0.0D, 2.0D, 0.0D), Sound.CHICKEN_EGG_POP, 10.0F, 1.0F);

							setMaterial(player, loc.clone().subtract(0.0D, 2.0D, 0.0D).subtract(0.0D, 0.0D, 1.0D), pathway.getMaterial());
							setMaterial(player, loc.clone().subtract(0.0D, 2.0D, 0.0D).subtract(1.0D, 0.0D, 0.0D), pathway.getMaterial());
							setMaterial(player, loc.clone().subtract(0.0D, 2.0D, 0.0D).add(0.0D, 0.0D, 1.0D), pathway.getMaterial());
							setMaterial(player, loc.clone().subtract(0.0D, 2.0D, 0.0D).add(1.0D, 0.0D, 0.0D), pathway.getMaterial());

							setData(player, loc.clone().subtract(0.0D, 2.0D, 0.0D).subtract(0.0D, 0.0D, 1.0D), data);
							setData(player, loc.clone().subtract(0.0D, 2.0D, 0.0D).subtract(1.0D, 0.0D, 0.0D), data);
							setData(player, loc.clone().subtract(0.0D, 2.0D, 0.0D).add(0.0D, 0.0D, 1.0D), data);
							setData(player, loc.clone().subtract(0.0D, 2.0D, 0.0D).add(1.0D, 0.0D, 0.0D), data);
						}
					}
				}
			}
		}.runTaskTimer(PluginBedWars.getInstance(), 0L, 1L);
	}

	public static void setMaterial(BadblockPlayer player, Location location, Material material)
	{
		for (BadblockTeam team : GameAPI.getAPI().getTeams())
		{
			BedWarsTeamData td = team.teamData(BedWarsTeamData.class);
			if (td == null)
			{
				continue;
			}

			if (td.getSpawnSelection() != null && td.getSpawnSelection().isInSelection(location))
			{
				return;
			}
		}
		
		BadblockTeam team = player.getTeam();
		if (team == null) return;
		Location lc = team.teamData(BedWarsTeamData.class).getRespawnLocation();
		if (location.getY() - lc.getY() >= 30) {
			return;
		}

		location.getBlock().setType(material);
		BedWarsMapProtector.breakableBlocks.add(location.getBlock().getLocation());
	}
	
	@SuppressWarnings("deprecation")
	public static void setData(BadblockPlayer player, Location location, byte data)
	{
		for (BadblockTeam team : GameAPI.getAPI().getTeams())
		{
			BedWarsTeamData td = team.teamData(BedWarsTeamData.class);
			if (td == null)
			{
				continue;
			}

			if (td.getSpawnSelection() != null && td.getSpawnSelection().isInSelection(location))
			{
				return;
			}
		}

		BadblockTeam team = player.getTeam();
		if (team == null) return;
		Location lc = team.teamData(BedWarsTeamData.class).getRespawnLocation();
		if (location.getY() - lc.getY() >= 30) {
			return;
		}

		location.getBlock().setData(data);
		BedWarsMapProtector.breakableBlocks.add(location.getBlock().getLocation());
	}

}
