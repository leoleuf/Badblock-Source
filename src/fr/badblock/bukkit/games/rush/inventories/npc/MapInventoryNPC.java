package fr.badblock.bukkit.games.rush.inventories.npc;

import fr.badblock.gameapi.configuration.values.MapValue;

public class MapInventoryNPC implements MapValue<InventoryNPC> {

	private String	location;
	private String	inventoryName;

	public MapInventoryNPC(String location, String inventoryName)
	{
		this.location = location;
		this.inventoryName = inventoryName;
	}

	@Override
	public InventoryNPC getHandle() {
		return new InventoryNPC(location, inventoryName);
	}
	
}
