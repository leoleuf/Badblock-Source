package fr.badblock.bukkit.games.rush.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import fr.badblock.bukkit.games.rush.entities.RushTeamData;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.events.PlayerFakeEntityInteractEvent;
import fr.badblock.gameapi.packets.in.play.PlayInUseEntity.UseEntityAction;

public class FakeEntityInteractListener extends BadListener {
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onFakeInteract(PlayerFakeEntityInteractEvent e){
		try {
			if(!inGame() || e.getPlayer().getTeam() == null)
				return;

			if(e.getAction() == UseEntityAction.INTERACT){

				boolean isInZone = e.getPlayer().getTeam().teamData(RushTeamData.class).getSpawnSelection().isInSelection(e.getEntity().getLocation());

				if(!isInZone) {
					e.setCancelled(true);
				}
			}
		}catch(Exception error) {
			System.out.println("Error on FakeEntityInteractListener: " + error.getMessage());
			error.printStackTrace();
		}

	}
}
