package fr.badblock.bukkit.games.shootflag.shooters;

import org.bukkit.scheduler.BukkitTask;

public class TempScheduler {

	public BukkitTask task;
	
	public TempScheduler(BukkitTask task) {
		this.task = task;
	}
	
	public void cancel() {
		task.cancel();
	}
	
}
