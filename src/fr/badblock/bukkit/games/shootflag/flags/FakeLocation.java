package fr.badblock.bukkit.games.shootflag.flags;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FakeLocation
{
	
	private String	worldName;
	private	int		x;
	private	int		y;
	private	int		z;
	
	public Location toLocation()
	{
		return new Location(Bukkit.getWorld(worldName), x, y, z);
	}

}
