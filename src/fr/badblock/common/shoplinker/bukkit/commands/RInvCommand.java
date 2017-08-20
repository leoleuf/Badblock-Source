package fr.badblock.common.shoplinker.bukkit.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import fr.badblock.common.shoplinker.bukkit.ShopLinker;
import fr.badblock.common.shoplinker.bukkit.inventories.InventoriesLoader;
import net.md_5.bungee.api.ChatColor;

public class RInvCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		InventoriesLoader.reloadInventories(ShopLinker.getInstance());
		sender.sendMessage(ChatColor.GREEN + "[ShopLinker] Reloaded configuration!");
		return true;
	}

}
