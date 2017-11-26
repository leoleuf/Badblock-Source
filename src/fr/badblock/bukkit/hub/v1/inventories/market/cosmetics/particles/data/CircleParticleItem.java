
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.CircleEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class CircleParticleItem extends ParticleItem {

	public CircleParticleItem() {
		super("circle");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return CircleEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		CircleEffect effect = new CircleEffect(getEffectManager());
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
