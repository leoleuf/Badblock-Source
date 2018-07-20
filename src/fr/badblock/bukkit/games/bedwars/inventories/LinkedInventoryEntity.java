package fr.badblock.bukkit.games.bedwars.inventories;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.games.bedwars.configuration.BedWarsMapConfiguration;
import fr.badblock.bukkit.games.bedwars.configuration.breakable.InventoryNPC;
import fr.badblock.bukkit.games.bedwars.configuration.breakable.MapInventoryNPC;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.fakeentities.FakeEntity;
import fr.badblock.gameapi.packets.watchers.WatcherEntity;
import fr.badblock.gameapi.packets.watchers.WatcherLivingEntity;
import fr.badblock.gameapi.packets.watchers.WatcherVillager;
import fr.badblock.gameapi.utils.ConfigUtils;
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
		return fakeEntity;
	}

	public static void load(BedWarsMapConfiguration mapConfiguration) {
		for (MapInventoryNPC mapInventoryNPC : mapConfiguration.getInventoryNPC())
		{
			InventoryNPC inventoryNPC = mapInventoryNPC.getHandle();
			createAndAssign(ConfigUtils.convertStringToLocation(inventoryNPC.location), EntityType.VILLAGER, WatcherVillager.class, inventoryNPC.inventoryName);
			System.out.println("Created a NPC: " + inventoryNPC.location);
		}
	}

}
