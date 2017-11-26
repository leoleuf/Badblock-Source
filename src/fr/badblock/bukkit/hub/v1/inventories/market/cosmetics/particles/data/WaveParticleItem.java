
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.WaveEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class WaveParticleItem extends ParticleItem {

	public WaveParticleItem() {
		super("wave");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return WaveEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		WaveEffect effect = new WaveEffect(getEffectManager());
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
