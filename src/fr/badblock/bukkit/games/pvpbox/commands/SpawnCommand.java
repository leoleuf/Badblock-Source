package fr.badblock.bukkit.games.pvpbox.commands;

import org.bukkit.command.CommandSender;

import fr.badblock.bukkit.games.pvpbox.PvPBox;
import fr.badblock.bukkit.games.pvpbox.config.BoxConfig;
import fr.badblock.bukkit.games.pvpbox.players.BoxPlayer;
import fr.badblock.gameapi.command.AbstractCommand;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.GamePermission;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.threading.TaskManager;

public class SpawnCommand extends AbstractCommand
{

	public SpawnCommand()
	{
		super("spawn", new TranslatableString("commands.pvpbox.usage"), GamePermission.PLAYER, GamePermission.PLAYER, GamePermission.PLAYER);
		this.allowConsole(false);
	}

	@Override
	public boolean executeCommand(CommandSender sender, String[] args)
	{
		BadblockPlayer player = (BadblockPlayer) sender;
		BoxPlayer boxPlayer = BoxPlayer.get(player);

		if (boxPlayer == null)
		{
			return true;
		}

		PvPBox pvpbox = PvPBox.getInstance();
		BoxConfig boxConfig = pvpbox.getBoxConfig();
		
		if (player.hasPermission("pvpbox.admin"))
		{
			spawnTeleport(boxPlayer, boxConfig);
			return true;
		}
		
		long teleportTime = System.currentTimeMillis() + 10_000L;
		boxPlayer.setLastSpawn(teleportTime);
		
		player.sendTranslatedMessage("pvpbox.youwillbeteleported");
		
		TaskManager.runTaskLater(new Runnable()
		{

			@Override
			public void run()
			{
				if (!player.isOnline())
				{
					return;
				}
				
				if (boxPlayer.getLastSpawn() != teleportTime)
				{
					return;
				}
				
				if (boxPlayer.getLastSpawn() < System.currentTimeMillis())
				{
					return;
				}
				
				spawnTeleport(boxPlayer, boxConfig);
			}

		}, (int) (boxConfig.getSpawnCommandTime() / 1000 * 20));
		return true;
	}

	private void spawnTeleport(BoxPlayer boxPlayer, BoxConfig boxConfig)
	{
		boxPlayer.setLastSpawn(0);
		boxPlayer.reset();
		boxPlayer.getPlayer().sendTranslatedMessage("pvpbox.teleportedspawn");
	}

}
