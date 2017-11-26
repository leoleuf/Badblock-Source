package fr.badblock.bukkit.hub.v1.effectlib.effect;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.EffectManager;
import fr.badblock.bukkit.hub.v1.effectlib.EffectType;
import fr.badblock.bukkit.hub.v1.effectlib.util.ParticleEffect;
import fr.badblock.bukkit.hub.v1.effectlib.util.RandomUtils;
import fr.badblock.bukkit.hub.v1.effectlib.util.VectorUtils;

public class AtomEffect extends Effect {

	/**
	 * Velocity of the orbitals
	 */
	public double angularVelocity = Math.PI / 80d;
	public Color colorNucleus = null;

	public Color colorOrbital = null;
	/**
	 * Orbitals around the nucleus
	 */
	public int orbitals = 3;

	/**
	 * ParticleType of the nucleus
	 */
	public ParticleEffect particleNucleus = ParticleEffect.DRIP_WATER;

	/**
	 * ParticleType of orbitals
	 */
	public ParticleEffect particleOrbital = ParticleEffect.DRIP_LAVA;

	/**
	 * Particles to be spawned in the nucleus per iteration
	 */
	public int particlesNucleus = 10;

	/**
	 * Particles to be spawned per orbital per iteration
	 */
	public int particlesOrbital = 10;

	/**
	 * Radius of the atom
	 */
	public double radius = 3;

	/**
	 * Radius of the nucleus as a fraction of the atom-radius
	 */
	public float radiusNucleus = .2f;

	/**
	 * Rotation around the Y-axis
	 */
	public double rotation = 0;

	/**
	 * Internal counter
	 */
	protected int step = 0;

	public AtomEffect(EffectManager effectManager) {
		super(effectManager);
		type = EffectType.REPEATING;
		period = 2;
		iterations = 200;
	}

	@Override
	public void onRun() {
		Location location = getLocation();
		for (int i = 0; i < particlesNucleus; i++) {
			Vector v = RandomUtils.getRandomVector().multiply(radius * radiusNucleus);
			location.add(v);
			display(particleNucleus, location, colorNucleus);
			location.subtract(v);
		}
		for (int i = 0; i < particlesOrbital; i++) {
			double angle = step * angularVelocity;
			for (int j = 0; j < orbitals; j++) {
				double xRotation = (Math.PI / orbitals) * j;
				Vector v = new Vector(Math.cos(angle), Math.sin(angle), 0).multiply(radius);
				VectorUtils.rotateAroundAxisX(v, xRotation);
				VectorUtils.rotateAroundAxisY(v, rotation);
				location.add(v);
				display(particleOrbital, location, colorOrbital);
				location.subtract(v);
			}
			step++;
		}
	}

}
