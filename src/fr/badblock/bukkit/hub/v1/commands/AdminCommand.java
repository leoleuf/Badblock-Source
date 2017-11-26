package fr.badblock.bukkit.hub.v1.commands;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;
import fr.badblock.gameapi.utils.ConfigUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.selections.CuboidSelection;

public class AdminCommand extends AbstractCommand {

	public AdminCommand() {
		super("ad", new TranslatableString("hub.admin.help"), GamePermission.ADMIN, GamePermission.ADMIN, GamePermission.ADMIN);
		allowConsole(false);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args) {
		BadblockPlayer player = (BadblockPlayer) sender;
		if (args.length >= 1 && args[0].equalsIgnoreCase("limit")) {
			if (player.getSelection() == null) {
				player.sendTranslatedMessage("commands.noselection");
			} else {
				Location loc1 = player.getSelection().getFirstBound().bukkit(player.getWorld());
				Location loc2 = player.getSelection().getSecondBound().bukkit(player.getWorld());

				loc1.setY(0d);
				loc2.setY(256d);

				ConfigUtils.saveLocation(BadBlockHub.getInstance(), "limit.loc1", loc1);
				ConfigUtils.saveLocation(BadBlockHub.getInstance(), "limit.loc2", loc2);
				BadBlockHub.getInstance().setCuboid(new CuboidSelection(loc1, loc2));
			}

			return true;
		}
		if (args.length >= 2 && args[0].equalsIgnoreCase("setlocation")) {
			ConfigUtils.saveLocation(BadBlockHub.getInstance(), args[1], player.getLocation());
			player.sendTranslatedMessage("hub.admin.setlocation", args[1]);
			// player.sendMessage("§a'" + args[1] + "' set.");
			return true;
		}
		if (args.length >= 1 && args[0].equalsIgnoreCase("bypass")) {
			String type = player.hasAdminMode() ? "disabled" : "activated";

			player.setAdminMode(!player.hasAdminMode());
			player.sendTranslatedMessage("hub.admin." + type);
			return true;
		}

		return false;
	}
}
