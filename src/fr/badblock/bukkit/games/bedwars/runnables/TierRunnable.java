package fr.badblock.bukkit.games.bedwars.runnables;

import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.utils.BukkitUtils;

public class TierRunnable extends BukkitRunnable {

	// TODO Config
	private static final int diamondDefaultTierTime = 300;
	private static final int emeraldDefaultTierTime = 510;

	public static int diamondTier		= 1;
	public static int emeraldTier		= 1;

	public static int diamondTierTime	= diamondDefaultTierTime;
	public static int emeraldTierTime	= emeraldDefaultTierTime;

	public TierRunnable()
	{
		this.runTaskTimer(GameAPI.getAPI(), 20, 20);
	}

	@Override
	public void run()
	{

		if (diamondTierTime == 0 && diamondTier < 3)
		{
			diamondTier++;
			String nextDiamondTier = TierRunnable.diamondTier == 2 ? "II" : TierRunnable.diamondTier == 3 ? "III" : "";
			BukkitUtils.getPlayers().stream().forEach(player ->
			{
				player.sendTranslatedMessage("bedwars.upgradetierdiamond", nextDiamondTier);
				player.playSound(Sound.LEVEL_UP);
			});
			if (diamondTier < 3)
			{
				diamondTierTime = diamondDefaultTierTime;
			}
			else
			{
				diamondTierTime = -1;
			}
		}
		else if (diamondTierTime <= 0 && diamondTier >= 3)
		{
			diamondTierTime = -1;
		}
		else
		{
			diamondTierTime--;
		}

		if (emeraldTierTime == 0 && emeraldTier < 3)
		{
			emeraldTier++;
			String nextEmeraldTier = TierRunnable.emeraldTier == 2 ? "II" : TierRunnable.emeraldTier == 3 ? "III" : "";
			BukkitUtils.getPlayers().stream().forEach(player ->
			{
				player.sendTranslatedMessage("bedwars.upgradetieremerald", nextEmeraldTier);
				player.playSound(Sound.LEVEL_UP);
			});
			if (emeraldTier < 3)
			{
				emeraldTierTime = emeraldDefaultTierTime;
			}
			else
			{
				emeraldTierTime = -1;
			}
		}
		else if (emeraldTierTime <= 0 && emeraldTier >= 3)
		{
			emeraldTierTime = -1;
		}
		else
		{
			emeraldTierTime--;
		}
	}

}
