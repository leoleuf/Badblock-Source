
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.TurnEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class TurnParticleItem extends ParticleItem {

	public TurnParticleItem() {
		super("turn");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return TurnEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		TurnEffect effect = new TurnEffect(getEffectManager());
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
