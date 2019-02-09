package fr.badblock.bukkit.hub.v2.utils.effects.math;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import fr.badblock.bukkit.hub.v2.utils.effects.util.ConfigUtils;

public class SequenceTransform implements Transform {
	private class Sequence {
		private final double start;
		private final Transform transform;

		public Sequence(ConfigurationSection configuration) {
			this.transform = Transforms.loadTransform(configuration, "transform");
			this.start = configuration.getDouble("start", 0);
		}

		public double get(double t) {
			return transform.get(t);
		}

		public double getStart() {
			return start;
		}
	}

	private List<Sequence> steps;;

	@Override
	public double get(double t) {
		double value = 0;
		for (Sequence step : steps) {
			if (step.getStart() <= t) {
				return step.get(t);
			}
		}
		return value;
	}

	@Override
	public void load(ConfigurationSection parameters) {
		steps = new ArrayList<Sequence>();
		Collection<ConfigurationSection> stepConfigurations = ConfigUtils.getNodeList(parameters, "steps");
		if (stepConfigurations != null) {
			for (ConfigurationSection stepConfig : stepConfigurations) {
				steps.add(new Sequence(stepConfig));
			}
		}
		Collections.reverse(steps);
	}
}
