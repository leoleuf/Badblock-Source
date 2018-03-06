package fr.badblock.bukkit.hub.v2.inventories.objects;

import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum ItemAction
{

	INVENTORY_DROP(InventoryAction.DROP_ONE_SLOT), 
	INVENTORY_LEFT_CLICK(InventoryAction.PICKUP_ALL),
	INVENTORY_RIGHT_CLICK(InventoryAction.PICKUP_HALF),
	INVENTORY_WHEEL_CLICK(InventoryAction.NOTHING),
	LEFT_CLICK_AIR,
	LEFT_CLICK_BLOCK,
	RIGHT_CLICK_AIR,
	RIGHT_CLICK_BLOCK;

	public static ItemAction get(Action action)
	{
		for (ItemAction itemAction : values())
		{
			if (itemAction.name().equals(action.name()))
			{
				return itemAction;
			}
		}
		return null;
	}

	public static ItemAction get(InventoryAction inventoryAction)
	{
		for (ItemAction itemAction : values())
		{
			if (itemAction.getAssignedInventoryAction() != null
					&& itemAction.getAssignedInventoryAction().equals(inventoryAction))
			{
				return itemAction;
			}
		}
		return null;
	}

	@Setter
	private InventoryAction assignedInventoryAction;

	ItemAction()
	{
	}

	ItemAction(InventoryAction assignedInventoryAction)
	{
		setAssignedInventoryAction(assignedInventoryAction);
	}

}
