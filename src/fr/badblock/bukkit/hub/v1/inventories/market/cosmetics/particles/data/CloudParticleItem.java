
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.CloudEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class CloudParticleItem extends ParticleItem {

	public CloudParticleItem() {
		super("cloud");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return CloudEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		CloudEffect effect = new CloudEffect(getEffectManager());
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
