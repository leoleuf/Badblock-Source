package fr.badblock.spaceballs.rockets;

import java.util.Random;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.badblock.gameapi.utils.i18n.Locale;
import fr.badblock.gameapi.utils.itemstack.ItemStackUtils;
import lombok.Getter;

@Getter
public enum Rockets {
	TELEPORT(new RocketTeleport(), 20),
	TELEPORT_HERE(new RocketTeleportHere(), 5),
	SWITCH(new RocketSwitch(), 10),
	//ANVIL_RAIN(new RocketTeleport(), 15),
	PIGMEN(new RocketPigmen(), 5),
	DIAMONDS_ERASE(new RocketDiamondsErase(), 2),
	EXPLOSIVE(new RocketExplosive(), 15),
	DESTRUCTIVE(new RocketDestructive(), 2),
	LIGHTNING(new RocketLightning(), 6);

	private static final int 	maxProbability;
	private static final Random random = new Random();

	static {
		int prob  = 0;
		int value = -1000;

		for(Rockets rocket : values()) {
			rocket.id =  value--;
			prob 	  += rocket.probability;
		}

		maxProbability = prob;
	}

	private final Rocket rocketHandler;
	private final int    probability;
	private 	  int    id;

	Rockets(Rocket rocketHandler, int probability){
		this.rocketHandler = rocketHandler;
		this.probability   = probability;
	}


	public static ItemStack createRandomRocket(){
		return createRocket(getRandomRocket(), 1);
	}
	
	public static ItemStack createRocket(Rockets rocket, int count){
		ItemStack item = new ItemStack(Material.FIREWORK, count);
		rocket.getRocketHandler().applyI18N(item, Locale.FRENCH_FRANCE, rocket.id);

		return item;
	}

	public static Rockets getRandomRocket(){
		int value = random.nextInt(maxProbability);

		
		for(Rockets rocket : values()){
			int prob = rocket.getProbability();
			
			if(prob > value)
				return rocket;
			else value -= prob;
		}
		
		return Rockets.TELEPORT; // au cas o�
	}

	public static ItemStack changeLanguage(ItemStack is, Locale locale){
		Rockets type = matchRocket(is);

		if(type != null)
			type.getRocketHandler().applyI18N(is, locale, type.id);

		return is;
	}

	public static Rockets matchRocket(ItemStack is){
		if(!ItemStackUtils.hasDisplayname(is))
			return null;

		if(is.getType() != Material.FIREWORK)
			return null;

		int id = ItemStackUtils.decodeItemId(is);

		if(id > -1000)
			return null;

		for(Rockets rocket : values())
			if(rocket.id == id)
				return rocket;

		return null;
	}
}
