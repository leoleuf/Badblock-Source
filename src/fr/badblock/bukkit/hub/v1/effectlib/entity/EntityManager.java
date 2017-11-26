package fr.badblock.bukkit.hub.v1.effectlib.entity;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.effectlib.EffectLib;
import fr.badblock.bukkit.hub.v1.effectlib.listener.ItemListener;
import fr.badblock.bukkit.hub.v1.effectlib.util.RandomUtils;
import fr.badblock.gameapi.utils.threading.TaskManager;

public final class EntityManager {

	private boolean disposed = false;
	@SuppressWarnings("unused")
	private final EffectLib effectLib;
	private final Map<Entity, BukkitTask> entities;

	public EntityManager(EffectLib effectLib) {
		this.effectLib = effectLib;
		this.entities = new HashMap<Entity, BukkitTask>();
	}

	public void add(final Entity entity, int duration) {
		if (disposed) {
			throw new IllegalStateException("EffectManager is disposed and not able to accept any effects.");
		}
		BukkitTask task = TaskManager.runTaskLater(new Runnable() {

			@Override
			public void run() {
				remove(entity);
			}

		}, duration);
		entities.put(entity, task);
	}

	public void dispose() {
		disposed = true;
		removeAll();
	}

	public void remove(Entity entity) {
		entities.get(entity).cancel();
		entities.remove(entity);
		entity.remove();
	}

	public void removeAll() {
		for (Map.Entry<Entity, BukkitTask> entry : entities.entrySet()) {
			entry.getKey().remove();
			entry.getValue().cancel();
		}
		entities.clear();
	}

	public Entity spawnEntity(EntityType type, Location loc, int duration) {
		Entity e = loc.getWorld().spawnEntity(loc, type);
		e.setMetadata(ItemListener.ITEM_IDENTIFIER, new FixedMetadataValue(BadBlockHub.getInstance(), 0));
		add(e, duration);
		return e;
	}

	public Item spawnItem(ItemStack is, Location loc, int duration) {
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ItemListener.ITEM_IDENTIFIER + ChatColor.RED + RandomUtils.random.nextInt(10000));
		is.setItemMeta(im);

		Item i = loc.getWorld().dropItem(loc, is);
		i.setMetadata(ItemListener.ITEM_IDENTIFIER, new FixedMetadataValue(BadBlockHub.getInstance(), 0));
		add(i, duration);
		return i;
	}

}
