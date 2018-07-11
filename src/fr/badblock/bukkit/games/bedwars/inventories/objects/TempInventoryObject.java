package fr.badblock.bukkit.games.bedwars.inventories.objects;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Data
public class TempInventoryObject
{

	private CustomItemAction	action;
	private String				actionData;
	
}
