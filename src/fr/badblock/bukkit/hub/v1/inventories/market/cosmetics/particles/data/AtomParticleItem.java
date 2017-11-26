
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.AtomEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class AtomParticleItem extends ParticleItem {

	public AtomParticleItem() {
		super("atom");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return AtomEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		AtomEffect effect = new AtomEffect(getEffectManager());
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
