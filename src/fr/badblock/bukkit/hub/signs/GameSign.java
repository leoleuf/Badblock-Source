package fr.badblock.bukkit.hub.signs;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;

import com.google.gson.annotations.Expose;

import fr.badblock.bukkit.hub.BadBlockHub;
import fr.badblock.bukkit.hub.inventories.selector.items.GameSelectorItem;
import fr.badblock.gameapi.utils.ConfigUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameSign implements Runnable {

	@Expose
	private String				internalName;
	@Expose
	private String				displayGameName;
	@Expose
	private String				displayModeName;
	@Expose
	private String				location;
	@Expose
	private String				direction;
	private Map<String, Long>	tempMap = new HashMap<>(); 
	private int					taskId;
	private Location			realLocation;
	private transient int		tempPlayers = -1;
	private transient long		lastRefresh;
	private transient long		firstRefresh;

	public GameSign(String internalName, String displayGameName, String displayModeName, Location location, String direction)
	{
		setInternalName(internalName);
		setDisplayGameName(displayGameName);
		setDisplayModeName(displayModeName);
		setRealLocation(location);
		setLocation(ConfigUtils.convertLocationToString(getRealLocation()));
		setDirection(direction);
		yop();
	}
	
	public void yop() {
		tempMap = new HashMap<>();
		firstRefresh = System.currentTimeMillis() + 30_000;
		realLocation = ConfigUtils.convertStringToLocation(getLocation());
		System.out.println("Set real location : " + realLocation);
		taskId = BadBlockHub.getInstance().getServer().getScheduler().scheduleSyncRepeatingTask(BadBlockHub.getInstance(), this, 0, 5);
	}

	public void run() {
		if (tempMap == null)
		{
			tempMap = new HashMap<>();
		}
		long timestamp = System.currentTimeMillis();
		Iterator<Entry<String, Long>> iterator = tempMap.entrySet().iterator();
		while (iterator.hasNext())
		{
			Entry<String, Long> entry = iterator.next();
			if (entry.getValue() < timestamp)
			{
				iterator.remove();
			}
		}
		// Manage recent players
		if (tempPlayers == -1 || firstRefresh > timestamp)
		{
			tempPlayers = getInGamePlayers();
		}
		else
		{
			System.out.println("AAAAAAAAAAAAAAAA");
			if (tempPlayers < getInGamePlayers())
			{
				System.out.println("BBBBBBBBBBBBB");
				if (getLastRefresh() < timestamp)
				{
					System.out.println("CCCCCCCCCCCCCC");
					tempPlayers++;
					setLastRefresh(timestamp + (new Random().nextInt(1300) + 900));
				}
			}
			else if (tempPlayers > getInGamePlayers())
			{
				System.out.println("0000000000");
				tempPlayers = getInGamePlayers();
			}
		}
		// Manage sign
		Block block = realLocation.getBlock();
		if (block.getType().name().contains("SIGN"))
		{
			Sign sign = (Sign) block.getState();
			org.bukkit.material.Sign signData = (org.bukkit.material.Sign) sign.getData();
			signData.setFacingDirection(getBlockFace(getDirection()));
			sign.setData(signData);
			String[] lines = generate();
			boolean equals = true;
			int i = -1;
			for (String line : sign.getLines())
			{
				i++;
				if (!line.equalsIgnoreCase(lines[i]))
				{
					System.out.println(line + " : " + lines[i]);
					equals = false;
					break;
				}
			}
			if (!equals)
			{
				for(int x=0;x<4;x++)
				{
					sign.setLine(x, lines[x]);
				}
				sign.update(true);
				System.out.println("Updated GameSign " + internalName);
			}
		}
		else
		{
			block.setType(Material.CHEST);
			org.bukkit.material.Chest chestData = (org.bukkit.material.Chest) block.getState().getData();
			chestData.setFacingDirection(getBlockFace(getDirection()));
			block.getState().setData(chestData);
			block.setType(Material.WALL_SIGN);
			org.bukkit.material.Sign signData = (org.bukkit.material.Sign) block.getState().getData();
			signData.setFacingDirection(getBlockFace(getDirection()));
			block.getState().setData(signData);
		}
	}
	
	private BlockFace getBlockFace(String name)
	{
		for (BlockFace blockFace : BlockFace.values())
			if (blockFace.name().equalsIgnoreCase(name))
				return blockFace;
		return BlockFace.NORTH;
	}

	public void remove() {
		BadBlockHub.getInstance().getServer().getScheduler().cancelTask(getTaskId());
		if (realLocation == null || realLocation.getBlock() == null)
			return;
		realLocation.getBlock().setType(Material.AIR);
	}

	public int getInGamePlayers()
	{
		return GameSelectorItem.inGamePlayers.containsKey(getInternalName()) ? GameSelectorItem.inGamePlayers.get(getInternalName()) : 0;
	}

	public int getWaitingLinePlayers()
	{
		return GameSelectorItem.waitingLinePlayers.containsKey(getInternalName()) ? GameSelectorItem.waitingLinePlayers.get(getInternalName()) : 0;
	}

	public long getTemporaryPlayers()
	{
		System.out.println("Temp map : " + getTempMap().size());
		return getWaitingLinePlayers() + (getInGamePlayers() > getTempPlayers() ? (getInGamePlayers() - getTempPlayers()) : 0) + getTempMap().size();
	}

	private String generateTemporaryView()
	{
		long temporaryPlayers = getTemporaryPlayers();
		return temporaryPlayers >= 1 ? (ChatColor.DARK_PURPLE + "(" + temporaryPlayers + ") ") + ChatColor.BLACK : "";
	}

	public String[] generate()
	{
		return new String[]
				{
						ChatColor.BOLD + getDisplayGameName(),
						generateTemporaryView() + plural(getInGamePlayers(), " joueur", "s"),
						bold(ChatColor.GREEN) + "✓ Jouer ✓",
						ChatColor.BLACK + getDisplayModeName()
				};
	}

	private String plural(int value, String suffix, String plural)
	{
		return value + suffix + (value > 1 ? plural : "");
	}

	private String bold(ChatColor chatColor)
	{
		return chatColor + "" + ChatColor.BOLD;
	}

}
