package fr.badblock.bukkit.games.bedwars.configuration.npc;

import fr.badblock.gameapi.configuration.values.MapValue;

public class MapBreakableBlock implements MapValue<BreakableBlock> {

	private String	location;
	private String	material;

	public MapBreakableBlock(String location, String material)
	{
		this.location = location;
		this.material = material;
	}

	@Override
	public BreakableBlock getHandle() {
		return new BreakableBlock(location, material);
	}
	
}
