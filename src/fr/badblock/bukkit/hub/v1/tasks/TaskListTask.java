package fr.badblock.bukkit.hub.v1.tasks;

import org.bukkit.Bukkit;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.utils.threading.TaskManager;

public class TaskListTask extends CustomTask {
	
	public TaskListTask() {
		super(0, 20 * 60);
	}

	@Override
	public void done() {
		System.out.println("--------------------------------");
		System.out.println("Active workers: " + Bukkit.getScheduler().getActiveWorkers().size());
		System.out.println("Pending tasks: " + Bukkit.getScheduler().getPendingTasks().size());
		System.out.println("TaskManager: " + TaskManager.taskList.size());
		int i = 0;
		for (String task : TaskManager.taskList.keySet())
		{
			i++;
			System.out.println(i + " : " + task);
		}
		System.out.println("--------------------------------");
		GameAPI.getAPI();
	}
	
}
