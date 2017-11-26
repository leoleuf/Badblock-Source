
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.IconEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class IconParticleItem extends ParticleItem {

	public IconParticleItem() {
		super("icon");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return IconEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		IconEffect effect = new IconEffect(getEffectManager());
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
