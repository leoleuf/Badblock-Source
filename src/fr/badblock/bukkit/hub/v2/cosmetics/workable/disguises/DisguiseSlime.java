package fr.badblock.bukkit.hub.v2.cosmetics.workable.disguises;

import org.bukkit.entity.EntityType;

import fr.badblock.gameapi.particles.ParticleEffectType;
import fr.badblock.gameapi.players.BadblockPlayer;

public class DisguiseSlime extends CustomDisguise
{
	
	public DisguiseSlime(BadblockPlayer player)
	{
		super(player);
	}

	@Override
	public CustomDisguiseEffect getEffect()
	{
		return new CustomDisguiseEffect(ParticleEffectType.SLIME, 5);
	}

	@Override
	public EntityType getEntityType()
	{
		return EntityType.SLIME;
	}

}