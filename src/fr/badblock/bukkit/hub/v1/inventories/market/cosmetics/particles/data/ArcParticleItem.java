
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.ArcEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class ArcParticleItem extends ParticleItem {

	public ArcParticleItem() {
		super("arc");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return ArcEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		ArcEffect effect = new ArcEffect(getEffectManager());
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
