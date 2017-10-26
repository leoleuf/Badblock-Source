package fr.badblock.bukkit.hub.listeners.players;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import fr.badblock.bukkit.hub.listeners._HubListener;
import fr.badblock.gameapi.players.BadblockPlayer;

public class DoubleJumpListener extends _HubListener {

	public Map<String, Integer> timesJumped = new HashMap<>();

	@EventHandler
	public void join(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (player.hasPermission("hub.doublejump"))
		{
			player.setAllowFlight(true);
		}
	}

	@EventHandler
	public void setFlyOnJump(PlayerToggleFlightEvent event) {
		BadblockPlayer player = (BadblockPlayer) event.getPlayer();
		Vector jump = player.getLocation().getDirection().multiply(0.2).setY(1);
		Location loc = player.getLocation();
		Block block = loc.add(0, -1, 0).getBlock();

		if(event.isFlying() && event.getPlayer().getGameMode() != GameMode.CREATIVE)
		{
			if (player.hasPermission("hub.doublejump"))
			{
				Integer maxInt = player.getPermissionValue("doublejump", Integer.class);
				int max = 0;
				if (maxInt != null)
				{
					max = maxInt.intValue();
				}
				int jumpZ = timesJumped.containsKey(player.getName()) ? timesJumped.get(player.getName()) : 0;
				if(jumpZ != max)
				{
					player.setFlying(false);
					player.setVelocity(player.getVelocity().add(jump));
					jumpZ++;
				}
				else if(jumpZ == max) 
				{
					if (block.getType() != Material.AIR)
					{
						player.setAllowFlight(true);
						jumpZ = 0;
					}
					else 
					{
						player.setFlying(false);
						player.setAllowFlight(true);
					}
				}
				timesJumped.put(player.getName(), jumpZ);

				event.setCancelled(true);
			}
		}
	}

}
