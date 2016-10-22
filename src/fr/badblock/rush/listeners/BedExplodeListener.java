package fr.badblock.rush.listeners;

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

import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.configuration.values.MapMaterial;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.rush.PluginRush;

public class BedExplodeListener extends BadListener {
	private Map<Block, UUID> placedTnts = new HashMap<>();

	@EventHandler
	public void onPlaceBlock(BlockPlaceEvent e){
		if(e.getBlock().getType() == Material.TNT){
			placedTnts.put(e.getBlock(), e.getPlayer().getUniqueId());
		}
	}

	@EventHandler
	public void onTNTExplose(EntityExplodeEvent e){
		e.setYield((float)0.0);
		
		for(int i=0;i<e.blockList().size();i++){
			if(e.blockList().get(i).getType() == Material.BED || e.blockList().get(i).getType() == Material.BED_BLOCK){
				BadblockPlayer player = null;

				for(Entry<Block, UUID> entries : placedTnts.entrySet()){
					Block block = entries.getKey();

					Location blockLoc = block.getLocation().clone();
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
				boolean can = false;
				
				for(MapMaterial material : PluginRush.getInstance().getMapConfiguration().getBreakableBlocks()){
					if(material.match(block)){
						can = true;
						break;
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
