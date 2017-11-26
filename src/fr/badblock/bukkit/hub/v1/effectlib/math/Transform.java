package fr.badblock.bukkit.hub.v1.effectlib.math;

import org.bukkit.configuration.ConfigurationSection;

public interface Transform {
	public double get(double t);

	public void load(ConfigurationSection parameters);
}
