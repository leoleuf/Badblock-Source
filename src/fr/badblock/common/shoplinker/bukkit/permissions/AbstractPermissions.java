package fr.badblock.common.shoplinker.bukkit.permissions;

import org.bukkit.Bukkit;

public abstract class AbstractPermissions {
	private static AbstractPermissions permissions = null;

	public static void init(){
		if(permissions != null) return;
		
		if(Bukkit.getServer().getPluginManager().getPlugin("PermissionsEx") != null){
			try {
				permissions = new PermissionsExManager();
			} catch(Exception e){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {}
				Bukkit.shutdown();
			}
		} else if(Bukkit.getServer().getPluginManager().getPlugin("BadblockGameAPI") != null){
			try {
				permissions = new APIPermissionsManager();
			} catch(Exception e){
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {}
				Bukkit.shutdown();
			}
		} else permissions = new NullPermissions();
	}
	public static AbstractPermissions getPermissions(){
		if(permissions == null) init();
		return permissions;
	}
	public abstract String getGroup(String playerName);
	public abstract String getPrefix(String playerName);
	public abstract String getSuffix(String playerName);
	public abstract String[] getGroups(String playerName);
	public abstract boolean	isInGroup(String playerName, String groupName);
	public abstract void removeGroup(String playerName, String groupName);
	public abstract void addGroup(String playerName, String groupName);
	
	public abstract boolean hasPermission(String playerName, String node);
	public abstract void addGroup(String playerName, String groupName, long lifetime);
	
}
