package fr.badblock.bukkit.games.shootflag;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.badblock.bukkit.games.shootflag.commands.GameCommand;
import fr.badblock.bukkit.games.shootflag.commands.ShootFlagCommand;
import fr.badblock.bukkit.games.shootflag.configuration.ShootFlagConfiguration;
import fr.badblock.bukkit.games.shootflag.configuration.ShootFlagMapConfiguration;
import fr.badblock.bukkit.games.shootflag.listeners.DeathListener;
import fr.badblock.bukkit.games.shootflag.listeners.EntityDamageListener;
import fr.badblock.bukkit.games.shootflag.listeners.EntityExplodeListener;
import fr.badblock.bukkit.games.shootflag.listeners.EntityRegainHealthListener;
import fr.badblock.bukkit.games.shootflag.listeners.JoinListener;
import fr.badblock.bukkit.games.shootflag.listeners.MoveListener;
import fr.badblock.bukkit.games.shootflag.listeners.PartyJoinListener;
import fr.badblock.bukkit.games.shootflag.listeners.PlayerBedEnterListener;
import fr.badblock.bukkit.games.shootflag.listeners.PlayerDropItemListener;
import fr.badblock.bukkit.games.shootflag.listeners.PlayerFoodLevelChangeListener;
import fr.badblock.bukkit.games.shootflag.listeners.PlayerInteractEntityListener;
import fr.badblock.bukkit.games.shootflag.listeners.PlayerInteractListener;
import fr.badblock.bukkit.games.shootflag.listeners.PlayerItemBreakListener;
import fr.badblock.bukkit.games.shootflag.listeners.PlayerItemConsumeListener;
import fr.badblock.bukkit.games.shootflag.listeners.PlayerItemHeldListener;
import fr.badblock.bukkit.games.shootflag.listeners.PlayerMountListener;
import fr.badblock.bukkit.games.shootflag.listeners.PlayerPickupItemListener;
import fr.badblock.bukkit.games.shootflag.listeners.PlayerShearEntityListener;
import fr.badblock.bukkit.games.shootflag.listeners.QuitListener;
import fr.badblock.bukkit.games.shootflag.listeners.ShootFlagMapProtector;
import fr.badblock.bukkit.games.shootflag.listeners.WeatherChangeListener;
import fr.badblock.bukkit.games.shootflag.players.ShootFlagData;
import fr.badblock.bukkit.games.shootflag.players.ShootFlagScoreboard;
import fr.badblock.bukkit.games.shootflag.runnables.PreStartRunnable;
import fr.badblock.gameapi.BadblockPlugin;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.achievements.AchievementList;
import fr.badblock.gameapi.game.GameServer.WhileRunningConnectionTypes;
import fr.badblock.gameapi.game.rankeds.RankedManager;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.kits.PlayerKit;
import fr.badblock.gameapi.run.BadblockGame;
import fr.badblock.gameapi.run.BadblockGameData;
import fr.badblock.gameapi.run.RunType;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.GameRules;
import fr.badblock.gameapi.utils.general.JsonUtils;
import fr.badblock.gameapi.utils.itemstack.ItemStackUtils;
import lombok.Getter;
import lombok.Setter;

public class PluginShootFlag extends BadblockPlugin {
	@Getter private static PluginShootFlag instance;

	public static 	     File   MAP;

	private static final String CONFIG 		   		   = "config.json";
	private static final String TEAMS_CONFIG 		   = "teams.yml";
	private static final String TEAMS_CONFIG_INVENTORY = "teamsInventory.yml";
	private static final String VOTES_CONFIG 		   = "votes.json";
	private static final String KITS_CONFIG_INVENTORY  = "kitInventory.yml";
	private static final String MAPS_CONFIG_FOLDER     = "maps";

	@Getter@Setter
	private int 			      maxPlayers;
	@Getter
	private ShootFlagConfiguration    configuration;
	@Getter@Setter
	private ShootFlagMapConfiguration mapConfiguration;

	@Getter
	private Map<String, PlayerKit> kits;

	public void giveDefaultKit(BadblockPlayer player)
	{	
		player.clearInventory();
		player.getInventory().setHeldItemSlot(0);

		// Set material
		Material material = null;
		String displayName = null;
		List<String> lore = null;
		boolean fakeEnchant = false;
		long reloadTime = 0;

		if (player.getVipLevel() >= 3)
		{
			material = Material.STICK;
			displayName = "§dMagic Stick";
			lore = Arrays.asList(
					"§7-----------------------------------",
					"§7Temps de rechargement : 0,5 seconde",
					"§7-----------------------------------"
					);
			reloadTime = 500;
			fakeEnchant = true;
		}
		else if (player.getVipLevel() >= 2)
		{
			material = Material.DIAMOND_HOE;
			displayName = "§bHoue en diamant";
			lore = Arrays.asList(
					"§7-----------------------------------",
					"§7Temps de rechargement : 0,75 seconde",
					"§7 ",
					"§dMagic Stick §7à partir du grade",
					"§aÉmeraude §7(0,5 seconde de rechargement)",
					"§7 ",
					"§7-----------------------------------"
					);
			reloadTime = 750;
		}
		else if (player.getVipLevel() >= 1)
		{
			material = Material.GOLD_HOE;
			displayName = "§6Houe en or";
			lore = Arrays.asList(
					"§7-----------------------------------",
					"§7Temps de rechargement : 1 seconde",
					"§7 ",
					"§bHoue en diamant §7à partir du grade",
					"§bDiamant §7(0,75 seconde de rechargement)",
					"§7 ",
					"§7-----------------------------------"
					);
			reloadTime = 1000;
		}
		else
		{
			material = Material.STONE_HOE;
			displayName = "§3Houe en pierre";
			lore = Arrays.asList(
					"§7-----------------------------------",
					"§7Temps de rechargement : 1,25 seconde",
					"§7 ",
					"§6Houe en or §7à partir du grade",
					"§6Gold §7(1 seconde de rechargement)",
					"§7 ",
					"§7-----------------------------------"
					);
			reloadTime = 1250;
		}

		ItemStack itemStack = new ItemStack(material);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(displayName);
		itemMeta.setLore(lore);
		itemStack.setItemMeta(itemMeta);

		if (fakeEnchant)
		{
			itemStack = ItemStackUtils.fakeEnchant(itemStack);
		}

		player.inGameData(ShootFlagData.class).reloadTime = reloadTime;

		player.getInventory().setItem(player.getInventory().getHeldItemSlot(), itemStack);
	}

