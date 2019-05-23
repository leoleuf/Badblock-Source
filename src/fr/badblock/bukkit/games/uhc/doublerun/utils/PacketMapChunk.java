package fr.badblock.bukkit.games.uhc.doublerun.utils;

import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;

public class PacketMapChunk {
  
    private final net.minecraft.server.v1_8_R3.Chunk chunk;
  
    public PacketMapChunk(final org.bukkit.Chunk chunk) {
        this.chunk = ((CraftChunk)chunk).getHandle();
    }
  
    public final void send(final Player player) {
        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(new PacketPlayOutMapChunk(chunk, true, 20));
    }

}