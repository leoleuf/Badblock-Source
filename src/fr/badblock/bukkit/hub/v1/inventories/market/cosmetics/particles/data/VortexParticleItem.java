
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.VortexEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class VortexParticleItem extends ParticleItem {

	public VortexParticleItem() {
		super("vortex");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return VortexEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		VortexEffect effect = new VortexEffect(getEffectManager());
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
