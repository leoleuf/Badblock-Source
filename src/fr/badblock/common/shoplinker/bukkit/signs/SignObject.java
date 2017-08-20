package fr.badblock.common.shoplinker.bukkit.signs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import lombok.Getter;

@Getter
public class SignObject {

	public String 			 		   world;
	public int    			 		   x;
	public int    			 		   y;
	public int    			 		   z;
	public String 			 		   inventoryName;

	@Getter private transient Location location;
	
	public SignObject(String world, int x, int y, int z, String inventoryName) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.inventoryName = inventoryName;
		this.genLocation();
	}
	
	public SignObject(Location location, String inventoryName) {
		this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), inventoryName);
	}
	
	public void genLocation() {
		World world = Bukkit.getWorld(this.world);
		location = new Location(world, x, y, z);
	}
	
}
