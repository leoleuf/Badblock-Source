package fr.badblock.bukkit.hub.v1.utils;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import fr.badblock.bukkit.hub.v1.inventories.join.PlayerCustomInventory;
import fr.badblock.game.core18R3.players.listeners.GameScoreboard;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.i18n.Locale;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_8_R3.PacketPlayOutNamedEntitySpawn;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.PlayerInteractManager;
import net.minecraft.server.v1_8_R3.WorldServer;

public class NPC {

	int entityid;
	UUID uuid;
	Location loc;
	public String rank;
	public List<EntityPlayer> npcs = new ArrayList<>();
	public List<Player> shown = new ArrayList<>();

	public NPC(String rank, Location loc) {
		System.out.println("1NPC(" + rank + ", " + loc + ")");
		this.loc = loc;
		this.rank = rank;
		double rx = loc.getX();
		double ry = loc.getY();
		double rz = loc.getZ();
		this.loc = new Location(loc.getWorld(), rx, ry, rz, loc.getYaw(), loc.getPitch());
	}

	public void show(String name, UUID uuid, Player player, PropertyMap propertyMap) {
		System.out.println("1NPCShow(" + name+ ", " + uuid.toString() + ")");
		MinecraftServer nmsServer = ((CraftServer) Bukkit.getServer()).getServer();
		WorldServer nmsWorld = ((CraftWorld) Bukkit.getWorlds().get(0)).getHandle();

		if (GameScoreboard.groups.get(rank) != null) {
			GameScoreboard.board.getTeam( GameScoreboard.groups.get(rank) ).addEntry(name);
		}
		EntityPlayer npc = new EntityPlayer(nmsServer, nmsWorld, new GameProfile(uuid, name), new PlayerInteractManager(nmsWorld));
		npc.getProfile().getProperties().clear();
		for (Entry<String, Property> entry : propertyMap.entries())
			npc.getProfile().getProperties().put(entry.getKey(), entry.getValue());
		Location loc = this.loc.clone();
		double rx = loc.getX();
		double ry = loc.getY();
		double rz = loc.getZ();
		rx += (-2)+new SecureRandom().nextInt(4);
		rz += (-2)+new SecureRandom().nextInt(4);
		loc = loc.getWorld().getHighestBlockAt((int) rx, (int) rz).getLocation();
		if (loc.getBlock().getType().name().contains("SLAB")) ry -= 0.5;
		rx = loc.getX();
		rx = loc.getZ();
		npc.setLocation(rx, ry, rz, loc.getPitch(), loc.getYaw());
		npc.getBukkitEntity().setItemInHand(PlayerCustomInventory.SELECTOR.getCustomItem().toItemStack(Locale.FRENCH_FRANCE));
		npc.spawnIn(nmsWorld);
		PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		connection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[] { npc }));
		connection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
		npcs.add(npc);
	}

	public void despawn() {
		System.out.println("DespawnNPC2(" + rank + ", " + loc + ")");
		for (Player p : Bukkit.getOnlinePlayers()) {
			final PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
			for (EntityPlayer npc : npcs) {
				connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, npc));
				connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
			}
		}
		for (EntityPlayer npc : npcs) {
			npc.die();
		}
	}

	public void despawn(BadblockPlayer player) {
		System.out.println("DespawnNPC1(" + rank + ", " + loc + ")");
		final PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
		for (EntityPlayer npc : npcs) {
			connection.sendPacket(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, npc));
			connection.sendPacket(new PacketPlayOutEntityDestroy(npc.getId()));
		}
	}

	public Integer getEntityId() {
		return Integer.valueOf(this.entityid);
	}

	public Location getLocation() {
		return loc;
	}

}
