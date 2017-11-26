
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.GridEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class GridParticleItem extends ParticleItem {

	public GridParticleItem() {
		super("grid");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return GridEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		GridEffect effect = new GridEffect(getEffectManager());
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
