package fr.badblock.bukkit.hub.v2.disguises;

import org.bukkit.entity.EntityType;

import fr.badblock.gameapi.players.BadblockPlayer;

public class DisguiseSkeleton extends CustomDisguise
{
	
	public DisguiseSkeleton(BadblockPlayer player)
	{
		super(player, EntityType.SKELETON);
	}

	@Override
	public CustomDisguiseEffect getEffect()
	{
		return null;
	}

}
