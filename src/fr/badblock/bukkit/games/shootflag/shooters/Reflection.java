package fr.badblock.bukkit.games.shootflag.shooters;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.Bukkit;

public class Reflection {
	public static String getVersion() {
		final String name = Bukkit.getServer().getClass().getPackage().getName();
		final String version = String.valueOf(name.substring(name.lastIndexOf(46) + 1)) + ".";
		return version;
	}

	public static Class<?> getNMSClass(final String className) {
		final String fullName = "net.minecraft.server." + getVersion() + className;
		Class<?> clazz = null;
		try {
			clazz = Class.forName(fullName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clazz;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Enum<?> getNMSEnum(final String className) {
		final String enumFullName = "net.minecraft.server." + getVersion() + className;
		final String[] x = enumFullName.split("\\.(?=[^\\.]+$)");
		if (x.length == 2) {
			final String enumClassName = x[0];
			final String enumName = x[1];
			try {
				final Class<Enum> cl = (Class<Enum>) Class.forName(enumClassName);
				return Enum.valueOf((Class<Enum>) cl, enumName);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Class<?> getOBCClass(final String className) {
		final String fullName = "org.bukkit.craftbukkit." + getVersion() + className;
		Class<?> clazz = null;
		try {
			clazz = Class.forName(fullName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return clazz;
	}

	public static Object getHandle(final Object obj) {
		try {
			return getMethod(obj.getClass(), "getHandle", (Class<?>[]) new Class[0]).invoke(obj, new Object[0]);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Field getField(final Class<?> clazz, final String name) {
		try {
			final Field field = clazz.getDeclaredField(name);
			field.setAccessible(true);
			return field;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Method getMethod(final Class<?> clazz, final String name, final Class<?>... args) {
		Method[] methods;
		for (int length = (methods = clazz.getMethods()).length, i = 0; i < length; ++i) {
			final Method m = methods[i];
			if (m.getName().equals(name) && (args.length == 0 || ClassListEqual(args, m.getParameterTypes()))) {
				m.setAccessible(true);
				return m;
			}
		}
		return null;
	}

	public static boolean ClassListEqual(final Class<?>[] l1, final Class<?>[] l2) {
		boolean equal = true;
		if (l1.length != l2.length) {
			return false;
		}
		for (int i = 0; i < l1.length; ++i) {
			if (l1[i] != l2[i]) {
				equal = false;
				break;
			}
		}
		return equal;
	}
}
