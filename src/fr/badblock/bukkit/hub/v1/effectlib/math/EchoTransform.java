package fr.badblock.bukkit.hub.v1.effectlib.math;

import org.bukkit.configuration.ConfigurationSection;

public class EchoTransform implements Transform {
	@Override
	public double get(double t) {
		return t;
	}

	@Override
	public void load(ConfigurationSection parameters) {
	}
}
