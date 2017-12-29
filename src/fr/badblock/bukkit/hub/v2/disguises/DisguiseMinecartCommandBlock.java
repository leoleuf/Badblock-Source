package fr.badblock.bukkit.hub.v2.disguises;

import org.bukkit.entity.EntityType;

import fr.badblock.gameapi.players.BadblockPlayer;

public class DisguiseMinecartCommandBlock extends CustomDisguise
{
	
	public DisguiseMinecartCommandBlock(BadblockPlayer player)
	{
		super(player, EntityType.MINECART_COMMAND);
	}

}
