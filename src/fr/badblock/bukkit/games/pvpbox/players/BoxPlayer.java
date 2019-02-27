package fr.badblock.bukkit.games.pvpbox.players;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import fr.badblock.api.common.tech.mongodb.MongoService;
import fr.badblock.bukkit.games.pvpbox.PvPBox;
import fr.badblock.bukkit.games.pvpbox.PvPBoxAchievementList;
import fr.badblock.bukkit.games.pvpbox.config.BoxConfig;
import fr.badblock.bukkit.games.pvpbox.config.BoxLocation;
import fr.badblock.bukkit.games.pvpbox.inventories.BukkitInventories;
import fr.badblock.bukkit.games.pvpbox.kits.Kit;
import fr.badblock.bukkit.games.pvpbox.kits.KitManager;
import fr.badblock.bukkit.games.pvpbox.utils.dataloader.DataLoaderManager;
import fr.badblock.bukkit.games.pvpbox.utils.dataunloader.DataUnloaderManager;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.data.PlayerAchievementState;
import fr.badblock.gameapi.run.BadblockGame;
import fr.badblock.gameapi.utils.i18n.Locale;
import fr.badblock.gameapi.utils.itemstack.ItemAction;
import fr.badblock.gameapi.utils.itemstack.ItemEvent;
import fr.badblock.gameapi.utils.itemstack.ItemStackExtra;
import fr.badblock.gameapi.utils.itemstack.ItemStackExtra.ItemPlaces;
import fr.badblock.gameapi.utils.threading.TaskManager;
import lombok.Data;
import net.md_5.bungee.api.ChatColor;

@Data
public class BoxPlayer
{

	public static Map<BadblockPlayer, BoxPlayer> players = new HashMap<>();

	private BadblockPlayer		player;

	private BoxScoreboard	scoreboard;

	private long						joinTimestamp;
	private long						quitTimestamp;
	private long						arenaJoinTimestamp;
	private long						lastSpawn;

	private Kit							kit;

	private boolean				loaded;
	private int							level;
	private int							xp;
	private int							kills;
	private int							deaths;
	private int							assists;

	private long						lastHit;

	private Map<String, Long>	kitUsages;

	private Map<String, Long>	lastAttacks;
	private long							lastPlay;

	private String						inventory;

	public BoxPlayer(BadblockPlayer player)
	{
		setPlayer(player);
		setJoinTimestamp(System.currentTimeMillis());

		setLastAttacks(new HashMap<>());

		GameAPI.logColor("§e[PvPBox] Loading " + player.getName() + "...");
		players.put(player, this);
		fetchAsync();
		loadScoreboard();
	}

	public ItemStackExtra createAchievItem(Locale locale, ItemPlaces place){
		ItemEvent event = new ItemEvent(){
			@Override
			public boolean call(ItemAction action, BadblockPlayer player) {
				try {
					BadblockGame.PVPBOX.getGameData().getAchievements().openInventory(player);
				} catch(Exception e){
					e.printStackTrace();
					player.sendMessage(ChatColor.RED + "error: badblock/bad-achievement-configuration");
				}
				return true;
			}
		};

		return GameAPI.getAPI().createItemStackFactory().type(Material.NETHER_STAR)
				.doWithI18n(locale)
				.displayName("joinitems.achievements.displayname")
				.lore("joinitems.achievements.lore")
				.asExtra(1)
				.listenAs(event, place);
	}

	public void loadScoreboard()
	{
		TaskManager.scheduleAsyncRepeatingTask("boxscoreboard_" + player.getName(), new Runnable()
		{
			@Override
			public void run()
			{
				if (!player.isOnline())
				{
					TaskManager.cancelTaskByName("boxscoreboard_" + player.getName());
					return;
				}

				if (scoreboard == null)
				{
					scoreboard = new BoxScoreboard(player);
				}
				else
				{
					scoreboard.generate();
				}

				if (PvPBox.getInstance().getBoxConfig().getArenaCuboid().getCuboidSelection().isInSelection(player))
				{
					long lsp = System.currentTimeMillis() - lastPlay;

					if (lsp > 60 * 2 * 1000)
					{
						int add = 2;
						int bcoins = player.getPlayerData().addBadcoins(add, true);

						setXp(getXp() + bcoins);

						player.sendTranslatedMessage("pvpbox.xpbonus", bcoins);
						player.sendTranslatedMessage("pvpbox.badcoinsplaybonus", add);

						if (bcoins > add)
						{
							player.sendTranslatedMessage("pvpbox.xpbonusadd", (bcoins - add));
							player.sendTranslatedMessage("pvpbox.badcoinsplaybonusadd", (bcoins - add));
						}

						lastPlay = System.currentTimeMillis();
					}
				}
			}
		}, 1, 20);
	}

