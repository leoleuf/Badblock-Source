package fr.badblock.bukkit.hub.v2.disguises;

import org.bukkit.entity.EntityType;

import fr.badblock.gameapi.particles.ParticleEffectType;
import fr.badblock.gameapi.players.BadblockPlayer;

public class DisguiseBlaze extends CustomDisguise
{
	
	public DisguiseBlaze(BadblockPlayer player)
	{
		super(player, EntityType.BLAZE);
	}

	@Override
	public CustomDisguiseEffect getEffect() {
		return new CustomDisguiseEffect(ParticleEffectType.LAVA, 3);
	}

}
