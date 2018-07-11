package fr.badblock.bukkit.hub.v1.inventories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.utils.MountManager;
import fr.badblock.gameapi.fakeentities.FakeEntity;
import fr.badblock.gameapi.packets.watchers.WatcherCreeper;
import fr.badblock.gameapi.packets.watchers.WatcherLivingEntity;
import fr.badblock.gameapi.packets.watchers.WatcherSheep;
import fr.badblock.gameapi.packets.watchers.WatcherSkeleton;
import fr.badblock.gameapi.packets.watchers.WatcherVillager;
import fr.badblock.gameapi.utils.ConfigUtils;
import fr.badblock.gameapi.utils.i18n.Locale;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import lombok.Getter;

public class LinkedInventoryEntity {

	@Getter
	private static Map<Location, ServerJoinerVillager> data = new HashMap<>();
	@Getter
	private static List<FakeEntity<?>> fakeEntities = new ArrayList<>();
	@Getter
	private static Map<Location, TranslatableString> floatingTexts = new HashMap<>();

	public static void assign(Location location, ServerJoinerVillager serverJoinerVillager) {
		data.put(location, serverJoinerVillager);
	}

	public static void createAndAssign(Location location, EntityType entityType,
			Class<? extends WatcherLivingEntity> watcherEntity, String displayNameKey, ServerJoinerVillager serverJoinerVillager) {
		FakeEntity<?> fakeEntity = MountManager.spawn(location, entityType, watcherEntity, false, false, false, false, "");
		fakeEntities.add(fakeEntity);
		assign(location, serverJoinerVillager);
		Location loc = location.clone();
		//floatingTexts.put(loc, new TranslatableString(displayNameKey));
		spawnNametag(loc, new TranslatableString(displayNameKey).getAsLine(Locale.FRENCH_FRANCE));
	}
	
	public static void spawnNametag(Location location, String text) {
		ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND); //Spawn the ArmorStand

		as.setGravity(false); //Make sure it doesn't fall
		as.setCanPickupItems(false); //I'm not sure what happens if you leave this as it is, but you might as well disable it
		as.setCustomName(text); //Set this to the text you want
		as.setCustomNameVisible(true); //This makes the text appear no matter if your looking at the entity or not
		as.setVisible(false); //Makes the ArmorStand invisible
    }

	public static void createAndAssign(String node, EntityType entityType,
			Class<? extends WatcherLivingEntity> watcherEntity, String displayNameKey,
			ServerJoinerVillager serverJoinerVillager) {
		createAndAssign(ConfigUtils.getLocation(BadBlockHub.getInstance(), node), entityType, watcherEntity,
				displayNameKey, serverJoinerVillager);
	}

	public static void load() {
		createAndAssign("gamepnj.tower", EntityType.ZOMBIE, WatcherVillager.class, "hub.gamepnj.tower", new ServerJoinerVillager(null, "tower"));
		createAndAssign("gamepnj.towerrun", EntityType.ZOMBIE, WatcherVillager.class, "hub.gamepnj.towerrun", new ServerJoinerVillager(null, "towerE"));
		createAndAssign("gamepnj.rush", EntityType.VILLAGER, WatcherVillager.class, "hub.gamepnj.rush", new ServerJoinerVillager(null, "rush2v2"));
		createAndAssign("gamepnj.spaceballs", EntityType.CREEPER, WatcherVillager.class, "hub.gamepnj.spaceballs", new ServerJoinerVillager(null, "sb4v4"));
		createAndAssign("gamepnj.speeduhc", EntityType.BLAZE, WatcherVillager.class, "hub.gamepnj.speeduhc", new ServerJoinerVillager(null, "speeduhct"));
		createAndAssign("gamepnj.skyb", EntityType.VILLAGER, WatcherVillager.class, "hub.gamepnj.skyb", new ServerJoinerVillager("skyb", null));
		createAndAssign("gamepnj.faction", EntityType.SKELETON, WatcherSkeleton.class, "hub.gamepnj.faction", new ServerJoinerVillager("faction", null));
		createAndAssign("gamepnj.box", EntityType.CREEPER, WatcherCreeper.class, "hub.gamepnj.box", new ServerJoinerVillager("box", null));
		createAndAssign("gamepnj.fb", EntityType.SHEEP, WatcherSheep.class, "hub.gamepnj.fb", new ServerJoinerVillager("fb", null));
	}

}
