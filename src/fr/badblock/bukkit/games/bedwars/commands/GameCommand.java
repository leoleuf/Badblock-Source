package fr.badblock.bukkit.games.bedwars.commands;

import fr.badblock.bukkit.games.bedwars.AnvilInput;
import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.bukkit.games.bedwars.runnables.StartRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.itemstack.ItemStackUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

public class GameCommand extends AbstractCommand {
	public GameCommand() {
		super("game", new TranslatableString("commands.gbedwars.usage"), "animation.gamecommand");
		allowConsole(false);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args) {
		if(args.length == 0) return false;
		BadblockPlayer player = (BadblockPlayer) sender;
		PluginBedWars plug = PluginBedWars.getInstance();
		switch(args[0].toLowerCase()){
            case "dev":
                new AnvilInput(GameAPI.getAPI(), player)
                        .setItem(AnvilInput.AnvilInputSlot.SLOT_LEFT, ItemStackUtils.changeDisplay(new ItemStack(Material.NAME_TAG), "Entrer un texte"))
                        .setClickConsumer(e -> {
                            if (e.getSlot() != AnvilInput.AnvilInputSlot.SLOT_RIGHT) e.setClosing(false);
                            else e.getPlayer().sendMessage("Le texte entré est " + e.getItemname());
                        })
                        .setCloseConsumer(e -> e.getPlayer().sendMessage("La fermeture de l'enclume " + (e.isPlanned() ? "était" : "n'étais pas") + " planifiée."))
                        .open();
                break;
			case "start":
				String msg = "commands.gbedwars.start";
				if(!StartRunnable.started()) StartRunnable.startGame();
				else msg += "-fail";
				player.sendTranslatedMessage(msg);
			break;
			case "stop":
				msg = "commands.gbedwars.stop";
				if(StartRunnable.started()) StartRunnable.stopGame();
				else msg += "-fail";
				
				player.sendTranslatedMessage(msg);
			break;
			case "playersperteam":
				if(args.length != 2) return false;
				int perTeam;
				try {
					perTeam = Integer.parseInt(args[1]);
				} catch(Exception e){
					return false;
				}
				for(BadblockTeam team : GameAPI.getAPI().getTeams()) team.setMaxPlayers(perTeam);
				plug.getConfiguration().maxPlayersInTeam = perTeam;
				plug.setMaxPlayers(GameAPI.getAPI().getTeams().size() * perTeam);
				try {
					BukkitUtils.setMaxPlayers(plug.getMaxPlayers());
				} catch (Exception e) {
					e.printStackTrace();
				}
				player.sendTranslatedMessage("commands.gbedwars.modifycount");
			break;
			default: return false;
		}
		
		return true;
	}
}