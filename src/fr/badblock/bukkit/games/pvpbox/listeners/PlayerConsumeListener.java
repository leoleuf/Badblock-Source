package fr.badblock.bukkit.games.pvpbox.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import fr.badblock.bukkit.games.pvpbox.PvPBox;
import fr.badblock.gameapi.BadListener;

public class PlayerConsumeListener extends BadListener
{

	@EventHandler
	public void onConsume(PlayerItemConsumeEvent event){

		if(!(event.getPlayer() instanceof Player))
		{
			return;
		}
		
		if(!event.getItem().getType().equals(Material.POTION))
		{
			return;
		}
		
		Bukkit.getScheduler().runTaskLaterAsynchronously(PvPBox.getInstance(), new Runnable() {
			@Override
			public void run()
			{
				Player player = event.getPlayer();
				if (player.getInventory().contains(Material.GLASS_BOTTLE))
				{
					player.getInventory().remove(Material.GLASS_BOTTLE);
				}
			}
		}, 1L);
	}

}
