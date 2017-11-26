package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.chests.objects;

import java.util.UUID;

import lombok.Data;

@Data
public class CustomChest {

	public UUID			uuid;
	public int 	   		typeId;
	public boolean 		opened;
	
	public CustomChest(int typeId, boolean opened) {
		this.setUuid(UUID.randomUUID());
		this.setTypeId(typeId);
		this.setOpened(opened);
	}
	
}
