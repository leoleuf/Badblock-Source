package fr.badblock.bukkit.hub.v2.inventories.actions;

import fr.badblock.bukkit.hub.v2.inventories.objects.CustomItemAction;
import fr.badblock.bukkit.hub.v2.inventories.objects.CustomItemActionType;
import fr.badblock.gameapi.players.BadblockPlayer;

public class ItemActionTeleportServer extends CustomItemAction
{

	@Override
	public void execute(BadblockPlayer player, CustomItemActionType action, String actionData)
	{
		player.sendPlayer(actionData);
	}

}
