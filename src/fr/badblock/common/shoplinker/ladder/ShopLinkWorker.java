package fr.badblock.common.shoplinker.ladder;

import org.bukkit.ChatColor;

import fr.badblock.common.shoplinker.api.objects.ShopData;
import fr.badblock.common.shoplinker.api.objects.ShopType;
import fr.badblock.ladder.api.Ladder;
import fr.badblock.ladder.api.entities.Player;

public class ShopLinkWorker {

	public static void workCommand(ShopData shopData) {
		String playerName = shopData.getPlayerName();
		Player player = Ladder.getInstance().getPlayer(playerName);
		String command = shopData.getObjectName().replace("%0", shopData.getPlayerName());
		ShopLinkerLadder.getConsole().forceCommand(command);
		ShopLinkerLadder.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RESET + "Executed command to " + shopData.getPlayerName() + ".");
		ShopLinkerLadder.getConsole().sendMessage(ChatColor.GOLD + "[ShopLinker] " + ChatColor.RESET + "Command: " + command);
		if (player != null)
			broadcastCommand(player, shopData);
	}

	public static void broadcastCommand(Player player, ShopData shopData) {
		String message = shopData.getDataType().equals(ShopType.BUY) ? ShopLinkerLadder.getInstance().getBoughtMessage() : ShopLinkerLadder.getInstance().getRewardMessage();
		message = message.replace("%0", shopData.getPlayerName()).replace("%1", shopData.getObjectName()).replace("%2", shopData.getDisplayName());
		player.getBukkitServer().broadcast(message);
	}

}
