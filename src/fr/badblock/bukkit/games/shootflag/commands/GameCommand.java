package fr.badblock.bukkit.games.shootflag.commands;

import java.io.File;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.games.shootflag.PluginShootFlag;
import fr.badblock.bukkit.games.shootflag.configuration.ShootFlagMapConfiguration;
import fr.badblock.bukkit.games.shootflag.runnables.StartRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class GameCommand extends AbstractCommand 
{

	public static int	setup			= -1;
	public static File	generatedFile	= null;

	public GameCommand()
	{
		super("game", new TranslatableString("commands.gshootflag.usage"), "animation.gamecommand");
		allowConsole(false);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args) {
		if(args.length == 0) {
			return false;
		}

		BadblockPlayer player = (BadblockPlayer) sender;
		PluginShootFlag plug = PluginShootFlag.getInstance();

		switch(args[0].toLowerCase()){
		case "start":
			String msg = "commands.gshootflag.start";

			if(!StartRunnable.started()){
				StartRunnable.startGame();
			} else msg += "-fail";

			player.sendTranslatedMessage(msg);
			break;
		case "forcestart":
			msg = "commands.gshootflag.forcestart";

			if(!StartRunnable.started()){
				StartRunnable.startGame();
			}
			StartRunnable.time = 10;

			player.sendTranslatedMessage(msg);
			break;
		case "stop":
			msg = "commands.gshootflag.stop";

			if(StartRunnable.started()){
				StartRunnable.stopGame();
			} else msg += "-fail";

			player.sendTranslatedMessage(msg);
			break;
		case "setup":
			if (setup == -1)
			{
				setup = 0;
				File   file   = new File(PluginShootFlag.MAP, "generated_" + UUID.randomUUID().toString().split("-")[0] + ".json");
				generatedFile = file;
				
				ShootFlagMapConfiguration config = new ShootFlagMapConfiguration(GameAPI.getAPI().loadConfiguration(file));
				config.save(file);
				PluginShootFlag.getInstance().setMapConfiguration(config);
				
				player.getInventory().clear();
				int i = -1;
				while (i < 8)
				{
					i++;
					player.getInventory().setItem(i, new ItemStack(Material.STICK, 1));
				}

				player.sendTranslatedMessage("commands.gshootflag.setup_1");
			}
			else if (setup == 0)
			{
				PluginShootFlag.getInstance().getMapConfiguration().save(generatedFile);
				player.sendTranslatedMessage("commands.gshootflag.setup_2", PluginShootFlag.getInstance().getMapConfiguration().getRespawnLocations().size());
				setup = 1;
			}
			else if (setup == 1)
			{
				PluginShootFlag.getInstance().getMapConfiguration().save(generatedFile);
				player.sendTranslatedMessage("commands.gshootflag.setup_3", PluginShootFlag.getInstance().getMapConfiguration().getFlags().size());
				setup = 0;
			}
			break;
		case "playersperteam":
			if(args.length != 2)
				return false;

			int perTeam = 4;

			try {
				perTeam = Integer.parseInt(args[1]);
			} catch(Exception e){
				return false;
			}

			for(BadblockTeam team : GameAPI.getAPI().getTeams())
				team.setMaxPlayers(perTeam);

			plug.getConfiguration().maxPlayersInTeam = perTeam;
			plug.setMaxPlayers(GameAPI.getAPI().getTeams().size() * perTeam);
			try {
				BukkitUtils.setMaxPlayers(GameAPI.getAPI().getTeams().size() * perTeam);
			} catch (Exception e) {
				e.printStackTrace();
			}

			player.sendTranslatedMessage("commands.gshootflag.modifycount");
			break;
		default: return false;
		}

		return true;
	}
}