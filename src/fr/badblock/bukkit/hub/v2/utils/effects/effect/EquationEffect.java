package fr.badblock.bukkit.hub.v2.utils.effects.effect;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import fr.badblock.bukkit.hub.v2.utils.effects.Effect;
import fr.badblock.bukkit.hub.v2.utils.effects.EffectManager;
import fr.badblock.bukkit.hub.v2.utils.effects.EffectType;
import fr.badblock.bukkit.hub.v2.utils.effects.math.EquationTransform;
import fr.badblock.bukkit.hub.v2.utils.effects.util.ParticleEffect;
import fr.badblock.bukkit.hub.v2.utils.effects.util.VectorUtils;

public class EquationEffect extends Effect {

	/**
	 * Set this to true to have the effect repeat from t = 0 at each iteration.
	 */
	public boolean cycle = false;

	/**
	 * Whether or not to orient the effect in the direction of the source
	 * Location
	 * 
	 * If this is set to true, the X axis will represent "forward".
	 */
	public boolean orient = true;
	/**
	 * ParticleType of spawned particle
	 */
	public ParticleEffect particle = ParticleEffect.REDSTONE;
	/**
	 * How many steps to take per iteration
	 */
	public int particles = 1;

	/**
	 * How many steps to take per sub-iteration
	 */
	public int particles2 = 0;

	private int step;

	/**
	 * The variable name used in equations to represent major ticks
	 */
	public String variable = "t";
	/**
	 * The variable name used in sub-equations to represent minor ticks
	 */
	public String variable2 = "t2";
	/**
	 * A set of equations that, if set, will be performed in a sub-iteration for
	 * each major iteration.
	 */
	public String x2Equation = null;

	private EquationTransform x2Transform;

	/**
	 * Equations defining the X,Y,Z coordinates over iteration t
	 * 
	 * These equations can make use of most common math functions, including
	 * randomized numbers. Some examples:
	 * 
	 * 4*sin(t) cos(t * rand(-4,5) + 32 tan(t)
	 */
	public String xEquation = "t";

	private EquationTransform xTransform;

	public String y2Equation = null;

	private EquationTransform y2Transform;
	public String yEquation = "0";
	private EquationTransform yTransform;

	public String z2Equation = null;
	private EquationTransform z2Transform;
	public String zEquation = "0";

	private EquationTransform zTransform;

	public EquationEffect(EffectManager effectManager) {
		super(effectManager);
		type = EffectType.REPEATING;
		period = 1;
		iterations = 100;
		step = 0;
	}

	@Override
	public void onRun() {
		if (xTransform == null) {
			xTransform = new EquationTransform(xEquation, variable, "p", "p2");
			yTransform = new EquationTransform(yEquation, variable, "p", "p2");
			zTransform = new EquationTransform(zEquation, variable, "p", "p2");

			if (x2Equation != null && y2Equation != null && z2Equation != null && particles2 > 0) {
				x2Transform = new EquationTransform(x2Equation, variable, variable2, "p", "p2");
				y2Transform = new EquationTransform(y2Equation, variable, variable2, "p", "p2");
				z2Transform = new EquationTransform(z2Equation, variable, variable2, "p", "p2");
			}
		}
		Location location = getLocation();

		boolean hasInnerEquation = (x2Transform != null && y2Transform != null && z2Transform != null);
		for (int i = 0; i < particles; i++) {
			Double xValue = xTransform.get(step, particles);
			Double yValue = yTransform.get(step, particles);
			Double zValue = zTransform.get(step, particles);

			Vector result = new Vector(xValue, yValue, zValue);
			if (orient) {
				result = VectorUtils.rotateVector(result, location);
			}

			Location targetLocation = location.clone();
			targetLocation.add(result);
			if (!hasInnerEquation) {
				display(particle, targetLocation);
			} else {
				for (int j = 0; j < particles2; j++) {
					Double x2Value = x2Transform.get(step, j, particles, particles2);
					Double y2Value = y2Transform.get(step, j, particles, particles2);
					Double z2Value = z2Transform.get(step, j, particles, particles2);

					Location target2Location = targetLocation.clone();
					target2Location.setX(target2Location.getX() + x2Value);
					target2Location.setY(target2Location.getY() + y2Value);
					target2Location.setZ(target2Location.getZ() + z2Value);
					display(particle, target2Location);
				}
			}

			step++;
		}

		if (cycle) {
			step = 0;
		}
	}
}
