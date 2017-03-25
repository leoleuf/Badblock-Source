package fr.badblock.common.shoplinker.plugin.permissions;

public class NullPermissions extends AbstractPermissions {

	@Override
	public String getGroup(String playerName){
		return "";
	}

	@Override
	public String getPrefix(String playerName) {
		return "";
	}

	@Override
	public String getSuffix(String playerName) {
		return "";
	}
	
	@Override
	public boolean hasPermission(String playerName, String node){
		return false;
	}
	
	@Override
	public String[] getGroups(String playerName){
		return null;
	}
	
	@Override
	public boolean isInGroup(String playerName, String groupName) {
		return false;
	}

	@Override
	public void addGroup(String playerName, String groupName) {
	}
	
	@Override
	public void removeGroup(String playerName, String groupName) {
	}
	
}
