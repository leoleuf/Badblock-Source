package fr.badblock.common.shoplinker.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.badblock.common.shoplinker.bukkit.ShopLinker;
import fr.badblock.common.shoplinker.bukkit.inventories.InventoriesLoader;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.CustomItemAction;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryActionManager;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.InventoryObject;
import fr.badblock.common.shoplinker.bukkit.inventories.utils.ChatColorUtils;
import net.md_5.bungee.api.ChatColor;

public class OPICommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "[ShopLinker] You must be a player to execute this.");
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "[ShopLinker] Usage: /opi <inventoryName>");
			return true;
		}
		Player player = (Player) sender;
		String inventoryName = args[0];
		InventoryObject inventoryObject = InventoriesLoader.getInventory(inventoryName);
		if (inventoryObject == null) {
			sender.sendMessage(ChatColor.RED + "[ShopLinker] Unknown inventory with name '" + inventoryName + "'.");
			return true;
		}
		String permission = inventoryObject.getPermission();
		if (permission != null && !permission.isEmpty()) {
			if (!player.hasPermission(permission)) {
				String messageKey = "messages.nopermission." + inventoryName; 
				String message = ChatColorUtils.translate(ShopLinker.getInstance().getConfig().getString(messageKey));
				if (message == null || message.isEmpty()) sender.sendMessage(ChatColor.RED + messageKey);
				return true;
			}
		}
		InventoryActionManager.openInventory(player, CustomItemAction.OPEN_INV, inventoryName);
		return true;
	}

}
