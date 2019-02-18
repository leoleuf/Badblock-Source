package fr.badblock.bukkit.hub.v2.commands.list;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fr.badblock.bukkit.hub.v2.config.ConfigLoader;
import fr.badblock.bukkit.hub.v2.games.shoot.ShootManager;
import fr.badblock.bukkit.hub.v2.utils.FeatureUtils;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.players.BadblockPlayer;

public class GoShootCommand extends AbstractCommand {


    public GoShootCommand() {
        super("goshoot", null, BadblockPlayer.GamePermission.PLAYER);
        this.allowConsole(false);
    }

    @Override
    public boolean executeCommand(CommandSender sender, String[] args) {
    	if (!ConfigLoader.getSwitchers().isGameEnabled())
    	{
    		return true;
    	}
    	
        if(sender instanceof Player) {
            BadblockPlayer p = (BadblockPlayer) sender;

            if(FeatureUtils.isInAGame(p)){
                p.sendMessage("§cTu es dejà en partie.");
                return true;
            }

            p.teleport(ShootManager.getInstance().getTeleportPoint());
        }
        return false;
    }
}
