package fr.badblock.bukkit.games.uhc.doublerun.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import fr.badblock.bukkit.games.uhc.doublerun.PluginUHC;
import fr.badblock.bukkit.games.uhc.doublerun.runnables.StartRunnable;
import fr.badblock.bukkit.games.uhc.doublerun.runnables.TeleportRunnable;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;

public class MoveListener extends BadListener {
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		BadblockPlayer p = (BadblockPlayer) e.getPlayer();
		if (StartRunnable.time <= 10 && TeleportRunnable.freezeLoc.containsKey(p))
		{
			if (!TeleportRunnable.freezeLoc.get(p).getBlock().equals(e.getTo().getBlock()))
			{
				e.setTo(TeleportRunnable.freezeLoc.get(p));
			}
			return;
		}

		if(e.getTo().getY() <= 150d && beforeGame()){
			Location spawn = PluginUHC.getInstance().getConfiguration().spawn.getHandle();

			Entity vehicle = null;

			if(e.getPlayer().isInsideVehicle()){
				vehicle = e.getPlayer().getVehicle();
				vehicle.eject();
				vehicle.teleport(spawn);
			}

			e.setTo(spawn);

			if(vehicle != null)
				vehicle.setPassenger(e.getPlayer());
		}
		else if (beforeGame())
		{
			Block b = e.getPlayer().getLocation().clone().getBlock().getRelative(BlockFace.DOWN);
			if (b.getType().equals(Material.STAINED_GLASS))
			{
				BadblockTeam t = p.getTeam();

				if (t != null)
				{
					b.setData(t.getDyeColor().getWoolData());
				}
			}
		}
		else if(e.getTo().getY() <= 0.0d && inGame()){
			BadblockPlayer player = (BadblockPlayer) e.getPlayer();
			if (player.getBadblockMode().equals(BadblockMode.PLAYER)) {
				EntityDamageEvent event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.VOID, player.getHealth());
				player.setLastDamageCause(event);
				Bukkit.getServer().getPluginManager().callEvent(event);
			}
		}
	}
}
