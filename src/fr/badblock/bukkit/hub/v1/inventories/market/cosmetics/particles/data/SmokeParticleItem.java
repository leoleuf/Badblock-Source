
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.SmokeEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class SmokeParticleItem extends ParticleItem {

	public SmokeParticleItem() {
		super("smoke");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return SmokeEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		SmokeEffect effect = new SmokeEffect(getEffectManager());
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
