package fr.badblock.bukkit.games.bedwars.runnables;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.general.MathsUtils;

public class TrackRunnable extends BukkitRunnable {

	public TrackRunnable()
	{
		this.runTaskTimer(GameAPI.getAPI(), 1, 1);
	}

	@Override
	public void run()
	{
		for (BadblockPlayer player : BukkitUtils.getPlayers())
		{
			if (player == null)
			{
				continue;
			}

			if (GameMode.SPECTATOR.equals(player.getGameMode()))
			{
				continue;
			}

			if (player.getItemInHand() == null)
			{
				continue;
			}

			if (player.getItemInHand().getType() == null)
			{
				continue;
			}

			if (!player.getItemInHand().getType().equals(Material.COMPASS))
			{
				continue;
			}

			List<BadblockPlayer> bo = BukkitUtils.getPlayers().parallelStream().filter(plo -> plo != null && !GameMode.SPECTATOR.equals(plo.getGameMode()) && plo.getTeam() != null
					&& !plo.getTeam().equals(player.getTeam())).collect(Collectors.toList());

			BadblockPlayer nearby = null;
			double nearbyBlocks = 0.0D;
			for (BadblockPlayer bp : bo)
			{
				if (!bp.getWorld().equals(player.getWorld()))
				{
					continue;
				}

				double dst = bp.getLocation().distance(player.getLocation());
				if (nearby == null || nearbyBlocks >= dst)
				{
					nearbyBlocks = dst;
					nearby = bp;
				}

			}

			if (nearby != null)
			{
				player.sendTranslatedActionBar("bedwars.nearbyplayer", nearby.getTeam().getChatPrefix().getAsLine(player), nearby.getName(), MathsUtils.round(nearbyBlocks, 2));
			}
			else
			{
				player.sendTranslatedActionBar("bedwars.nobodynearby");
			}
		}
	}

}
