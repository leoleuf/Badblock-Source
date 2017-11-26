
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.TraceEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class TraceParticleItem extends ParticleItem {

	public TraceParticleItem() {
		super("trace");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return TraceEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		TraceEffect effect = new TraceEffect(getEffectManager());
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
