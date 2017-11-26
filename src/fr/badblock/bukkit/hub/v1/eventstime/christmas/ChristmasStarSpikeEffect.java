package fr.badblock.bukkit.hub.v1.eventstime.christmas;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.EffectManager;
import fr.badblock.bukkit.hub.v1.effectlib.EffectType;
import fr.badblock.bukkit.hub.v1.effectlib.util.MathUtils;
import fr.badblock.bukkit.hub.v1.effectlib.util.ParticleEffect;
import fr.badblock.bukkit.hub.v1.effectlib.util.RandomUtils;
import fr.badblock.bukkit.hub.v1.effectlib.util.VectorUtils;

public class ChristmasStarSpikeEffect extends Effect {

	/**
	 * Inner radius of the star. (0.5)
	 */
	public float innerRadius = 0.5f;

	/**
	 * Particles to create the star
	 */
	public ParticleEffect particle = ParticleEffect.SNOW_SHOVEL;

	/**
	 * Particles per spike
	 */
	public int particles = 50;

	/**
	 * Height of the spikes in blocks
	 */
	public float spikeHeight = 3.5f;

	/**
	 * Half amount of spikes. Creation is only done half and then mirrored.
	 */
	public int spikesHalf = 3;

	public ChristmasStarSpikeEffect(EffectManager effectManager) {
		super(effectManager);
		type = EffectType.REPEATING;
		iterations = -1;
	}

	@Override
	public void onRun() {
		Location location = getLocation();
		float radius = 3 * innerRadius / MathUtils.SQRT_3;
		for (int i = 0; i < spikesHalf * 2; i++) {
			double xRotation = i * Math.PI / spikesHalf;
			for (int x = 0; x < particles; x++) {
				double angle = 2 * Math.PI * x / particles;
				float height = RandomUtils.random.nextFloat() * spikeHeight;
				Vector v = new Vector(Math.cos(angle), 0, Math.sin(angle));
				v.multiply((spikeHeight - height) * radius / spikeHeight);
				v.setY(innerRadius + height);
				VectorUtils.rotateAroundAxisX(v, xRotation);
				location.add(v);
				display(particle, location);
				location.subtract(v);
				VectorUtils.rotateAroundAxisX(v, Math.PI);
				VectorUtils.rotateAroundAxisY(v, Math.PI / 2);
				location.add(v);
				display(particle, location);
				location.subtract(v);
			}
		}
	}

}
