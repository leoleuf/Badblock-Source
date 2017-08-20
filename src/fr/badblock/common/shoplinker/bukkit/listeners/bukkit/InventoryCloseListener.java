package fr.badblock.common.shoplinker.bukkit.listeners.bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryActionManager;

public class InventoryCloseListener implements Listener {

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		if (!(event.getPlayer() instanceof Player)) return;
		Player player = (Player) event.getPlayer();
		InventoryActionManager.setInventory(player, null);
	}

}
