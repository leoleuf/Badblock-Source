package fr.badblock.bukkit.games.bedwars.animations;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class ItemSpawner extends BukkitRunnable {
    private Location l;
    int degree = 0;
    private ArmorStand as1;
    private ArmorStand as2;

    public ItemSpawner(Location l) {
        this.l = l;
        l.setY(l.getY() - 1.7d);
        as1 = l.getWorld().spawn(l, ArmorStand.class);
        as2 =  l.getWorld().spawn(l, ArmorStand.class);
        as1.setHelmet(new ItemStack(Material.DIAMOND_BLOCK));
        as2.setHelmet(new ItemStack(Material.DIAMOND_BLOCK));
        Location load = l.clone();
        load.setY(9999);
        as1.teleport(load);
        as2.teleport(load);
        as1.setVisible(false);
        as2.setVisible(false);
    }

    @Override
    public void run() {
        as1.setHelmet(new ItemStack(Material.DIAMOND_BLOCK));
        as2.setHelmet(new ItemStack(Material.DIAMOND_BLOCK));
        double radians = Math.toRadians(degree);
        double x = Math.cos(radians);
        double z = Math.sin(radians);
        l.add(x,0,z);
        as1.teleport(l);
        l.subtract(x,0,z);
        radians = Math.toRadians(degree + 180);
        x = Math.cos(radians);
        z = Math.sin(radians);
        l.add(x,0,z);
        as2.teleport(l);
        l.subtract(x,0,z);
        degree += 10;
    }
}
