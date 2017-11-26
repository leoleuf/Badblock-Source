
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.AnimatedBallEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class AnimatedBallParticleItem extends ParticleItem {

	public AnimatedBallParticleItem() {
		super("animatedBall");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return AnimatedBallEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		AnimatedBallEffect effect = new AnimatedBallEffect(getEffectManager());
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