	public void fetchAsync()
	{
		DataLoaderManager.getInstance().send(this);
	}

	public void saveAsync()
	{
		DataUnloaderManager.getInstance().send(this);
	}

	public void save()
	{
		if (!isLoaded())
		{
			GameAPI.logColor("§c[PvPBox] Can't unload " + player.getName() + ": Not loaded yet?");
			return;
		}

		if (getQuitTimestamp() <= 0)
		{
			return;
		}

		MongoService mongoService = GameAPI.getAPI().getMongoService();
		DB db = mongoService.getDb();
		DBCollection dbCollection = db.getCollection("pvpbox_players");

		String uniqueId = player.getUniqueId().toString();

		BasicDBObject query = new BasicDBObject();
		query.put("uniqueId", uniqueId.toLowerCase());

		DBCursor cursor = dbCollection.find(query);

		String kitName = this.getKit() != null ? this.getKit().getName() : null;

		if (System.currentTimeMillis() - this.getLastHit() < 10000)
		{
			this.setDeaths(this.getDeaths() + 1);
		}

		BasicDBObject newObject = new BasicDBObject();
		newObject.put("uniqueId", uniqueId);
		newObject.put("xp", this.getXp());
		newObject.put("lastKitName", kitName);
		newObject.put("assists", this.getAssists());
		newObject.put("deaths", this.getDeaths());
		newObject.put("kills", this.getKills());
		newObject.put("level", this.getLevel());
		newObject.put("kitUsages", this.getKitUsages());

		if (cursor.hasNext())
		{
			BasicDBObject updater = new BasicDBObject("$set", newObject);
			dbCollection.update(query, updater);
		}
		else
		{
			dbCollection.insert(newObject);
		}

		cursor.close();

		double diffTime = System.currentTimeMillis() - getQuitTimestamp();
		GameAPI.logColor("§a[PvPBox] Unloaded " + player.getName() + " (in " + diffTime + " milliseconds)");
	}

	public void fetch()
	{
		if (!player.isOnline())
		{
			GameAPI.logColor("§c[PvPBox] Unable to load data for " + player.getName() + ": Offline.");
			return;
		}

		MongoService mongoService = GameAPI.getAPI().getMongoService();
		DB db = mongoService.getDb();
		DBCollection dbCollection = db.getCollection("pvpbox_players");

		String uniqueId = player.getUniqueId().toString();

		BasicDBObject query = new BasicDBObject();
		query.put("uniqueId", uniqueId.toLowerCase());

		DBCursor cursor = dbCollection.find(query);

		if (cursor.hasNext())
		{
			// get current data
			BasicDBObject data = (BasicDBObject) cursor.next();
			if (data.containsField("level"))
			{
				level = data.getInt("level");
			}

			if (level == 0)
			{
				level = 1;
			}

			xp = data.getInt("xp");
			kills = data.getInt("kills");
			deaths = data.getInt("deaths");
			assists = data.getInt("assists");

			if (data.containsField("kitUsages"))
			{
				@SuppressWarnings("unchecked")
				Map<String, Long> mp = (HashMap<String, Long>) data.get("kitUsages");
				this.setKitUsages(mp);
			}

			String kitName = data.getString("lastKitName");
			if (kitName != null && !kitName.isEmpty())
			{
				KitManager kitManager = KitManager.getInstance();

				if (kitManager.exists(kitName))
				{
					Kit kit = kitManager.getKit(kitName);
					this.setKit(kit);
				}
				else
				{
					GameAPI.logError("Unable to find last kit for " + player.getName() + ": '" + kitName + "'");
				}

			}
		}
		else
		{
			level = 1;
		}

		cursor.close();

		setLoaded(true);

		double diffTime = System.currentTimeMillis() - getJoinTimestamp();
		GameAPI.logColor("§a[PvPBox] Loaded " + player.getName() + " (in " + diffTime + " milliseconds)");
	}

