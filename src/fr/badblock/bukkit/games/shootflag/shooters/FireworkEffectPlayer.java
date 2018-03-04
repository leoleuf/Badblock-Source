package fr.badblock.bukkit.games.shootflag.shooters;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;

public class FireworkEffectPlayer {
	private Method	world_getHandle;
	private Method	nms_world_broadcastEntityEffect;
	private Method	firework_getHandle;

	public FireworkEffectPlayer() {
		super();
		this.world_getHandle = null;
		this.nms_world_broadcastEntityEffect = null;
		this.firework_getHandle = null;
	}

	@SuppressWarnings("unchecked")
	public void playFirework(final World world, final Location loc, final FireworkEffect fe) throws Exception {
		@SuppressWarnings("rawtypes")
		final Firework fw = (Firework) world.spawn(loc, (Class) Firework.class);
		Object nms_world = null;
		Object nms_firework = null;
		if (this.world_getHandle == null) {
			this.world_getHandle = getMethod(world.getClass(), "getHandle");
			this.firework_getHandle = getMethod(fw.getClass(), "getHandle");
		}
		nms_world = this.world_getHandle.invoke(world, (Object[]) null);
		nms_firework = this.firework_getHandle.invoke(fw, (Object[]) null);
		if (this.nms_world_broadcastEntityEffect == null) {
			this.nms_world_broadcastEntityEffect = getMethod(nms_world.getClass(), "broadcastEntityEffect");
		}
		final FireworkMeta data = fw.getFireworkMeta();
		data.clearEffects();
		data.setPower(1);
		data.addEffect(fe);
		fw.setFireworkMeta(data);
		this.nms_world_broadcastEntityEffect.invoke(nms_world, nms_firework, (byte) 17);
		fw.remove();
	}

	public void playFirework(final World world, final Location loc) throws Exception {
		@SuppressWarnings({ "unchecked", "rawtypes" })
		final Firework fw = (Firework) world.spawn(loc, (Class) Firework.class);
		if (this.world_getHandle == null) {
			this.world_getHandle = getMethod(world.getClass(), "getHandle");
			this.firework_getHandle = getMethod(fw.getClass(), "getHandle");
		}
		final Object nms_world = this.world_getHandle.invoke(world, (Object[]) null);
		final Object nms_firework = this.firework_getHandle.invoke(fw, (Object[]) null);
		if (this.nms_world_broadcastEntityEffect == null) {
			this.nms_world_broadcastEntityEffect = getMethod(nms_world.getClass(), "broadcastEntityEffect");
		}
		final FireworkMeta data = fw.getFireworkMeta();
		data.clearEffects();
		data.setPower(1);
		fw.setFireworkMeta(data);
		try {
			this.nms_world_broadcastEntityEffect.invoke(nms_world, nms_firework, (byte) 17);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e2) {
			e2.printStackTrace();
		} catch (InvocationTargetException e3) {
			e3.printStackTrace();
		}
		fw.remove();
	}

	private static Method getMethod(final Class<?> cl, final String method) {
		Method[] methods;
		for (int length = (methods = cl.getMethods()).length, i = 0; i < length; ++i) {
			final Method m = methods[i];
			if (m.getName().equals(method)) {
				return m;
			}
		}
		return null;
	}
}
