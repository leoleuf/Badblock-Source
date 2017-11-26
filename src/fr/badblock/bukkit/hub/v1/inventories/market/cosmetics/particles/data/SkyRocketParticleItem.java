
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.SkyRocketEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class SkyRocketParticleItem extends ParticleItem {

	public SkyRocketParticleItem() {
		super("skyrocket");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return SkyRocketEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		SkyRocketEffect effect = new SkyRocketEffect(getEffectManager());
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
