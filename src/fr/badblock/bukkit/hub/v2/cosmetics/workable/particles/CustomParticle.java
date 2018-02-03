package fr.badblock.bukkit.hub.v2.cosmetics.workable.particles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.particles.ParticleEffect;
import fr.badblock.gameapi.particles.ParticleEffectType;
import fr.badblock.gameapi.players.BadblockPlayer;
import lombok.Data;

@Data
public abstract class CustomParticle
{

	Player player;

	public abstract ParticleEffectType getParticleEffectType();
	public abstract void run();
	
	public void playEffect(Location location)
	{
		ParticleEffect effect = (ParticleEffect) GameAPI.getAPI().createParticleEffect(getParticleEffectType());
		effect.setSpeed(0);
		effect.setLongDistance(true);
		effect.setAmount(2);
		for (Player player : Bukkit.getOnlinePlayers())
		{
			BadblockPlayer bplayer = (BadblockPlayer) player;
			bplayer.sendParticle(location, effect);
		}
	}

}
