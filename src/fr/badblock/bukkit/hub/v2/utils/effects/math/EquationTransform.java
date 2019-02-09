package fr.badblock.bukkit.hub.v2.utils.effects.math;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.configuration.ConfigurationSection;

import fr.badblock.bukkit.hub.v2.utils.effects.exp4j.Expression;
import fr.badblock.bukkit.hub.v2.utils.effects.exp4j.ExpressionBuilder;
import fr.badblock.bukkit.hub.v2.utils.effects.exp4j.function.Function;

public class EquationTransform implements Transform {
	private static Function randFunction;
	private Expression expression;
	private final Set<String> inputVariables;

	public EquationTransform(String equation) {
		this(equation, "t");
	}

	public EquationTransform(String equation, String inputVariable) {
		inputVariables = new HashSet<String>();
		inputVariables.add(inputVariable);
		setEquation(equation);
	}

	public EquationTransform(String equation, String... inputVariables) {
		this.inputVariables = new HashSet<String>();
		for (String inputVariable : inputVariables) {
			this.inputVariables.add(inputVariable);
		}
		setEquation(equation);
	}

	@Override
	public double get(double t) {
		if (expression == null) {
			return 0;
		}
		for (String inputVariable : inputVariables) {
			expression.setVariable(inputVariable, t);
		}
		return expression.evaluate();
	}

	public double get(double... t) {
		if (expression == null) {
			return 0;
		}
		int index = 0;
		for (String inputVariable : inputVariables) {
			expression.setVariable(inputVariable, t[index]);
			if (index < t.length - 1)
				index++;
		}
		return expression.evaluate();
	}

	@Override
	public void load(ConfigurationSection parameters) {
		setEquation(parameters.getString("equation", ""));
	}

	public void setEquation(String equation) {
		try {
			if (randFunction == null) {
				randFunction = new Function("rand", 2) {
					private Random random = new Random();

					@Override
					public double apply(double... args) {
						return random.nextDouble() * (args[1] - args[0]) + args[0];
					}
				};
			}
			expression = new ExpressionBuilder(equation).function(randFunction).variables(inputVariables).build();
		} catch (Exception ex) {
			expression = null;
			org.bukkit.Bukkit.getLogger().log(Level.WARNING, "Error parsing equation " + equation, ex);
		}
	}
}
