package fr.badblock.bukkit.hub.commands;

import java.util.Iterator;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import fr.badblock.bukkit.hub.BadBlockHub;
import fr.badblock.bukkit.hub.signs.GameSign;
import fr.badblock.bukkit.hub.signs.GameSignManager;
import fr.badblock.game.core18R3.players.GameBadblockPlayer;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.databases.SQLRequestType;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;
import fr.badblock.gameapi.utils.i18n.I18n;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import net.md_5.bungee.api.ChatColor;

public class SignCommand extends AbstractCommand {

	public SignCommand() {
		super("sign", new TranslatableString("hub.sign.help"), GamePermission.ADMIN, GamePermission.ADMIN, GamePermission.ADMIN);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args) {
		if (args.length == 0)
			return false;
		I18n i18n = GameAPI.i18n();
		if (args[0].equalsIgnoreCase("list")) {
			Iterator<Entry<Integer, GameSign>> signData = GameSignManager.stockage.entrySet().iterator();
			if (!signData.hasNext()) {
				i18n.sendMessage(sender, "hub.sign.list_no_one");
				return true;
			}
			while (signData.hasNext()) {
				Entry<Integer, GameSign> sign = signData.next();
				i18n.sendMessage(sender, "hub.sign.list_each", sign.getKey(), sign.getValue().getDisplayGameName() + " : " + sign.getValue().getDisplayModeName());
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("add")) {
			if (!(sender instanceof BadblockPlayer)) {
				sender.sendMessage(ChatColor.RED + "This command is only for players.");
				return true;
			}
			GameBadblockPlayer player = (GameBadblockPlayer) sender;
			// /sign add <internalname> <displaygamename> <displaymodename>
			// /sign add tower2v2 Tower 2v2
			if (args.length >= 5) {
				String internalName = args[1];
				String displayGameName = args[2];
				String displayModeName = args[3];
				String direction = args[4];
				if (player.getSecondVector() == null)
				{
					i18n.sendMessage(sender, "hub.sign.usewand");
					return true;
				}
				int id = 0;
				while (GameSignManager.stockage.containsKey(id)) {
					id++;
				}
				Location location = new Location(player.getWorld(), player.getSecondVector().getX(), player.getSecondVector().getY(), player.getSecondVector().getZ());
				GameSign gameSign = new GameSign(internalName, displayGameName, displayModeName, location, direction);
				GameSignManager.stockage.put(id, gameSign);
				GameAPI.getAPI().getSqlDatabase().call("UPDATE keyValues SET value = '"
						+ BadBlockHub.getInstance().getGsonExpose().toJson(GameSignManager.stockage) + "' WHERE `key` = 'signs'",
						SQLRequestType.UPDATE);
				i18n.sendMessage(sender, "hub.sign.added_sign", id);
			}
			i18n.sendMessage(sender, "hub.sign.help_add");
			return true;
		}
		if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("rm")) {
			if (args.length >= 2) {
				String intId = args[1];
				int id = -1;
				try {
					id = Integer.parseInt(intId);
				} catch (Exception error) {
					i18n.sendMessage(sender, "hub.sign.idmustbeinteger");
					return true;
				}
				if (GameSignManager.stockage.containsKey(id)) {
					GameSignManager.stockage.get(id).remove();
					GameSignManager.stockage.remove(id);
				}
				GameAPI.getAPI().getSqlDatabase().call("UPDATE keyValues SET value = '"
						+ BadBlockHub.getInstance().getGsonExpose().toJson(GameSignManager.stockage) + "' WHERE `key` = 'signs'",
						SQLRequestType.UPDATE);
				i18n.sendMessage(sender, "hub.sign.removed_sign", id);
				return true;
			}
			i18n.sendMessage(sender, "hub.sign.help_remove");
			return true;
		}
		return false;
	}

}
