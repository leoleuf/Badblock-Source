package fr.badblock.common.shoplinker.bukkit.commands;

import java.util.HashSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.badblock.common.shoplinker.bukkit.signs.SignManager;
import fr.badblock.common.shoplinker.bukkit.signs.SignObject;
import net.md_5.bungee.api.ChatColor;

public class SetSignCommand implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "[ShopLinker] You must be a player to execute this.");
			return true;
		}
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "[ShopLinker] Usage: /setsign <inventoryName>");
			return true;
		}
		Player player = (Player) sender;
		List<Block> blocks = player.getLineOfSight(new HashSet<Material>(), 200);
		Block b = null;
		for (Block block : blocks) {
			if (block != null && !block.getType().equals(Material.AIR)) {
				b = block;
				break;
			}
		}
		if (b == null) {
			sender.sendMessage(ChatColor.RED + "[ShopLinker] You must be in front of a sign.");
			return true;
		}
		SignManager signManager = SignManager.getInstance();
		String inventoryName = args[0];
		SignObject signObject = new SignObject(b.getLocation(), inventoryName);	
		signManager.setSign(signObject);
		signManager.save();
		return true;
	}


}
