package fr.badblock.survival.listeners;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.badblock.gameapi.BadListener;
import fr.badblock.survival.PluginSurvival;
import fr.badblock.survival.runnables.game.NoMoveRunnable;

public class MoveListener extends BadListener {
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if(e.getTo().getY() <= 0.0d && !inGame()){
			Location spawn = PluginSurvival.getInstance().getConfiguration().spawn.getHandle();

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
		}

		if(NoMoveRunnable.noMove){
			Location from = e.getFrom().clone();
			from.setYaw(e.getTo().getYaw());
			from.setPitch(e.getTo().getPitch());

			e.setTo(from);
		}
	}
}
