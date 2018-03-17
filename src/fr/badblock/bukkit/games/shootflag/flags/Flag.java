package fr.badblock.bukkit.games.shootflag.flags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.badblock.bukkit.games.shootflag.PluginShootFlag;
import fr.badblock.bukkit.games.shootflag.entities.ShootFlagTeamData;
import fr.badblock.bukkit.games.shootflag.listeners.ShootFlagMapProtector;
import fr.badblock.bukkit.games.shootflag.players.ShootFlagScoreboard;
import fr.badblock.game.core18R3.players.GameTeam;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import lombok.Data;

@Data
public class Flag implements Runnable
{

	public static Map<String, Flag>	flags = new HashMap<>();

	private String				name;
	private List<FakeLocation>	glass;
	private List<FakeLocation>	itemFrames;
	private List<FakeLocation>	wools;

	private transient int		percent;
	private transient GameTeam	teamFlagging;
	private transient GameTeam	flaggedBy;
	private transient long		lastFlagging;
	private transient long		cache;

	public Flag(String name, List<FakeLocation> glass, List<FakeLocation> itemFrames, List<FakeLocation> wools)
	{
		this(name, toLocations(glass), toLocations(itemFrames), toLocations(wools), false);
	}

	@SuppressWarnings("deprecation")
	public Flag(String name, List<Location> glass, List<Location> itemFrames, List<Location> wools, boolean b)
	{
		setName(name);
		setGlass(toFakeLocations(glass));
		setItemFrames(toFakeLocations(itemFrames));
		setWools(toFakeLocations(wools));

		flags.put(getName(), this);

		// Set to white
		for (Location location : getRealWools())
		{
			Block block = location.getBlock();
			if (block.getType().equals(Material.WOOL))
			{
				if (block.getData() == DyeColor.WHITE.getData())
				{
					continue;
				}
				block.setData(DyeColor.WHITE.getData());
			}
		}

		// Set to white
		for (Location location : getRealGlass())
		{
			Block block = location.getBlock();
			if (block.getType().equals(Material.STAINED_GLASS))
			{
				if (block.getData() == DyeColor.WHITE.getData())
				{
					continue;
				}
				block.setData(DyeColor.WHITE.getData());
			}
		}

		// Task
		Bukkit.getScheduler().runTaskTimer(PluginShootFlag.getInstance(), this, 20, 20);

		// log
		System.out.println("Loaded flag : " + getName());
	}

