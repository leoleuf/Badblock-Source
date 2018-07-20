package fr.badblock.bukkit.games.bedwars.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;

import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;

public class SilverfishListener extends BadListener {

	public static Map<Silverfish, BadblockTeam> silverfishs = new HashMap<Silverfish, BadblockTeam>();
	
	@EventHandler
	public void onTarget(EntityTargetEvent event)
	{
		if (!event.getEntityType().equals(EntityType.SILVERFISH))
		{
			return;
		}
		
		if (event.getTarget() == null || !event.getTarget().getType().equals(EntityType.PLAYER))
		{
			event.setCancelled(true);
			return;
		}
		
		Silverfish silverfish = (Silverfish) event.getEntity();
		
		if (!silverfishs.containsKey(silverfish))
		{
			return;
		}
		
		BadblockPlayer target = (BadblockPlayer) event.getTarget();
		BadblockTeam team = silverfishs.get(silverfish);
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