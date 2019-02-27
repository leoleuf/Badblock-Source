package fr.badblock.bukkit.games.pvpbox.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.badblock.bukkit.games.pvpbox.players.BoxPlayer;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;

public class InventoryCloseListener extends BadListener
{

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event)
	{
		if (!(event.getPlayer() instanceof Player)) return;
		BadblockPlayer player = (BadblockPlayer) event.getPlayer();
		BoxPlayer boxPlayer = BoxPlayer.get(player);
		
		// On met son dernier inventaire à null
		boxPlayer.setInventory(null);
	}
	
}
