
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.LoveEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class LoveParticleItem extends ParticleItem {

	public LoveParticleItem() {
		super("love");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return LoveEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		LoveEffect effect = new LoveEffect(getEffectManager());
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
