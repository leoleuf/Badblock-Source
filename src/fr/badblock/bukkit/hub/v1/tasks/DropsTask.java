package fr.badblock.bukkit.hub.v1.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class DropsTask extends CustomTask {

	public DropsTask() {
		super(0, 20, true);
	}

	@Override
	public void done() {
		work();
	}

	public static void work() {
		for (Entity entity : Bukkit.getWorld("world").getEntities())
		{
			if (entity.getType().equals(EntityType.DROPPED_ITEM))
			{
				entity.remove();
			}
		}
	}

}
