package fr.badblock.bukkit.hub.v2.cosmetics.workable.pets;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Villager;

public class PetsVillager extends CustomPet
{
	
	public PetsVillager()
	{
		super(Villager.class, true);
	}

	@Override
	public void onSpawn(LivingEntity livingEntity)
	{
		// Nothing there yet
	}

}