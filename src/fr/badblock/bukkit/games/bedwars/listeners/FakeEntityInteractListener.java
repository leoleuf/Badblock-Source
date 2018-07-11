package fr.badblock.bukkit.games.bedwars.listeners;

import java.util.Optional;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.bukkit.games.bedwars.configuration.BedWarsMapConfiguration;
import fr.badblock.bukkit.games.bedwars.configuration.breakable.InventoryNPC;
import fr.badblock.bukkit.games.bedwars.configuration.breakable.MapInventoryNPC;
import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
import fr.badblock.bukkit.games.bedwars.inventories.BukkitInventories;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.events.PlayerFakeEntityInteractEvent;
import fr.badblock.gameapi.packets.in.play.PlayInUseEntity.UseEntityAction;
import fr.badblock.gameapi.utils.ConfigUtils;

public class FakeEntityInteractListener extends BadListener {
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onFakeInteract(PlayerFakeEntityInteractEvent e){
		try {
			if(!inGame() || e.getPlayer().getTeam() == null)
				return;

			if (!e.getAction().equals(UseEntityAction.INTERACT))
			{
				return;
			}

			boolean isInZone = e.getPlayer().getTeam().teamData(BedWarsTeamData.class).getSpawnSelection().isInSelection(e.getEntity().getLocation());

			if(!isInZone) {
				e.setCancelled(true);
				return;
			}

			BedWarsMapConfiguration map = PluginBedWars.getInstance().getMapConfiguration();

			if (map == null)
			{
				return;
			}

			Optional<MapInventoryNPC> optional = map.getInventoryNPC().parallelStream().filter(npc -> npc.getHandle().location.equalsIgnoreCase(ConfigUtils.convertLocationToString(e.getEntity().getLocation()))).findFirst();
			if (optional.isPresent())
			{
				MapInventoryNPC mapNPC = optional.get();
				InventoryNPC inventory = mapNPC.getHandle();
				BukkitInventories.openInventory(e.getPlayer(), inventory.inventoryName);
			}

		}
		catch(Exception error)
		{
			System.out.println("Error on FakeEntityInteractListener: " + error.getMessage());
			error.printStackTrace();
		}

	}
}
