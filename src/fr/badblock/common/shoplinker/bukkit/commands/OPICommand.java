package fr.badblock.common.shoplinker.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.badblock.common.shoplinker.bukkit.inventories.BukkitInventories;
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
		BukkitInventories.openInventory(player, inventoryName);
		return true;
	}


}
