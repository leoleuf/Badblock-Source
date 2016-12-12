package fr.badblock.spaceballs.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.spaceballs.PluginSB;

public class MoveListener extends BadListener {
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if(e.getTo().getY() <= 0.0d && !inGame()){
			Location spawn = PluginSB.getInstance().getConfiguration().spawn.getHandle();
			
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
				player.setHealth(0.0D);
			}
		}
	}
}
