package fr.badblock.bukkit.hub.v1.effectlib.effect;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.EffectManager;
import fr.badblock.bukkit.hub.v1.effectlib.EffectType;
import fr.badblock.bukkit.hub.v1.effectlib.util.ParticleEffect;
import fr.badblock.bukkit.hub.v1.effectlib.util.RandomUtils;

public class FlameEffect extends Effect {

	public FlameEffect(EffectManager effectManager) {
		super(effectManager);
		type = EffectType.REPEATING;
		period = 1;
		iterations = 600;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRun() {
		Location location = getLocation();
		for (int i = 0; i < 10; i++) {
			Vector v = RandomUtils.getRandomCircleVector().multiply(RandomUtils.random.nextDouble() * 0.6d);
			v.setY(RandomUtils.random.nextFloat() * 1.8);
			location.add(v);
			ParticleEffect.FLAME.display(location, visibleRange);
			location.subtract(v);
		}
	}

}
