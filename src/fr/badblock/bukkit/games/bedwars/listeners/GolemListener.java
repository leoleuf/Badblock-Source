package fr.badblock.bukkit.games.bedwars.listeners;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;

import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.BukkitUtils;

public class GolemListener extends BadListener {

	public static Map<IronGolem, BadblockTeam> golems = new HashMap<IronGolem, BadblockTeam>();
	
	public GolemListener()
	{
		Bukkit.getScheduler().runTaskTimer(PluginBedWars.getInstance(), new Runnable()
		{

			@Override
			public void run()
			{
				Iterator<Entry<IronGolem, BadblockTeam>> iterator = golems.entrySet().iterator();
				while (iterator.hasNext())
				{
					Entry<IronGolem, BadblockTeam> entry = iterator.next();
					if (entry.getKey() == null || !entry.getKey().isValid())
					{
						iterator.remove();
						continue;
					}
					
					Player nearbyPlayer = getNearbyPlayer(entry, 10);
					
					if (nearbyPlayer == null)
					{
						continue;
					}
					
					IronGolem ironGolem = entry.getKey();
					
					if (nearbyPlayer.equals(ironGolem.getTarget()))
					{
						continue;
					}
					
					ironGolem.setPlayerCreated(false);
					ironGolem.damage(0, nearbyPlayer);
					ironGolem.setTarget(nearbyPlayer);
				}
			}

		}, 20, 20);
	}
	
	private Player getNearbyPlayer(Entry<IronGolem, BadblockTeam> entry, double max)
	{
		Location golemLocation = entry.getKey().getLocation();
		
		Player nearbyPlayer = null;
		double nearby = 0.0D;
		for (BadblockPlayer po : BukkitUtils.getPlayers())
		{
			if (!GameMode.SURVIVAL.equals(po.getGameMode()))
			{
				continue;
			}
			
			if (!BadblockMode.PLAYER.equals(po.getBadblockMode()))
			{
				continue;
			}
			
			if (!po.getWorld().equals(golemLocation.getWorld()))
			{
				continue;
			}
			
			if (po.getTeam() == null || po.getTeam().equals(entry.getValue()))
			{
				continue;
			}
			
			double distance = po.getLocation().distance(golemLocation);
			
			if ((nearbyPlayer == null || nearby > distance) && distance <= max)
			{
				nearbyPlayer = po;
				nearby = distance;
			}
		}
		
		return nearbyPlayer;
	}

	@EventHandler
	public void onTarget(EntityTargetEvent event)
	{
		if (!event.getEntityType().equals(EntityType.IRON_GOLEM))
		{
			return;
		}

		if (event.getTarget() == null || !event.getTarget().getType().equals(EntityType.PLAYER))
		{
			event.setCancelled(true);
			return;
		}

		if (!(event.getEntity() instanceof IronGolem))
		{
			return;
		}

		IronGolem ironGolem = (IronGolem) event.getEntity();

		if (!golems.containsKey(ironGolem))
		{
			return;
		}

		BadblockPlayer target = (BadblockPlayer) event.getTarget();
		BadblockTeam team = golems.get(ironGolem);

		if (target == null || team == null)
		{
			event.setCancelled(true);
			return;
		}

		if (target.getTeam() != null && target.getTeam().equals(team))
		{
			event.setCancelled(true);
		}
	}

}