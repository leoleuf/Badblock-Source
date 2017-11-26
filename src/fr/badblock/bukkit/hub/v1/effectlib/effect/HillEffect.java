package fr.badblock.bukkit.hub.v1.effectlib.effect;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.EffectManager;
import fr.badblock.bukkit.hub.v1.effectlib.EffectType;
import fr.badblock.bukkit.hub.v1.effectlib.util.ParticleEffect;
import fr.badblock.bukkit.hub.v1.effectlib.util.VectorUtils;

/**
 * Taken from http://en.wikipedia.org/wiki/Torus
 *
 * @author Kevin
 */
public class HillEffect extends Effect {

	/**
	 * Length of the edge
	 */
	public float edgeLength = 6.5f;

	/**
	 * Height of the hill in blocks
	 */
	public float height = 2.5f;

	/**
	 * ParticleType of spawned particle
	 */
	public ParticleEffect particle = ParticleEffect.FLAME;

	/**
	 * Amount of particles per row
	 */
	public float particles = 30;

	/**
	 * Rotation of the Hill
	 */
	public double yRotation = Math.PI / 7;

	public HillEffect(EffectManager effectManager) {
		super(effectManager);
		type = EffectType.REPEATING;
		period = 10;
		iterations = 20;
	}

	@Override
	public void onRun() {
		Location location = getLocation();
		Vector v = new Vector();
		for (int x = 0; x <= particles; x++) {
			double y1 = Math.sin(Math.PI * x / particles);
			for (int z = 0; z <= particles; z++) {
				double y2 = Math.sin(Math.PI * z / particles);
				v.setX(edgeLength * x / particles).setZ(edgeLength * z / particles);
				v.setY(height * y1 * y2);
				VectorUtils.rotateAroundAxisY(v, yRotation);

				display(particle, location.add(v));
				location.subtract(v);
			}
		}
	}
}
