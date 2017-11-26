
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.data;

import java.io.IOException;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.effect.ImageEffect;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults.ParticleItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class ImageParticleItem extends ParticleItem {

	public ImageParticleItem() {
		super("image");
	}

	@Override
	protected Class<? extends Effect> getEffectClass() {
		return ImageEffect.class;
	}

	@Override
	protected Effect run(BadblockPlayer player, HubPlayer hubPlayer) {
		ImageEffect effect;
		try {
			effect = new ImageEffect(getEffectManager());
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		effect.setEntity(player);
		effect.start();
		return effect;
	}

}
