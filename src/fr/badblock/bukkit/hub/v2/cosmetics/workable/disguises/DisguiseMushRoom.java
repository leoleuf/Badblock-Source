package fr.badblock.bukkit.hub.v2.cosmetics.workable.disguises;

import org.bukkit.entity.EntityType;

import fr.badblock.gameapi.particles.ParticleEffectType;
import fr.badblock.gameapi.players.BadblockPlayer;

public class DisguiseMushRoom extends CustomDisguise
{
	
	public DisguiseMushRoom(BadblockPlayer player)
	{
		super(player);
	}

	@Override
	public CustomDisguiseEffect getEffect()
	{
		return new CustomDisguiseEffect(ParticleEffectType.VILLAGER_ANGRY, 3);
	}

	@Override
	public EntityType getEntityType()
	{
		return EntityType.MUSHROOM_COW;
	}

}
