package fr.badblock.bukkit.hub.v2.utils.effects.effect;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import fr.badblock.bukkit.hub.v2.utils.effects.Effect;
import fr.badblock.bukkit.hub.v2.utils.effects.EffectManager;
import fr.badblock.bukkit.hub.v2.utils.effects.EffectType;
import fr.badblock.bukkit.hub.v2.utils.effects.util.MathUtils;
import fr.badblock.bukkit.hub.v2.utils.effects.util.ParticleEffect;
import fr.badblock.bukkit.hub.v2.utils.effects.util.RandomUtils;
import fr.badblock.bukkit.hub.v2.utils.effects.util.VectorUtils;

public class ConeEffect extends Effect {

	/**
	 * Radials per iteration to spawn the next particle (PI / 16)
	 */
	public double angularVelocity = Math.PI / 16;

	/**
	 * Growing per iteration in the length (0.05)
	 */
	public float lengthGrow = .05f;

	/**
	 * ParticleType of spawned particle
	 */
	public ParticleEffect particle = ParticleEffect.FLAME;

	/**
	 * Cone-particles per interation (10)
	 */
	public int particles = 10;

	/**
	 * Conesize in particles per cone
	 */
	public int particlesCone = 180;

	/**
	 * Growth in blocks per iteration on the radius (0.006)
	 */
	public float radiusGrow = 0.006f;

	/**
	 * Randomize every cone on creation (false)
	 */
	public boolean randomize = false;

	/**
	 * Start-angle or rotation of the cone
	 */
	public double rotation = 0;

	/**
	 * Current step. Works as counter
	 */
	protected int step = 0;

	public ConeEffect(EffectManager effectManager) {
		super(effectManager);
		type = EffectType.REPEATING;
		period = 1;
		iterations = 200;
	}

	@Override
	public void onRun() {
		Location location = getLocation();
		for (int x = 0; x < particles; x++) {
			if (step > particlesCone) {
				step = 0;
			}
			if (randomize && step == 0) {
				rotation = RandomUtils.getRandomAngle();
			}
			double angle = step * angularVelocity + rotation;
			float radius = step * radiusGrow;
			float length = step * lengthGrow;
			Vector v = new Vector(Math.cos(angle) * radius, length, Math.sin(angle) * radius);
			VectorUtils.rotateAroundAxisX(v, (location.getPitch() + 90) * MathUtils.degreesToRadians);
			VectorUtils.rotateAroundAxisY(v, -location.getYaw() * MathUtils.degreesToRadians);

			location.add(v);
			display(particle, location);
			location.subtract(v);
			step++;
		}
	}
}
