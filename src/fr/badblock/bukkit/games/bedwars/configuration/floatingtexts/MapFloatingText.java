package fr.badblock.bukkit.games.bedwars.configuration.floatingtexts;

import fr.badblock.gameapi.configuration.values.MapValue;

public class MapFloatingText implements MapValue<FloatingText> {

	private String	location;
	private String	text;

	public MapFloatingText(String location, String text)
	{
		this.location = location;
		this.text = text;
	}

	@Override
	public FloatingText getHandle() {
		return new FloatingText(location, text);
	}
	
}
