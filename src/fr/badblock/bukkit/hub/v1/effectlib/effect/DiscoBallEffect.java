package fr.badblock.bukkit.hub.v1.effectlib.effect;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.EffectManager;
import fr.badblock.bukkit.hub.v1.effectlib.EffectType;
import fr.badblock.bukkit.hub.v1.effectlib.util.ParticleEffect;
import fr.badblock.bukkit.hub.v1.effectlib.util.RandomUtils;

public class DiscoBallEffect extends Effect {

	public enum Direction {

		BOTH, DOWN, UP;
	}

	/**
	 * Direction of the lines
	 */
	public Direction direction = Direction.DOWN;

	/**
	 * Max number of particles per line
	 */
	public int lineParticles = 100, sphereParticles = 50;
	/**
	 * Min and max sizes of the lines
	 */
	public int max = 15;

	/**
	 * Max number of lines
	 */
	public int maxLines = 7;

	public Color sphereColor = null, lineColor = null;

	/**
	 * Particle of the sphere and of the lines
	 */
	public ParticleEffect sphereParticle = ParticleEffect.FLAME, lineParticle = ParticleEffect.REDSTONE;

	/**
	 * Radius of the sphere
	 */
	public float sphereRadius = .6f;

	public DiscoBallEffect(EffectManager manager) {
		super(manager);
		type = EffectType.REPEATING;
		period = 7;
		iterations = 500;
	}

	@Override
	public void onRun() {
		Location location = getLocation();
		// Lines
		int mL = RandomUtils.random.nextInt(maxLines - 2) + 2;
		for (int m = 0; m < mL * 2; m++) {
			double x = RandomUtils.random.nextInt(max - max * (-1)) + max * (-1);
			double y = RandomUtils.random.nextInt(max - max * (-1)) + max * (-1);
			double z = RandomUtils.random.nextInt(max - max * (-1)) + max * (-1);
			if (direction == Direction.DOWN) {
				y = RandomUtils.random.nextInt(max * 2 - max) + max;
			} else if (direction == Direction.UP) {
				y = RandomUtils.random.nextInt(max * (-1) - max * (-2)) + max * (-2);
			}
			Location target = location.clone().subtract(x, y, z);
			if (target == null) {
				cancel();
				return;
			}
			Vector link = target.toVector().subtract(location.toVector());
			float length = (float) link.length();
			link.normalize();

			float ratio = length / lineParticles;
			Vector v = link.multiply(ratio);
			Location loc = location.clone().subtract(v);
			for (int i = 0; i < lineParticles; i++) {
				loc.add(v);
				display(lineParticle, loc, lineColor);
			}
		}

		// Sphere
		for (int i = 0; i < sphereParticles; i++) {
			Vector vector = RandomUtils.getRandomVector().multiply(sphereRadius);
			location.add(vector);
			display(sphereParticle, location, sphereColor);
			location.subtract(vector);
		}
	}

}