	@Override
	public void onEnable(RunType runType){
		AchievementList list = ShootFlagAchievementList.instance;

		BadblockGame.SHOOTFLAG.setGameData(new BadblockGameData() {
			@Override
			public AchievementList getAchievements() {
				return list;
			}
		});

		instance = this;

		if(runType == RunType.LOBBY)
			return;

		try {
			if(!getDataFolder().exists()) getDataFolder().mkdir();

			/**
			 * Chargement de la configuration du jeu
			 */

			// Modification des GameRules
			GameRules.doDaylightCycle.setGameRule(false);
			GameRules.spectatorsGenerateChunks.setGameRule(false);
			GameRules.doFireTick.setGameRule(false);

			// Lecture de la configuration du jeu

			BadblockGame.SHOOTFLAG.use();

			File configFile    = new File(getDataFolder(), CONFIG);
			this.configuration = JsonUtils.load(configFile, ShootFlagConfiguration.class);

			JsonUtils.save(configFile, configuration, true);

			File 			  teamsFile 	= new File(getDataFolder(), TEAMS_CONFIG);
			FileConfiguration teams 		= YamlConfiguration.loadConfiguration(teamsFile);

			getAPI().registerTeams(configuration.maxPlayersInTeam, teams);
			try {
				BukkitUtils.setMaxPlayers(GameAPI.getAPI().getTeams().size() * configuration.maxPlayersInTeam);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}
			getAPI().setDefaultKitContentManager(false);

			maxPlayers = getAPI().getTeams().size() * configuration.maxPlayersInTeam;
			kits	   = getAPI().loadKits(GameAPI.getInternalGameName());

			try { teams.save(teamsFile); } catch (IOException unused){}

			// Chargement des fonctionnalités de l'API non utilisées par défaut

			getAPI().getBadblockScoreboard().doBelowNameHealth();
			getAPI().getBadblockScoreboard().doTabListHealth();
			getAPI().getBadblockScoreboard().doTeamsPrefix();
			getAPI().getBadblockScoreboard().doOnDamageHologram();

			getAPI().formatChat(true, true);
			getAPI().getJoinItems().registerKitItem(0, kits, new File(getDataFolder(), KITS_CONFIG_INVENTORY));
			getAPI().getJoinItems().registerTeamItem(3, new File(getDataFolder(), TEAMS_CONFIG_INVENTORY));
			getAPI().getJoinItems().registerAchievementsItem(4, BadblockGame.SHOOTFLAG);
			getAPI().getJoinItems().registerVoteItem(5);
			getAPI().getJoinItems().registerLeaveItem(8, configuration.fallbackServer);

			getAPI().setMapProtector(new ShootFlagMapProtector());
			getAPI().enableAntiSpawnKill();

			getAPI().getGameServer().whileRunningConnection(WhileRunningConnectionTypes.SPECTATOR);

			new MoveListener();
			new EntityDamageListener();
			new EntityExplodeListener();
			new EntityRegainHealthListener();
			new DeathListener();
			new JoinListener();
			new QuitListener();
			new PartyJoinListener();
			new PlayerFoodLevelChangeListener();
			new PlayerBedEnterListener();
			new PlayerDropItemListener();
			new PlayerInteractListener();
			new PlayerMountListener();
			new PlayerItemBreakListener();
			new PlayerItemConsumeListener();
			new PlayerItemHeldListener();
			new PlayerPickupItemListener();
			new PlayerShearEntityListener();
			new PlayerInteractEntityListener();

			File votesFile = new File(getDataFolder(), VOTES_CONFIG);

			if(!votesFile.exists())
				votesFile.createNewFile();

			getAPI().getBadblockScoreboard().beginVote(JsonUtils.loadArray(votesFile));

			new PreStartRunnable().runTaskTimer(GameAPI.getAPI(), 0, 30L);

			MAP = new File(getDataFolder(), MAPS_CONFIG_FOLDER);

			new ShootFlagCommand(MAP);
			new GameCommand();

			Bukkit.getWorlds().forEach(world -> {
				world.setTime(2000L);
				world.setWeatherDuration(0);
				world.getEntities().forEach(entity -> entity.remove());
			});

			Bukkit.getScheduler().runTaskLater(this, new Runnable()
			{
				@Override
				public void run()
				{
					new WeatherChangeListener();
				}
			}, 20 * 5);

			// Ranked
			RankedManager.instance.initialize(RankedManager.instance.getCurrentRankedGameName(), 
					ShootFlagScoreboard.KILLS, ShootFlagScoreboard.DEATHS, ShootFlagScoreboard.FLAGS, ShootFlagScoreboard.WINS, ShootFlagScoreboard.LOOSES, ShootFlagScoreboard.SHOOTS_OK, ShootFlagScoreboard.SHOOTS_ERR);

		} catch(Throwable e){
			e.printStackTrace();
		}
	}

	public void saveJsonConfig(){
		File configFile = new File(getDataFolder(), CONFIG);
		JsonUtils.save(configFile, configuration, true);
	}
}