	public void arenaJoin()
	{
		this.arenaJoinTimestamp = System.currentTimeMillis();

		if (getKit() != null && !getKit().canUse(getPlayer()))
		{
			setKit(null);
		}
		else if (getKit() != null && getKit().canUse(getPlayer()))
		{
			getKit().remove(getPlayer());
		}

		if (getKit() == null)
		{
			// TODO: give default kit
			KitManager kitManager = KitManager.getInstance();
			Kit defaultKit = kitManager.getDefaultKit();

			if (defaultKit == null)
			{
				getPlayer().sendTranslatedMessage("pvpbox.noavailablekit");
				return;
			}

			setKit(defaultKit);
		}

		getKit().give(getPlayer());

		getPlayer().sendTranslatedMessage("pvpbox.joinarena");

		//BoxConfig boxConfig = PvPBox.getInstance().getBoxConfig();

		/*Location lc = null;
		double minDst = 0.0D;

		for (BoxLocation bl : boxConfig.getTeleportLocations())
		{
			Location loc = bl.getBukkitLocation();

			if (loc == null)
			{
				continue;
			}

			if (lc == null)
			{
				lc = loc;
				continue;
			}

			double minDist = Long.MAX_VALUE;
			
			for (Player pl : Bukkit.getOnlinePlayers())
			{
				if (!pl.getWorld().equals(loc.getWorld()))
				{
					continue;
				}
				
				double dst = loc.distance(pl.getLocation());
				if (minDist < dst)
				{
					continue;
				}
				
				minDist = dst;
			}
			
			if (minDst > minDist)
			{
				continue;
			}
			
			lc = loc;
			minDst = minDist;
		}
		
		if (lc != null)
		{
			getPlayer().teleport(lc);
		}*/
	}

	public void clearAll()
	{
		// Clear
		getPlayer().clearInventory();
		getPlayer().getEquipment().clear();
	}

	public void reset()
	{
		// Set game mode
		getPlayer().setGameMode(GameMode.ADVENTURE);

		for (PotionEffect effect : player.getActivePotionEffects())
		{
			player.removePotionEffect(effect.getType());
		}

		player.setHealth(player.getMaxHealth());

		// Spawn teleport
		PvPBox box = PvPBox.getInstance();
		BoxConfig boxConfig = box.getBoxConfig();

		Location spawn = boxConfig.getSpawnLocation().getBukkitLocation();

		getPlayer().teleport(spawn);

		PlayerAchievementState state = player.getPlayerData().getAchievementState(PvPBoxAchievementList.PVPBOX_KILLER);
		state.setProgress(0);

		state = player.getPlayerData().getAchievementState(PvPBoxAchievementList.PVPBOX_UKILLER);
		state.setProgress(0);

		state = player.getPlayerData().getAchievementState(PvPBoxAchievementList.PVPBOX_ASSIST);
		state.setProgress(0);

		// Clear all
		clearAll();

		// Give default inventory
		BukkitInventories.giveDefaultInventory(player);

		// Give achievement item
		ItemStackExtra ext = createAchievItem(Locale.FRENCH_FRANCE, ItemPlaces.HOTBAR_CLICKABLE);
		player.getInventory().setItem(0, ext.getHandler());

		ItemEvent event = new ItemEvent(){
			@Override
			public boolean call(ItemAction action, BadblockPlayer player) {
				player.sendPlayer(PvPBox.getInstance().getBoxConfig().getFallbackServer());
				return true;
			}
		};

		ItemStack item = GameAPI.getAPI().createItemStackFactory().type(Material.DARK_OAK_DOOR_ITEM)
				.doWithI18n(Locale.FRENCH_FRANCE)
				.displayName("joinitems.leave.displayname")
				.lore("joinitems.leave.lore")
				.asExtra(1)
				.listenAs(event, ItemPlaces.HOTBAR_CLICKABLE)
				.getHandler();

		player.getInventory().setItem(8, item);
	}

	public void remove()
	{
		player.saveGameData();

		GameAPI.logColor("§6[PvPBox] Unloading " + player.getName() + "...");

		setQuitTimestamp(System.currentTimeMillis());
		players.remove(player);

		saveAsync();
	}

	public static BoxPlayer get(BadblockPlayer player)
	{
		if (!players.containsKey(player))
		{
			throw new IllegalArgumentException("The player " + player.getName() + " isn't registered as a BoxPlayer");
		}

		return players.get(player);
	}

	public static BoxPlayer make(BadblockPlayer player)
	{
		if (players.containsKey(player))
		{
			return null;
		}

		BoxPlayer boxPlayer = new BoxPlayer(player);

		// ??
		return boxPlayer;
	}

}
