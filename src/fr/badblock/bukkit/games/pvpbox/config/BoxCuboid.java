package fr.badblock.bukkit.games.pvpbox.config;

import org.bukkit.Location;

import fr.badblock.gameapi.utils.selections.CuboidSelection;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class BoxCuboid
{

	private transient CuboidSelection cuboidSelection;
	
	private BoxLocation		location1;
	private BoxLocation		location2;
	
	public BoxCuboid(BoxLocation location1, BoxLocation location2)
	{
		setLocation1(location1);
		setLocation2(location2);
		
		this.buildCuboid();
	}
	
	public void buildCuboid()
	{
		if (getLocation1().getBukkitLocation() == null)
		{
			getLocation1().buildLocation();
		}
		
		if (getLocation2().getBukkitLocation() == null)
		{
			getLocation2().buildLocation();
		}
		
		Location loc1 = getLocation1().getBukkitLocation();
		Location loc2 = getLocation2().getBukkitLocation();
		this.setCuboidSelection(new CuboidSelection(loc1, loc2));
	}
	
}
