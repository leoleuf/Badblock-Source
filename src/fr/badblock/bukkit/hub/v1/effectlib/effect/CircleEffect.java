package fr.badblock.bukkit.hub.v1.effectlib.effect;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.EffectManager;
import fr.badblock.bukkit.hub.v1.effectlib.EffectType;
import fr.badblock.bukkit.hub.v1.effectlib.util.ParticleEffect;
import fr.badblock.bukkit.hub.v1.effectlib.util.VectorUtils;

public class CircleEffect extends Effect {

	/*
	 * Turns the cube by this angle each iteration around the x-axis
	 */
	public double angularVelocityX = Math.PI / 200;

	/*
	 * Turns the cube by this angle each iteration around the y-axis
	 */
	public double angularVelocityY = Math.PI / 170;

	/*
	 * Turns the cube by this angle each iteration around the z-axis
	 */
	public double angularVelocityZ = Math.PI / 155;

	/*
	 * Should it rotate?
	 */
	public boolean enableRotation = true;

	/*
	 * ParticleType of spawned particle
	 */
	public ParticleEffect particle = ParticleEffect.VILLAGER_HAPPY;

	/*
	 * Amount of particles per circle
	 */
	public int particles = 20;

	/*
	 * Radius of circle above head
	 */
	public float radius = .4f;

	/*
	 * Current step. Works as a counter
	 */
	protected float step = 0;

	/*
	 * Rotation of the torus.
	 */
	public double xRotation, yRotation, zRotation = 0;

	/*
	 * Subtracts from location if needed
	 */
	public double xSubtract, ySubtract, zSubtract;

	public CircleEffect(EffectManager effectManager) {
		super(effectManager);
		type = EffectType.REPEATING;
		period = 2;
		iterations = 50;
	}

	@Override
	public void onRun() {
		Location location = getLocation();
		location.subtract(xSubtract, ySubtract, zSubtract);
		double inc = (2 * Math.PI) / particles;
		double angle = step * inc;
		Vector v = new Vector();
		v.setX(Math.cos(angle) * radius);
		v.setZ(Math.sin(angle) * radius);
		VectorUtils.rotateVector(v, xRotation, yRotation, zRotation);
		if (enableRotation) {
			VectorUtils.rotateVector(v, angularVelocityX * step, angularVelocityY * step, angularVelocityZ * step);
		}
		display(particle, location.add(v), 0, 30);
		step++;
	}

}
