package fr.badblock.bukkit.games.bedwars.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityTargetEvent;

import fr.badblock.bukkit.games.bedwars.players.AngryIronGolem;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;

public class GolemListener extends BadListener {

	public static Map<IronGolem, BadblockTeam> golems = new HashMap<IronGolem, BadblockTeam>();
	
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
		
		if (!(event.getEntity() instanceof AngryIronGolem))
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