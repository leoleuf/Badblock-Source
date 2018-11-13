package fr.badblock.bukkit.games.bedwars.listeners;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;

public class BedExplodeListener extends BadListener {
	public static Map<Location, UUID> placedTnts = new HashMap<>();

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent e){
		if(e.getBlock().getType() == Material.TNT){
			placedTnts.put(e.getBlock().getLocation(), e.getPlayer().getUniqueId());
		}
		BadblockPlayer player = (BadblockPlayer) e.getPlayer();
		BadblockTeam team = player.getTeam();
		if (team == null) return;
		Location location = team.teamData(BedWarsTeamData.class).getRespawnLocation();
		if (e.getBlock().getY() - location.getY() >= 30) {
			e.setCancelled(true);
			player.sendTranslatedMessage("bedwars.youcantplaceblockstoohigh");
		}
	}

	@EventHandler
	public void onTNTExplose(EntityExplodeEvent e){
		for(int i=0;i<e.blockList().size();i++){
			if(e.blockList().get(i).getType() == Material.BED || e.blockList().get(i).getType() == Material.BED_BLOCK){
				BadblockPlayer player = null;

				for(Entry<Location, UUID> entries : placedTnts.entrySet()){

					Location blockLoc = entries.getKey().clone();
					Location tntLoc   = e.getEntity().getLocation().clone();

					blockLoc.setY(0);
					tntLoc.setY(0);

					if(blockLoc.distance(tntLoc) < 1.5d){
						player = (BadblockPlayer) Bukkit.getPlayer(entries.getValue());
						break;
					}
				}

				if(player == null){
					e.setCancelled(true); return;
				}

				if(!BedListenerUtils.onBreakBed(player, e.blockList().get(i), true)){
					e.blockList().remove(i);
					i--;
				}

			} else {
				Block block = e.blockList().get(i);

				boolean can = BedWarsMapProtector.breakableBlocks.contains(block.getLocation());
				
				if (e.blockList().get(i).getType() == Material.GLASS && BedWarsMapProtector.glassBlocks.containsKey(block.getLocation()))
				{
					BadblockPlayer player = null;

					for(Entry<Location, UUID> entries : placedTnts.entrySet()){

						Location blockLoc = entries.getKey().clone();
						Location tntLoc   = e.getEntity().getLocation().clone();

						blockLoc.setY(0);
						tntLoc.setY(0);

						if(blockLoc.distance(tntLoc) < 1.5d){
							player = (BadblockPlayer) Bukkit.getPlayer(entries.getValue());
							break;
						}
					}

					if(player == null){
						e.setCancelled(true); return;
					}
					
					BadblockTeam t = BedWarsMapProtector.glassBlocks.get(block.getLocation());
					if (t != null && !t.equals(player.getTeam()))
					{
						can = false;
					}
				}
				
				if(!can){
					e.blockList().remove(i);
					i--;
				}
			}
		}
	}
}
