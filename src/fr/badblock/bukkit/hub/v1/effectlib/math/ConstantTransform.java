package fr.badblock.bukkit.hub.v1.effectlib.math;

import org.bukkit.configuration.ConfigurationSection;

public class ConstantTransform implements Transform {

	private double value;

	public ConstantTransform() {

	}

	public ConstantTransform(double value) {
		this.value = value;
	}

	@Override
	public double get(double t) {
		return value;
	}

	@Override
	public void load(ConfigurationSection parameters) {
		value = parameters.getDouble("value");
	}
}
