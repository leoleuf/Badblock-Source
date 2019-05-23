package fr.badblock.bukkit.games.rush.inventories;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.games.rush.configuration.RushMapConfiguration;
import fr.badblock.bukkit.games.rush.inventories.npc.InventoryNPC;
import fr.badblock.bukkit.games.rush.inventories.npc.MapInventoryNPC;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.fakeentities.FakeEntity;
import fr.badblock.gameapi.fakeentities.FakeEntity.Visibility;
import fr.badblock.gameapi.packets.watchers.WatcherEntity;
import fr.badblock.gameapi.packets.watchers.WatcherLivingEntity;
import fr.badblock.gameapi.packets.watchers.WatcherVillager;
import lombok.Getter;

public class LinkedInventoryEntity {

	@Getter
	public static Map<FakeEntity<?>, String> fakeEntities = new HashMap<>();

	public static void createAndAssign(Location location, EntityType entityType,
			Class<? extends WatcherLivingEntity> watcherEntity, String inventoryName) {
		FakeEntity<?> fakeEntity = spawn(location, entityType, watcherEntity, false, false, false, false);
		fakeEntities.put(fakeEntity, inventoryName);
	}

	public static <T extends WatcherEntity> FakeEntity<T> spawn(Location location, EntityType type, Class<T> clazz,
			boolean movable, boolean rideable, boolean canFly, boolean inversed) {
		FakeEntity<T> fakeEntity = GameAPI.getAPI().spawnFakeLivingEntity(location, type, clazz);

		if (fakeEntity == null)
			return null;
		fakeEntity.getWatchers().setOnFire(false);
		fakeEntity.setVisibility(Visibility.SERVER);
		fakeEntity.teleport(location);
		fakeEntity.move(location);
		fakeEntity.setHeadYaw(location.getYaw());
		return fakeEntity;
	}

	public static void load(RushMapConfiguration mapConfiguration) {
		for (MapInventoryNPC mapInventoryNPC : mapConfiguration.getInventoryNPC())
		{
			InventoryNPC inventoryNPC = mapInventoryNPC.getHandle();
			Location loc = convertStringToLocation(inventoryNPC.location);
			System.out.println("Create and assign : " + loc.toString());
			createAndAssign(loc, EntityType.VILLAGER, WatcherVillager.class, inventoryNPC.inventoryName);
		}
	}
	
	public static Location convertStringToLocation(String string) {
		if (string == null)
			return null;
		String[] wxyzPitchYaw = string.split(",");
		World w = Bukkit.getWorld(wxyzPitchYaw[0]);
		double x = Double.parseDouble(wxyzPitchYaw[1]);
		double y = Double.parseDouble(wxyzPitchYaw[2]);
		double z = Double.parseDouble(wxyzPitchYaw[3]);
		float pitch = Float.parseFloat(wxyzPitchYaw[5]);
		float yaw = Float.parseFloat(wxyzPitchYaw[4]);
		Location location = new Location(w, x, y, z);
		location.setPitch(pitch);
		location.setYaw(yaw);
		return location;
	}

}
