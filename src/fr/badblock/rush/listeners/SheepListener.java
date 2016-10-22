package fr.badblock.rush.listeners;

import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;

public class SheepListener extends BadListener {
	private DyeColor[] colors = DyeColor.values();
	
	@EventHandler
	public void onDamage(EntityDamageEvent e){
		if(inGame()) return;
		
		if(e.getEntityType() == EntityType.SHEEP)
			e.setCancelled(true);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if(beforeGame() && e.getPlayer().getVehicle() != null){
			
			BadblockPlayer player = (BadblockPlayer) e.getPlayer();
			
			Block 		 b    = e.getPlayer().getVehicle().getLocation().getBlock().getRelative(BlockFace.DOWN);
			BadblockTeam team = player.getTeam();
			
			if(b.getType() != Material.AIR && b.getType().isSolid()){
				
				if(b.getType() == Material.STONE || b.getType() == Material.COBBLESTONE || b.getType() == Material.QUARTZ_BLOCK || b.getType() == Material.OBSIDIAN || b.getType() == Material.COBBLESTONE){
					b.setType(Material.STAINED_CLAY);
				} else if(b.getType() == Material.GRASS || b.getType() == Material.DIRT || b.getType() == Material.NETHER_BRICK || b.getType() == Material.SANDSTONE){
					b.setType(Material.STAINED_GLASS);
				}
				
				if(b.getType() == Material.STAINED_GLASS || b.getType() == Material.WOOL || b.getType() == Material.STAINED_CLAY){
					DyeColor color = team != null ? team.getDyeColor() : colors[ new Random().nextInt(colors.length) ];
					b.setData(color.getWoolData());
				}
			}
		}
	}
	
	@EventHandler
	public void onPvE(EntityDamageByEntityEvent e){
		if(inGame()) return;
		
		if(e.getEntityType() == EntityType.SHEEP && e.getDamager().getType() == EntityType.PLAYER){
			e.setCancelled(true);

			e.getEntity().eject();
			e.getEntity().getVelocity().multiply(10.0d);

			Vector vector = new Vector(
					e.getEntity().getLocation().getX() - e.getDamager().getLocation().getX(),
					0,
					e.getEntity().getLocation().getZ() - e.getDamager().getLocation().getZ()
			);
			
			double distance = e.getDamager().getLocation().distance(e.getEntity().getLocation());
			
			vector.divide(new Vector(distance, distance, distance));
			vector.multiply(2.0d);

			vector.setY(0.8);
			
			Sheep 		   sheep  = (Sheep) e.getEntity();
			BadblockPlayer player = (BadblockPlayer) e.getDamager();

			sheep.setVelocity(vector);
			
			if(player.getTeam() != null){
				sheep.setColor(player.getTeam().getDyeColor());
			} else {
				DyeColor[] colors = DyeColor.values();
				sheep.setColor(colors[ new Random().nextInt(colors.length) ]);
			}
		}
	}
}
