package fr.badblock.common.shoplinker.bukkit.permissions;

import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;
import net.md_5.bungee.api.ChatColor;

public class APIPermissionsManager extends AbstractPermissions {

	@Override
	public String getGroup(String playerName) {
		BadblockPlayer player = BukkitUtils.getPlayer(playerName);
		if (player == null) return null;
		return ChatColor.translateAlternateColorCodes('&', player.getMainGroup());
	}

	@Override
	public String getPrefix(String playerName) {
		BadblockPlayer player = BukkitUtils.getPlayer(playerName);
		if (player == null) return null;
		return ChatColor.translateAlternateColorCodes('&', player.getTabGroupPrefix().getAsLine(player));
	}

	@Override
	public String getSuffix(String playerName) {
		BadblockPlayer player = BukkitUtils.getPlayer(playerName);
		if (player == null) return null;
		return ChatColor.translateAlternateColorCodes('&', player.getGroupSuffix().getAsLine(player));
	}

	@Override
	public boolean hasPermission(String playerName, String node){
		BadblockPlayer player = BukkitUtils.getPlayer(playerName);
		if (player == null) return false;
		return player.hasPermission(node);
	}

	@Override
	public String[] getGroups(String playerName) {
		BadblockPlayer player = BukkitUtils.getPlayer(playerName);
		if (player == null) return null;
		return player.getAlternateGroups().toArray(new String[] {});
	}

	@Override
	public boolean isInGroup(String playerName, String groupName) {
		BadblockPlayer player = BukkitUtils.getPlayer(playerName);
		if (player == null) return false;
		if (player.getMainGroup().equalsIgnoreCase(groupName) || player.getAlternateGroups().contains(groupName))
			return true;
		return false;
	}

	@Override
	public void removeGroup(String playerName, String groupName) {
		BadblockPlayer player = BukkitUtils.getPlayer(playerName);
		if (player == null) return;
		// no
	}

	@Override
	public void addGroup(String playerName, String groupName) {
		BadblockPlayer player = BukkitUtils.getPlayer(playerName);
		if (player == null) return;
		// no
	}

	@Override
	public void addGroup(String playerName, String groupName, long lifetime) {
		BadblockPlayer player = BukkitUtils.getPlayer(playerName);
		if (player == null) return;
		// no
	}

}
