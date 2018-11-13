package fr.badblock.bukkit.hub.v1.tasks;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.signs.GameSign;
import fr.badblock.bukkit.hub.v1.signs.GameSignManager;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.databases.SQLRequestType;
import fr.badblock.gameapi.utils.general.Callback;
import fr.badblock.gameapi.utils.threading.TaskManager;

public class RequestSignsTask extends CustomTask {

	private static final Type type = new TypeToken<Map<Integer, GameSign>>() {}.getType();
	public static boolean work;

	public RequestSignsTask() {
		super(0, 20, false);
	}

	@Override
	public void done() {
		work();
	}

	public static void work() {
		GameAPI.getAPI().getSqlDatabase().call("SELECT value FROM keyValues WHERE `key` = 'signs'", SQLRequestType.QUERY,
				new Callback<ResultSet>() {

					@Override
					public void done(ResultSet result, Throwable error) {
						try {
							result.next();
							String value = result.getString("value");
							BadBlockHub hub = BadBlockHub.getInstance();
							Gson gson = hub.getGsonExpose();
							Map<Integer, GameSign> updatedMap = gson.fromJson(value, type);
							Map<Integer, GameSign> oldMap = GameSignManager.stockage;
							TaskManager.runTask(new Runnable() {
								@Override
								public void run() {
									// New
									for (Entry<Integer, GameSign> gameSign : updatedMap.entrySet()) {
										if (!oldMap.containsKey(gameSign.getKey())) {
											System.out.println("Coucou new!");
											gameSign.getValue().yop();
											oldMap.put(gameSign.getKey(), gameSign.getValue());
										}
									}
									// Remove old
									for (Entry<Integer, GameSign> gameSign : oldMap.entrySet()) {
										if (!updatedMap.containsKey(gameSign.getKey())) {
											gameSign.getValue().remove();
											oldMap.remove(gameSign.getKey());
										}
									}
									
									work = true;
								}
							});
							result.close(); // don't forget to close it
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}

				});
	}

}
