package fr.badblock.bukkit.games.bedwars.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;

public class MoveListener extends BadListener {
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if(e.getTo().getY() <= 0.0d && !inGame()){
			Location spawn = PluginBedWars.getInstance().getConfiguration().spawn.getHandle();

			Entity vehicle = null;

			if(e.getPlayer().isInsideVehicle()){
				vehicle = e.getPlayer().getVehicle();
				vehicle.eject();
				vehicle.teleport(spawn);
			}

			e.setCancelled(true);
			e.getPlayer().teleport(spawn);

			if(vehicle != null)
				vehicle.setPassenger(e.getPlayer());
		}else if(e.getTo().getY() <= 0.0d && inGame()){
			BadblockPlayer player = (BadblockPlayer) e.getPlayer();
			if (player.getBadblockMode().equals(BadblockMode.PLAYER)) {
				@SuppressWarnings("deprecation")
				EntityDamageEvent event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.VOID, player.getHealth());
				player.setLastDamageCause(event);
				Bukkit.getServer().getPluginManager().callEvent(event);
			}
		}
		else if (inGame())
		{
			BadblockPlayer player = (BadblockPlayer) e.getPlayer();
			BadblockTeam team = player.getTeam();

			if (team == null)
			{
				return;
			}

			for (BadblockTeam t : GameAPI.getAPI().getTeams())
			{
				if (t == null)
				{
					continue;
				}
				if (t.equals(team))
				{
					continue;
				}
				BedWarsTeamData td = t.teamData(BedWarsTeamData.class);
				if (td == null)
				{
					continue;
				}

				if (td.getSpawnSelection() != null && td.getSpawnSelection().isInSelection(e.getTo()))
				{
					if (td.trespassing)
					{
						player.getTeam().getOnlinePlayers().forEach(op -> op.playSound(Sound.ENDERMAN_DEATH));
						player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.trespassingchat", player.getName()));
						player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedTitle("bedwars.trespassing", player.getName()));
						td.trespassing = false;
					}
					break;
				}
			}
		}
	}
}
