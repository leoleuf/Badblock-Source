package fr.badblock.bukkit.hub.v1.commands;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.tasks.RequestNPCTask;
import fr.badblock.bukkit.hub.v1.utils.pnj.NPCData;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.databases.SQLRequestType;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;
import fr.badblock.gameapi.utils.ConfigUtils;
import fr.badblock.gameapi.utils.general.StringUtils;
import fr.badblock.gameapi.utils.i18n.I18n;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import net.md_5.bungee.api.ChatColor;

public class NPCCommand extends AbstractCommand {

	public NPCCommand() {
		super("npc", new TranslatableString("hub.npc.help"), GamePermission.ADMIN, GamePermission.ADMIN, GamePermission.ADMIN);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args) {
		if (args.length == 0)
			return false;
		I18n i18n = GameAPI.i18n();
		if (args[0].equalsIgnoreCase("list")) {
			Iterator<Entry<Integer, NPCData>> npcDatas = NPCData.stockage.entrySet().iterator();
			if (!npcDatas.hasNext()) {
				i18n.sendMessage(sender, "hub.npc.list_no_one");
				return true;
			}
			while (npcDatas.hasNext()) {
				Entry<Integer, NPCData> npcData = npcDatas.next();
				i18n.sendMessage(sender, "hub.npc.list_each", npcData.getKey(), npcData.getValue().getDisplayName());
			}
			return true;
		}
		if (args[0].equalsIgnoreCase("add")) {
			if (!(sender instanceof BadblockPlayer)) {
				sender.sendMessage(ChatColor.RED + "This command is only for players.");
				return true;
			}
			BadblockPlayer player = (BadblockPlayer) sender;
			// /npc add <entitytype> <highlighteditem> <dyecolor> <server> <vip>
			// <staff> <displayname>
			// /npc add VILLAGER BED RED rush2v2 Event Rush 2v2 !
			if (args.length >= 7) {
				List<EntityType> entityTypes = Arrays.asList(EntityType.values());
				EntityType entityType = null;
				for (EntityType entityTypee : entityTypes)
					if (entityTypee.name().equals(args[1].toUpperCase())) {
						entityType = entityTypee;
						break;
					}
				if (entityType == null) {
					i18n.sendMessage(sender, "hub.npc.add_unknown_entitytype", args[1].toUpperCase());
					return true;
				}
				String serverName = args[2];
				String vipBooleanString = args[3];
				String staffBooleanString = args[4];
				String matchmakingBooleanString = args[5];
				boolean vip = false;
				boolean staff = false;
				try {
					vip = Boolean.parseBoolean(vipBooleanString.toUpperCase());
				} catch (Exception error) {
					i18n.sendMessage(sender, "hub.npc.booleanmustbeboolean");
					return true;
				}
				boolean matchmaking = false;
				try {
					matchmaking = Boolean.parseBoolean(matchmakingBooleanString.toUpperCase());
				} catch (Exception error) {
					i18n.sendMessage(sender, "hub.npc.booleanmustbeboolean");
					return true;
				}
				try {
					staff = Boolean.parseBoolean(staffBooleanString.toUpperCase());
				} catch (Exception error) {
					i18n.sendMessage(sender, "hub.npc.booleanmustbeboolean");
					return true;
				}
				String displayName = StringUtils.join(args, " ", 6);
				// id
				int id = 0;
				while (true) {
					if (NPCData.stockage.containsKey(id))
						id++;
					else
						break;
				}
				NPCData npcData = new NPCData(ConfigUtils.convertLocationToString(player.getLocation()), entityType,
						vip, staff, matchmaking, displayName.replace("&", "°"), serverName);
				NPCData.stockage.put(id, npcData);
				RequestNPCTask.work();
				GameAPI.getAPI().getSqlDatabase().call("UPDATE keyValues SET value = '"
						+ BadBlockHub.getInstance().getGsonExpose().toJson(NPCData.stockage) + "' WHERE `key` = 'npc'",
						SQLRequestType.UPDATE);
				i18n.sendMessage(sender, "hub.npc.added_npc", id);
			}
			i18n.sendMessage(sender, "hub.npc.help_add");
			return true;
		}
		if (args[0].equalsIgnoreCase("remove") || args[0].equalsIgnoreCase("rm")) {
			if (args.length >= 2) {
				String intId = args[1];
				int id = -1;
				try {
					id = Integer.parseInt(intId);
				} catch (Exception error) {
					i18n.sendMessage(sender, "hub.npc.idmustbeinteger");
					return true;
				}
				if (NPCData.stockage.containsKey(id)) {
					NPCData.stockage.get(id).remove();
					NPCData.stockage.remove(id);
				}
				GameAPI.getAPI().getSqlDatabase().call("UPDATE keyValues SET value = '"
						+ BadBlockHub.getInstance().getGsonExpose().toJson(NPCData.stockage) + "' WHERE `key` = 'npc'",
						SQLRequestType.UPDATE);
				i18n.sendMessage(sender, "hub.npc.removed_npc", id);
				return true;
			}
			i18n.sendMessage(sender, "hub.npc.help_remove");
			return true;
		}
		return false;
	}

}