	@SuppressWarnings("deprecation")
	public boolean ownPart(GameTeam team, BadblockPlayer player, ItemFrame itemFrame)
	{
		if (!ShootFlagMapProtector.inGame())
		{
			return false;
		}

		setLastFlagging(time() + 4000L);
		setTeamFlagging(team);
		setPercent(Math.min(percent + 2, 100));

		if (getPercent() == 100)
		{
			BadblockTeam lastFlaggedTeam = getFlaggedBy();
			setFlaggedBy(team);
			setLastFlagging(0);
			player.getPlayerData().incrementStatistic("shootflag", ShootFlagScoreboard.FLAGS);
			player.sendTranslatedMessage("shootflag.capture");
			player.setMaxHealth(player.getMaxHealth() + 2);

			if (player.getHealth() + 2 <= player.getMaxHealth())
			{
				player.setHealth(player.getHealth() + 2);
			}
			else
			{
				player.setHealth(player.getMaxHealth());
			}

			for (BadblockPlayer po : GameAPI.getAPI().getRealOnlinePlayers())
			{
				po.sendTranslatedMessage("shootflag.capturedby", team.getChatName(), player.getName(), getName());
			}

			for (BadblockTeam gameTeam : GameAPI.getAPI().getTeams())
			{
				if (gameTeam.equals(team))
				{
					gameTeam.getOnlinePlayers().forEach(pl ->
					{
						pl.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 10, 1));
						pl.playSound(pl.getLocation(), Sound.LEVEL_UP, 10F, 1F);
					});
				}
				else
				{
					gameTeam.getOnlinePlayers().forEach(pl -> {
						pl.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 1));
						pl.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 6));
						if (lastFlaggedTeam != null && lastFlaggedTeam.equals(team))
						{
							pl.playSound(pl.getLocation(), Sound.ENDERMAN_DEATH, 10F, 1F);
						}
						else
						{
							pl.playSound(pl.getLocation(), Sound.ENDERDRAGON_WINGS, 10F, 1F);
						}
					});
				}
			}

			// Wools
			byte data = team.getDyeColor().getData();
			for (Location location : getRealWools())
			{
				if (location.getBlock().getType().equals(Material.WOOL))
				{
					if (location.getBlock().getData() == data)
					{
						continue;
					}
					location.getBlock().setData(data);
				}
			}
			

			for (Location location : getRealGlass())
			{
				if (location.getBlock().getType().equals(Material.STAINED_GLASS))
				{
					if (location.getBlock().getData() == data)
					{
						continue;
					}
					location.getBlock().setData(data);
				}
			}

			team.teamData(ShootFlagTeamData.class).addPoints(30);

		}

		setCache(getCache() + 2);

		boolean needGetPartOfWool = (int) 100 / getRealWools().size() <= cache;

		if (needGetPartOfWool)
		{
			byte data = team.getDyeColor().getData();
			for (Location location : getRealWools())
			{
				if (location.getBlock().getType().equals(Material.WOOL))
				{
					if (location.getBlock().getData() == data)
					{
						continue;
					}

					location.getBlock().setData(data);
					setCache(0);
					break;

				}
			}
		}

		return true;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void run()
	{
		if (getLastFlagging() < time() && getPercent() != 0)
		{
			setPercent(0);

			// ItemFrame names
			for (Entity entity : getRealItemFrames().get(0).getChunk().getEntities())
			{
				if (!(entity instanceof ItemFrame))
				{
					continue;
				}
				if (entity.getLocation().distance(getRealItemFrames().get(0)) > 3)
				{
					continue;
				}

				Location location = null;
				for (Location loc : getRealItemFrames())
				{
					if (entity.getLocation().equals(location))
					{
						location = loc;
					}
				}
				if (location == null)
				{
					continue;
				}

				ItemFrame itemFrame = (ItemFrame) entity;
				ItemStack itemStack = itemFrame.getItem();
				ItemMeta itemMeta = itemStack.getItemMeta();

				itemMeta.setDisplayName("§eCrocheter §7(Clic droit)");
				itemStack.setItemMeta(itemMeta);
				itemFrame.setItem(itemStack);
			}

			for (Location wool : getRealWools())
			{
				if (getTeamFlagging() != null)
				{
					byte dataWool = wool.getBlock().getData();
					byte data = getTeamFlagging().getDyeColor().getData();
					// same :P
					if (data == dataWool)
					{
						if (getFlaggedBy() != null)
						{
							byte data2 = getFlaggedBy().getDyeColor().getData();
							wool.getBlock().setData(data2);
						}
						else
						{
							wool.getBlock().setData(DyeColor.WHITE.getData());
						}
					}
				}
			}
			setTeamFlagging(null);
		}

		// Points
		if (getFlaggedBy() != null && ShootFlagMapProtector.inGame())
		{
			getFlaggedBy().teamData(ShootFlagTeamData.class).addPoints(1);
		}

	}

	private long time()
	{
		return System.currentTimeMillis();
	}

	private FakeLocation toFakeLocation(Location location)
	{
		return new FakeLocation(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
	}

	private List<FakeLocation> toFakeLocations(List<Location> locations)
	{
		return locations.stream().map(location -> toFakeLocation(location)).collect(Collectors.toList());
	}

	private static Location toLocation(FakeLocation fakeLocation)
	{
		return fakeLocation.toLocation();
	}

	private static List<Location> toLocations(List<FakeLocation> fakeLocations)
	{
		return fakeLocations.stream().map(fakeLocation -> toLocation(fakeLocation)).collect(Collectors.toList());
	}

	private List<Location> getRealWools()
	{
		return toLocations(getWools());
	}

	public List<Location> getRealItemFrames()
	{
		return toLocations(getItemFrames());
	}

	private List<Location> getRealGlass()
	{
		return toLocations(getGlass());
	}

}
