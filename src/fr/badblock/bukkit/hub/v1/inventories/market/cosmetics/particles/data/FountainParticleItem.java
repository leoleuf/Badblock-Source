
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.FountainEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class FountainParticleItem extends ParticleItem {

	public FountainParticleItem() {
		super("fountain");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return FountainEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		FountainEffect effect = new FountainEffect(getEffectManager());
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
