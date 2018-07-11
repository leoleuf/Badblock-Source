package fr.badblock.bukkit.games.bedwars.runnables;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockTeam;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor public class ItemSpawnRunnable extends BukkitRunnable {
	private final Material   material;
	private final long	     ticks;

	private List<Item> items  = new ArrayList<>();
	
	@Override
	public void run(){
		for(Item item : items){
			if(!item.isDead()){
				item.remove();
			}
		}
		
		items.clear();
		
		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			for(Location location : team.teamData(BedWarsTeamData.class).getItemSpawnLocations()){
				Item item = location.getWorld().dropItem(location, new ItemStack(material, 1));
				item.setVelocity(new Vector(0, 0, 0));
				
				items.add(item);
			}
			
		}
		
	}

	public void start(){
		runTaskTimer(GameAPI.getAPI(), ticks, ticks);
	}
}
