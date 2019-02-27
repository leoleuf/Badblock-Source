package fr.badblock.bukkit.games.pvpbox.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.google.gson.JsonObject;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BoxLocation
{

	private transient Location bukkitLocation;
	
	private String		world;
	
	private double	x;
	private double	y;
	private double	z;
	
	private float		pitch;
	private float		yaw;
	
	public BoxLocation(JsonObject jsonObject)
	{
		this.world = jsonObject.get("world").getAsString();
		this.x = jsonObject.get("x").getAsDouble();
		this.y = jsonObject.get("y").getAsDouble();
		this.z = jsonObject.get("z").getAsDouble();
		this.pitch = jsonObject.get("pitch").getAsFloat();
		this.yaw = jsonObject.get("yaw").getAsFloat();
		
		this.buildLocation();
	}
	
	public BoxLocation(String world, double x, double y, double z, float pitch, float yaw)
	{
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
		this.pitch = pitch;
		this.yaw = yaw;
		this.buildLocation();
	}
	
	void buildLocation()
	{
		World w = Bukkit.getWorld(world);
		
		if (w == null)
		{
			throw new IllegalArgumentException("The world " + world + " doesn't exist");
		}
		
		this.setBukkitLocation(new Location(w, x, y, z, yaw, pitch));
	}
	
}
