package fr.badblock.common.shoplinker.bukkit.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

/**
 * Created by XvPROTETCEDvX / XvBaseballkidvX on 2/3/14.
 */
public class RandomFireWorks {

	private static RandomFireWorks fireWorks = new RandomFireWorks();

	//While in other classes you can now use RandomFirWorks.getManager().<METHODS>();

	public static RandomFireWorks getManager(){
		return fireWorks;
	}
	
	public RandomFireWorks()
	{
		addColors();
		addTypes();
	}

	//Make the arraylists for the colors and types
	ArrayList<Color> colors = new ArrayList<Color>();
	ArrayList<FireworkEffect.Type> types = new ArrayList<FireworkEffect.Type>();

	//MAKE SURE YOU PUT THIS IN YOUR ONENABLE!!!
	public void addColors(){
		//ADD ALL THE COLORS
		colors.add(Color.PURPLE);
		colors.add(Color.RED);
		colors.add(Color.GREEN);
		colors.add(Color.AQUA);
		colors.add(Color.BLUE);
		colors.add(Color.FUCHSIA);
		colors.add(Color.GRAY);
		colors.add(Color.LIME);
		colors.add(Color.MAROON);
		colors.add(Color.YELLOW);
		colors.add(Color.SILVER);
		colors.add(Color.TEAL);
		colors.add(Color.ORANGE);
		colors.add(Color.OLIVE);
		colors.add(Color.NAVY);
		colors.add(Color.BLACK);
		//I think I added them all not sure though
	}

	//MAKE SURE YOU PUT THIS IN YOUR ONENABLE!!!
	public void addTypes(){
		//ADD ALL THE TYPES
		types.add(FireworkEffect.Type.BALL_LARGE);
		//Added all the types
	}

	//Getting a random firework

	public FireworkEffect.Type getRandomType(){
		int size = types.size();
		Random ran = new Random();
		FireworkEffect.Type theType = types.get(ran.nextInt(size));

		return theType;
	}

	//Getting a random COLOR!!!

	public Color getRandomColor(){
		int size = colors.size();
		Random ran = new Random();
		Color color = colors.get(ran.nextInt(size));

		return color;
	}

	public void launchRandomFirework(Location loc){
		Firework fw = loc.getWorld().spawn(loc, Firework.class);
		FireworkMeta fm = fw.getFireworkMeta();
		//Adding all the effects to the firework meta
		fm.addEffects(FireworkEffect.builder().with(getRandomType()).withColor(getRandomColor()).build());
		//set the firework meta to the firework!
		fw.setFireworkMeta(fm);
		
		try
		{
			Object entityFirework = fw.getClass().getMethod("getHandle").invoke(fw);
			Field lifespan = entityFirework.getClass().getDeclaredField("expectedLifespan");
			lifespan.setAccessible(true);
			lifespan.set(entityFirework, 2);
		}
		catch (Exception error)
		{
			error.printStackTrace();
		}
	}
	
}