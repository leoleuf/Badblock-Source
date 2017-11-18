package fr.badblock.common.shoplinker.bukkit.utils;

public interface Callback<T> {
	public void done(T result, Throwable error);
}
