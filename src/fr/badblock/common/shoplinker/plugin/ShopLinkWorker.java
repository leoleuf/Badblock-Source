package fr.badblock.common.shoplinker.plugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import fr.badblock.common.shoplinker.api.ShopData;
import fr.badblock.common.shoplinker.plugin.permissions.AbstractPermissions;

public class ShopLinkWorker {

	public static void workWithPermissionsEx(ShopData shopData) {
		AbstractPermissions permissionsManager = AbstractPermissions.getPermissions();
		if (permissionsManager.isInGroup(shopData.getPlayerName(), shopData.getObjectName())) {
			ShopLinker.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RESET + shopData.getPlayerName() + " is already in the group named as " + shopData.getObjectName());
			return;
		}
		permissionsManager.addGroup(shopData.getPlayerName(), shopData.getObjectName());
		ShopLinker.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RESET + shopData.getPlayerName() + " is now in the group " + shopData.getObjectName());
		Bukkit.broadcastMessage(ShopLinker.getMessage().replace("%0", shopData.getPlayerName()).replace("%1", shopData.getObjectName()));
	}

}
