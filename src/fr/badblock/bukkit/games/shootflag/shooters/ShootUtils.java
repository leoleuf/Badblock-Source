package fr.badblock.bukkit.games.shootflag.shooters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import fr.badblock.bukkit.games.shootflag.PluginShootFlag;
import fr.badblock.bukkit.games.shootflag.entities.ShootFlagTeamData;
import fr.badblock.bukkit.games.shootflag.players.ShootFlagData;
import fr.badblock.bukkit.games.shootflag.players.ShootFlagScoreboard;
import fr.badblock.game.core18R3.players.GameTeam;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.BukkitUtils;

public class ShootUtils
{

	public static void shoot(BadblockPlayer player)
	{
		ShootFlagData playerData = player.inGameData(ShootFlagData.class);

		if (playerData.getLastShootFlag() > time())
		{
			player.sendTranslatedMessage("shootflag.pleasewaitbetweeneachshoot");
			return;
		}

		GameTeam gameTeam = (GameTeam) player.getTeam();
		final Firework firework = (Firework) player.getWorld().spawnEntity
				(player.getLocation().add(0, 1, 0),EntityType.FIREWORK);

		firework.setVelocity(player.getLocation().add(0, 1, 0).getDirection());

		FireworkMeta fireworkMeta = firework.getFireworkMeta();
		fireworkMeta.addEffect(FireworkEffect.builder().withColor(gameTeam.geNormalColor()).build());

		firework.setFireworkMeta(fireworkMeta);

		final TempScheduler s = new TempScheduler(null);

		final long nextTime = time()+ 1000;
		playerData.setLastShootFlag(nextTime);

		s.task = Bukkit.getScheduler().runTaskTimer(PluginShootFlag.getInstance(), new Runnable()
		{

			int	ticks	= 20;

			@Override
			public void run()
			{
				if (player == null || !(player.isOnline()) || !(player.isValid()))
				{
					return;
				}

				if (time() > nextTime)
				{
					s.cancel();
					player.setExp(0);
					return;
				}

				ticks--;
				float f = (10 * ticks);
				player.setExp(f / 100F);
			}
		}, 0L, 1L);

		List<BadblockPlayer> victims = getTarget(player, 100, 1.2D, false);
		int i = 0;
		
		FireworkEffect effect = FireworkEffect.builder().withColor(getColor()).with(getFireWorkType()).build();

		boolean avoid = true;
		
		for (BadblockPlayer victim : victims) {
			// kill yourself!
			if (player.getUniqueId().equals(victim.getUniqueId()))
				continue;
			BadblockTeam o = player.getTeam();
			BadblockTeam o2 = victim.getTeam();
			if (o2.equals(o))
			{
				player.sendTranslatedMessage("shootflag.cantshootyourteam");
				return;
			}
			i++;
			if (i == 2)
			{
				GameAPI.i18n().broadcast("shootflag.doublekill", o.getColor() + player.getName());
			}
			else if (i == 3)
			{
				GameAPI.i18n().broadcast("shootflag.triplekill", o.getColor() + player.getName());
			}
			else if (i >= 4)
			{
				GameAPI.i18n().broadcast("shootflag.quadkill", o.getColor() + player.getName());
			}

			if (!victim.equals(player))
			{
				avoid = false;
				try
				{
					FireworkEffectPlayer fireworkEffect = new FireworkEffectPlayer();
					fireworkEffect.playFirework(victim.getWorld(), victim.getLocation(), effect);
				}
				catch(Exception error)
				{
					error.printStackTrace();
				}
				
				playerData.canHurt = time() + 10;
				victim.damage(7, player);
				
				o.teamData(ShootFlagTeamData.class).addPoints(1);
				
			}
		}
		
		player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 0.1F,
				2.0F);
		player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 0.1F,
				1.5F);
		player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 0.1F,
				1.4F);
		player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 0.1F,
				1.3F);
		player.getWorld().playSound(player.getLocation(), Sound.EXPLODE, 0.1F,
				1.2F);
		
		if (avoid)
		{
			player.getPlayerData().incrementStatistic("shootflag", ShootFlagScoreboard.SHOOTS_ERR);
		}
		else
		{
			player.getPlayerData().incrementStatistic("shootflag", ShootFlagScoreboard.SHOOTS_OK);
		}

	}
	
	public static Type getFireWorkType() {
		int r = new Random().nextInt(1) + 5;
		if (r == 1)
			return FireworkEffect.Type.BALL;
		else if (r == 2)
			return FireworkEffect.Type.BALL_LARGE;
		else if (r == 3)
			return FireworkEffect.Type.BURST;
		else if (r == 4)
			return FireworkEffect.Type.CREEPER;
		else if (r == 5)
			return FireworkEffect.Type.STAR;
		else
			return FireworkEffect.Type.BALL;
	}
	
	public static Color getColor() {
		int r = new Random().nextInt(1) + 15;
		if (r == 1)
			return Color.AQUA;
		else if (r == 2)
			return Color.BLUE;
		else if (r == 3)
			return Color.FUCHSIA;
		else if (r == 4)
			return Color.GRAY;
		else if (r == 5)
			return Color.GREEN;
		else if (r == 6)
			return Color.LIME;
		else if (r == 7)
			return Color.MAROON;
		else if (r == 8)
			return Color.NAVY;
		else if (r == 9)
			return Color.OLIVE;
		else if (r == 10)
			return Color.ORANGE;
		else if (r == 11)
			return Color.PURPLE;
		else if (r == 12)
			return Color.RED;
		else if (r == 13)
			return Color.SILVER;
		else if (r == 14)
			return Color.TEAL;
		else if (r == 15)
			return Color.YELLOW;
		else
			return Color.BLUE;
	}

	private static List<BadblockPlayer> getTarget(final BadblockPlayer player, int maxRange, final double aiming, final boolean wallHack) {
		final List<BadblockPlayer> target = new ArrayList<BadblockPlayer>();
		final Location playerEyes = player.getEyeLocation();
		final Vector direction = playerEyes.getDirection().normalize();
		final List<BadblockPlayer> targets = new ArrayList<BadblockPlayer>();
		for (BadblockPlayer p : BukkitUtils.getAllPlayers()) {
			if (!p.getUniqueId().equals(player.getUniqueId())
					&& p.getLocation().distanceSquared(playerEyes) < maxRange
					* maxRange) {
				targets.add(p);
			}
		}
		final Location loc = playerEyes.clone();
		final Vector progress = direction.clone().multiply(0.7);
		maxRange = 100 * maxRange / 70;
		int loop = 0;
		while (loop < maxRange) {
			++loop;
			loc.add(progress);
			final Block block = loc.getBlock();
			if (!wallHack && block.getType().isSolid())
			{
				break;
			}
			final double lx = loc.getX();
			final double ly = loc.getY();
			final double lz = loc.getZ();
			for (Player p : Bukkit.getOnlinePlayers())
			{
				try {
					ParticleEffects.FIREWORKS_SPARK.sendToPlayer(p, loc, 0.1f, 0.1f, 0.1f, 0.05f, 2);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			for (final BadblockPlayer possibleTarget : targets)
			{
				if (possibleTarget.getUniqueId() == player.getUniqueId())
				{
					continue;
				}
				final Location testLoc = possibleTarget.getLocation().add(0.0,
						0.85, 0.0);
				final double px = testLoc.getX();
				final double py = testLoc.getY();
				final double pz = testLoc.getZ();
				final boolean dX = Math.abs(lx - px) < 0.7 * aiming;
				final boolean dY = Math.abs(ly - py) < 1.7 * aiming;
				final boolean dZ = Math.abs(lz - pz) < 0.7 * aiming;
				if (!dX || !dY || !dZ || target.contains(possibleTarget)) {
					continue;
				}
				target.add(possibleTarget);
			}
		}
		return target;
	}

	private static long time()
	{
		return System.currentTimeMillis();
	}

}
