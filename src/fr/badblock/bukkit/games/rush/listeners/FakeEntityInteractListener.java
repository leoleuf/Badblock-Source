package fr.badblock.bukkit.games.rush.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import fr.badblock.bukkit.games.rush.inventories.BukkitInventories;
import fr.badblock.bukkit.games.rush.inventories.LinkedInventoryEntity;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.events.PlayerFakeEntityInteractEvent;
import fr.badblock.gameapi.packets.in.play.PlayInUseEntity.UseEntityAction;

public class FakeEntityInteractListener extends BadListener {
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onFakeInteract(PlayerFakeEntityInteractEvent e){
		try {
			e.setCancelled(true);
			
			if(!inGame() || e.getPlayer().getTeam() == null)
				return;
			
			if (!e.getAction().equals(UseEntityAction.INTERACT))
			{
				return;
			}
			
			if (!LinkedInventoryEntity.getFakeEntities().containsKey(e.getEntity()))
			{
				return;
			}
			
			String inventoryName = LinkedInventoryEntity.getFakeEntities().get(e.getEntity());
			if (inventoryName != null)
			{
				BukkitInventories.openInventory(e.getPlayer(), inventoryName);
			}

		}
		catch(Exception error)
		{
			System.out.println("Error on FakeEntityInteractListener: " + error.getMessage());
			error.printStackTrace();
		}

	}
}
