package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.badblock.bukkit.games.shootflag.PluginShootFlag;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;

public class MoveListener extends BadListener {
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if(e.getTo().getY() <= 0.0d && !inGame()){
			Location spawn = PluginShootFlag.getInstance().getConfiguration().spawn.getHandle();

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
		} else if(inGame()){
			BadblockPlayer player = (BadblockPlayer) e.getPlayer();

			if(player.getTeam() == null || player.getBadblockMode() != BadblockMode.PLAYER) return;

			// Capturer le drapeau
			/*for(BadblockTeam team : GameAPI.getAPI().getTeams()){
				if(team.teamData(ShootFlagTeamData.class).getPool().isInSelection(e.getTo()) && !team.equals(player.getTeam())){
					player.getTeam().teamData(ShootFlagTeamData.class).addMark(player);
				}
			}*/
		}
	}
}
