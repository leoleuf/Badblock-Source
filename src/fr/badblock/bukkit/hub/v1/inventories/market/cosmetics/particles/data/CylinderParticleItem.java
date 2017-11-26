
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.CylinderEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class CylinderParticleItem extends ParticleItem {

	public CylinderParticleItem() {
		super("cylinder");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return CylinderEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		CylinderEffect effect = new CylinderEffect(getEffectManager());
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
