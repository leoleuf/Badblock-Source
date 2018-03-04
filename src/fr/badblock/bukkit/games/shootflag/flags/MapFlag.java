package fr.badblock.bukkit.games.shootflag.flags;

import java.util.List;

import fr.badblock.gameapi.configuration.values.MapList;
import fr.badblock.gameapi.configuration.values.MapValue;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/***
 * Représente une Location Bukkit
 * 
 * @author LeLanN
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MapFlag implements MapValue<Flag>
{
	
	public static MapList<MapFlag, Flag> toMapList(List<Flag> objs)
	{
		MapList<MapFlag, Flag> result = new MapList<>();

		for (Flag is : objs)
		{
			result.add(new MapFlag(is));
		}

		return result;
	}
	
	private String name = "world";
	private FakeLocation glass = null;
	private FakeLocation beacon = null;
	private List<FakeLocation> itemframes = null;
	private List<FakeLocation> wools = null;
	/**
	 * Créé une MapLocation depuis une location Bukkit
	 * 
	 * @param location
	 *            La location
	 */
	public MapFlag(Flag flag) {
		this(flag.getName(), flag.getGlass(), flag.getBeacon(), flag.getItemFrames(), flag.getWools());

	}

	@Override
	public Flag getHandle() {
		return new Flag(name, glass, beacon, itemframes, wools);
	}
	
	@Override
	public void postLoad() {
		// round();
	}
}
