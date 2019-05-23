package fr.badblock.bukkit.games.spaceballs.rockets;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.spaceballs.PluginSB;
import fr.badblock.bukkit.games.spaceballs.entities.SpaceTeamData;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.selections.CuboidSelection;
import fr.badblock.gameapi.utils.selections.Vector3f;

public class RocketDestructive implements Rocket {
	private static final int	 		radius   = 20;
	private static final List<Vector3f> blocks   = new ArrayList<>();
	private static final Random		    random   = new Random();
	private static final double			dropProb = 0.02;

	static {
		double pi 	 = Math.PI;
		double perim = 2 * radius * pi;
		double step  = pi / perim;
		
		for(double arg=0d;arg < 2 * pi;arg += step){

			double cos = Math.cos(arg);
			double sin = Math.sin(arg);

			for(double i=0d;i<radius;i += 0.2d){
				Vector3f vector = new Vector3f((int) (cos * i), 0d, (int) (sin * i));

				if(!blocks.contains(vector))
					blocks.add(vector);
			}
		}
	}

	@Override
	public int getRange() {
		return 30;
	}

	@Override
	public String getName() {
		return "destructive";
	}

	@Override
	public void impact(BadblockPlayer launcher, Block block) {
		createExplosion(block.getLocation());
	}

	@Override
	public void impact(BadblockPlayer launcher, Entity entity) {
		createExplosion(entity.getLocation());
	}

	protected void createExplosion(Location location){
		World  w = location.getWorld();
		double x = location.getX();
		double z = location.getZ();
		int    y = (int) location.getY();

		List<CuboidSelection> toProtect = toProtect();

		new BukkitRunnable(){
			@Override
			public void run(){
				List<Block> list = new ArrayList<>();

				for(int i=y-radius;i<y+radius;i++){
					for(Vector3f vector : blocks){
						Block block = new Location(w, x  + vector.getX(), i, z + vector.getZ()).getBlock();

						boolean can = true;

						for(CuboidSelection selection : toProtect)
							if(selection.isInSelection(block)){
								can = false; break;
							}
						
						can = can && PluginSB.getInstance().getMapConfiguration().getMapBounds().isInSelection(block);

						if(!can)
							continue;

						if(block.getType() != Material.AIR && block.getType() != Material.DIAMOND_ORE){
							list.add(block);
						}
					}
				}

				new BukkitRunnable(){
					int i = 0;

					@SuppressWarnings("deprecation")
					@Override
					public void run(){

						int o = i;
						i += 60;

						if(i > list.size()){
							i = list.size();
							cancel();
						}

						for(;o<i;o++){
							Block block = list.get(o);

							if(random.nextDouble() <= dropProb){
								FallingBlock entity = w.spawnFallingBlock(block.getLocation(), block.getType(), block.getData());

								entity.setDropItem(false);
								entity.setHurtEntities(false);
							}
							
							block.setType(Material.AIR);
						}



					}
				}.runTaskTimer(GameAPI.getAPI(), 0, 1L);
			}
		}.runTaskAsynchronously(GameAPI.getAPI());

	}

	private List<CuboidSelection> toProtect(){
		List<CuboidSelection> toProtect = new ArrayList<>();

		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			toProtect.add( extended(team.teamData(SpaceTeamData.class).getSpawnSelection()) );
		}

		toProtect.add( extended(PluginSB.getInstance().getMapConfiguration().getTowerBounds()) );

		return toProtect;
	}

	private CuboidSelection extended(CuboidSelection selection){
		return new CuboidSelection(selection.getWorldName(), selection.getFirstBound().clone().setY(0.0d), selection.getSecondBound().clone().setY(256.0d));
	}
}
