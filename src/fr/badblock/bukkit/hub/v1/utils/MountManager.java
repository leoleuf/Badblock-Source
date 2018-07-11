package fr.badblock.bukkit.hub.v1.utils;

import java.util.Random;

import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Horse;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Skeleton.SkeletonType;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.fakeentities.FakeEntity;
import fr.badblock.gameapi.packets.watchers.WatcherEntity;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.entities.CustomCreature;
import fr.badblock.gameapi.utils.entities.CustomCreature.CreatureBehaviour;
import fr.badblock.gameapi.utils.entities.CustomCreature.CreatureFlag;
import fr.badblock.gameapi.utils.entities.CustomCreature.CreatureGenericAttribute;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class MountManager {

	public final static int maxSlimeSize = 5;
	public static double mountSpeed = 0.9D;

	private static boolean canSummonMount(Location location) {
		org.bukkit.World world = location.getWorld();
		Block block = location.getBlock();
		for (int x = location.getBlockX() - 1; x <= location.getBlockX() + 1; x++) {
			for (int y = location.getBlockY(); y <= location.getBlockY() + 1; y++) {
				for (int z = location.getBlockZ() - 1; z <= location.getBlockZ() + 1; z++) {
					block = world.getBlockAt(x, y, z);
					if (block.getType().isSolid())
						return false;
				}
			}
		}
		return true;
	}

	private static boolean make(LivingEntity entity, BadblockPlayer player, boolean b) {
		if (player.isInsideVehicle())
			player.getVehicle().eject();

		if (entity instanceof Ageable)
			((Ageable) entity).setAge(0);

		entity.setPassenger(player);
		return true;
	}

	/*
	 * public static LivingEntity launchEntity(Location location, EntityType
	 * entityType) { LivingEntity mount = spawn(location, entityType, true,
	 * false, false, false, mountSpeed);
	 * 
	 * if(mount instanceof Ageable) ((Ageable) mount).setAge(0);
	 * mount.setMaxHealth(maxHealth);
	 * 
	 * return mount; }
	 */

	public static void makeMagmaCube(BadblockPlayer player, int size, boolean b) {
		if (prevent(player))
			return;
		MagmaCube bEntity = (MagmaCube) spawn(player, player.getLocation(), EntityType.MAGMA_CUBE, true, false, false,
				b, mountSpeed);
		bEntity.setCanPickupItems(true);
		bEntity.setSize(size);

		make(bEntity, player, b);
	}

	public static void makeSlime(BadblockPlayer player, int size, boolean b) {
		if (prevent(player))
			return;
		MagmaCube bEntity = (MagmaCube) spawn(player, player.getLocation(), EntityType.SLIME, true, false, false, b,
				mountSpeed);
		bEntity.setCanPickupItems(true);
		bEntity.setSize(size);

		make(bEntity, player, b);
	}

	private static boolean prevent(BadblockPlayer player) {
		if (!canSummonMount(player.getLocation())) {
			player.sendTranslatedMessage("hub.mount.cantbeusedhere");
			return true;
		}
		return false;
	}

	public static void rideBabyZombie(BadblockPlayer player, boolean b) {
		if (prevent(player))
			return;
		Zombie bEntity = (Zombie) spawn(player, player.getLocation(), EntityType.ZOMBIE, true, false, false, b,
				mountSpeed);
		bEntity.setCanPickupItems(true);
		bEntity.setBaby(true);

		make(bEntity, player, b);
	}

	public static boolean rideEntity(BadblockPlayer player, EntityType creature, boolean b, boolean fly,
			double mountSpeed, boolean tameable, boolean mounted) {
		if (prevent(player))
			return false;
		HubPlayer hubPlayer = HubPlayer.get(player);
		if (hubPlayer.mountEntity != null && hubPlayer.mountEntity.isValid())
			hubPlayer.mountEntity.remove();
		LivingEntity bEntity = spawn(player, player.getLocation(), creature, true, true, fly, b, mountSpeed);
		hubPlayer.setMountEntity(bEntity);
		hubPlayer.mountEntity = bEntity;
		if (bEntity instanceof Ageable)
			((Ageable) bEntity).setAge(0);

		if (mounted) {
			if (tameable) {
				Tameable tameabled = (Tameable) bEntity;
				tameabled.setTamed(true);
				tameabled.setOwner(player);
				if (bEntity instanceof Horse) {
					((Horse) bEntity).getInventory().setSaddle(new ItemStack(Material.SADDLE));
				}
			}
			/*
			 * if(bEntity == null){ return false; } Si null, NPE avant :p
			 */

			bEntity.setCanPickupItems(true);
			return make(bEntity, player, b);
		} else {
			/*
			 * if(bEntity == null){ return false; } Si null, NPE avant :p
			 */
			return true;
		}
	}

	public static void rideHorse(BadblockPlayer player, boolean b) {
		if (prevent(player))
			return;
		Horse bEntity = (Horse) spawn(player, player.getLocation(), EntityType.HORSE, true, true, true, b, 0.3D);
		bEntity.setTamed(true);
		bEntity.setOwner(player);

		bEntity.getInventory().setSaddle(new ItemStack(Material.SADDLE));
		make(bEntity, player, b);
	}

	public static void ridePig(BadblockPlayer player, boolean b) {
		if (prevent(player))
			return;
		Pig bEntity = (Pig) spawn(player, player.getLocation(), EntityType.PIG, true, false, false, b, mountSpeed);
		bEntity.setSaddle(true);
		bEntity.setCustomName("Dinnerbone");
		bEntity.setCustomNameVisible(false);

		make(bEntity, player, b);
	}

	public static void rideSheep(BadblockPlayer player, boolean b) {
		if (prevent(player))
			return;
		Sheep bEntity = (Sheep) spawn(player, player.getLocation(), EntityType.SHEEP, true, false, false, b,
				mountSpeed);
		bEntity.setAdult();
		bEntity.setCustomName(player.getName());
		bEntity.setCustomNameVisible(true);

		make(bEntity, player, b);
	}

	public static void rideSheepDisco(BadblockPlayer player, boolean b) {
		if (prevent(player))
			return;
		Sheep bEntity = (Sheep) spawn(player, player.getLocation(), EntityType.SHEEP, true, false, false, b,
				mountSpeed);
		bEntity.setAdult();
		bEntity.setCustomName("jeb_");
		bEntity.setCustomNameVisible(false);

		make(bEntity, player, b);
	}

	public static void rideWitherSkeleton(BadblockPlayer player, boolean b) {
		if (prevent(player))
			return;
		Skeleton bEntity = (Skeleton) spawn(player, player.getLocation(), EntityType.SKELETON, true, false, false, b,
				mountSpeed);
		bEntity.setCanPickupItems(true);
		bEntity.setSkeletonType(SkeletonType.WITHER);
		bEntity.getEquipment().setItemInHand(new ItemStack(Material.STONE_SWORD, 1));

		make(bEntity, player, b);
	}

	public static void rideWolf(BadblockPlayer player, boolean b) {
		if (prevent(player))
			return;
		Wolf bEntity = (Wolf) spawn(player, player.getLocation(), EntityType.WOLF, true, false, false, b, mountSpeed);
		bEntity.setCanPickupItems(true);
		bEntity.setOwner(player);
		bEntity.setCollarColor(DyeColor.values()[new Random().nextInt(16)]);

		make(bEntity, player, b);
	}

	public static boolean shouldDie(final LivingEntity mount, BadblockPlayer rider) {
		if (mount.getPassenger() == null || !(mount.getPassenger() instanceof HumanEntity)) {
			if (rider == null || !rider.isOnline() || HubPlayer.get(rider).teleportMount < System.currentTimeMillis()) {
				mount.remove();
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	public static LivingEntity spawn(BadblockPlayer player, Location location, EntityType type, boolean movable,
			boolean rideable, boolean canFly, boolean inversed, double mountSpeed) {
		CustomCreature custom = GameAPI.getAPI().spawnCustomEntity(location.clone().add(0, 0.5d, 0), type);
		if (custom == null)
			return null;
		if (player != null) {
			HubPlayer hubPlayer = HubPlayer.get(player);
			hubPlayer.setLastCreature(custom);
		}

		custom.removeCreatureFlag(CreatureFlag.AGRESSIVE);
		custom.addCreatureFlags(CreatureFlag.FIREPROOF, CreatureFlag.INVINCIBLE, CreatureFlag.RIDEABLE);

		custom.setCreatureBehaviour(canFly ? CreatureBehaviour.FLYING : CreatureBehaviour.NORMAL);
		custom.setCreatureGenericAttribute(CreatureGenericAttribute.SPEED, mountSpeed);

		Entity entity = custom.getBukkit();

		if (inversed) {
			entity.setCustomName("Dinnerbone");
			entity.setCustomNameVisible(false);
		}
		return (LivingEntity) entity;
	}

	public static <T extends WatcherEntity> FakeEntity<T> spawn(Location location, EntityType type, Class<T> clazz,
			boolean movable, boolean rideable, boolean canFly, boolean inversed, TranslatableString customName) {
		FakeEntity<T> fakeEntity = GameAPI.getAPI().spawnFakeLivingEntity(location, type, clazz);
		if (customName != null) {
			fakeEntity.getWatchers().setCustomName(customName);
			fakeEntity.getWatchers().setCustomNameVisible(true);
		} else if (inversed) {
			fakeEntity.getWatchers().setCustomName("Dinnerbone");
			fakeEntity.getWatchers().setCustomNameVisible(false);
		}
		if (fakeEntity == null)
			return null;
		fakeEntity.getWatchers().setOnFire(false);
		return fakeEntity;
	}

	public static <T extends WatcherEntity> FakeEntity<T> spawn(Location location, EntityType type, Class<T> clazz,
			boolean movable, boolean rideable, boolean canFly, boolean inversed, String customName) {
		FakeEntity<T> fakeEntity = GameAPI.getAPI().spawnFakeLivingEntity(location, type, clazz);
		if (customName != null && !customName.isEmpty()) {
			fakeEntity.getWatchers().setCustomName(customName);
			fakeEntity.getWatchers().setCustomNameVisible(true);
		} else if (inversed) {
			fakeEntity.getWatchers().setCustomName("Dinnerbone");
			fakeEntity.getWatchers().setCustomNameVisible(false);
		}
		if (fakeEntity == null)
			return null;
		fakeEntity.getWatchers().setOnFire(false);
		return fakeEntity;
	}

}