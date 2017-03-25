package fr.badblock.common.shoplinker.plugin.permissions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class PermissionsExManager extends AbstractPermissions {
	
    private final PermissionManager manager = PermissionsEx.getPermissionManager();

    @SuppressWarnings("deprecation")
    @Override
	public String getGroup(String playerName) {
        PermissionUser user = manager.getUser(playerName);
        if(user == null) {
            return null;
        } else {
            return ChatColor.translateAlternateColorCodes('&', user.getGroupsNames()[0]);
        }
    }
    
    @Override
    public String getPrefix(String playerName) {
        PermissionUser user = manager.getUser(playerName);
        if(user == null) {
            return null;
        } else {
            return ChatColor.translateAlternateColorCodes('&', user.getPrefix());
        }
    }

    @Override
    public String getSuffix(String playerName) {
        PermissionUser user = manager.getUser(playerName);
        if(user == null) {
            return null;
        } else {
            return ChatColor.translateAlternateColorCodes('&', user.getSuffix());
        }
    }
    
	@Override
	public boolean hasPermission(String playerName, String node){
    	 PermissionUser user = manager.getUser(playerName);
         if(user == null) {
        	 Player player = Bukkit.getPlayer(playerName);
             return player == null ? false : player.hasPermission(node);
         } else {
        	 return user.has(node);
         }
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public String[] getGroups(String playerName) {
		PermissionUser user = manager.getUser(playerName);
		if(user == null) {
			return null;
		} else {
			return user.getGroupsNames();
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean isInGroup(String playerName, String groupName) {
		PermissionUser user = manager.getUser(playerName);
		if(user == null) {
			return false;
		} else {
			return user.getAllGroups().containsKey(groupName);
		}
	}

	@Override
	public void removeGroup(String playerName, String groupName) {
		PermissionUser user = manager.getUser(playerName);
		if (user != null) user.removeGroup(groupName);
	}

	@Override
	public void addGroup(String playerName, String groupName) {
		PermissionUser user = manager.getUser(playerName);
		if (user != null) user.addGroup(groupName);
	}
	
}
