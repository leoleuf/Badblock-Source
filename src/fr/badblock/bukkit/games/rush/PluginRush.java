package fr.badblock.bukkit.games.rush;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fr.badblock.bukkit.games.rush.commands.GameCommand;
import fr.badblock.bukkit.games.rush.commands.RushCommand;
import fr.badblock.bukkit.games.rush.configuration.RushConfiguration;
import fr.badblock.bukkit.games.rush.configuration.RushMapConfiguration;
import fr.badblock.bukkit.games.rush.listeners.BedExplodeListener;
import fr.badblock.bukkit.games.rush.listeners.DeathListener;
import fr.badblock.bukkit.games.rush.listeners.FakeEntityInteractListener;
import fr.badblock.bukkit.games.rush.listeners.JoinListener;
import fr.badblock.bukkit.games.rush.listeners.MoveListener;
import fr.badblock.bukkit.games.rush.listeners.PartyJoinListener;
import fr.badblock.bukkit.games.rush.listeners.QuitListener;
import fr.badblock.bukkit.games.rush.listeners.RushMapProtector;
import fr.badblock.bukkit.games.rush.listeners.SheepListener;
import fr.badblock.bukkit.games.rush.players.RushScoreboard;
import fr.badblock.bukkit.games.rush.runnables.PreStartRunnable;
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
import lombok.Getter;
import lombok.Setter;

public class PluginRush extends BadblockPlugin {
	@Getter private static PluginRush instance;
	
	public static 	     File   MAP;
	
	private static final String CONFIG 		   		   = "config.json";
	private static final String TEAMS_CONFIG 		   = "teams.yml";
	private static final String TEAMS_CONFIG_INVENTORY = "teamsInventory.yml";
	private static final String VOTES_CONFIG 		   = "votes.json";
	private static final String KITS_CONFIG_INVENTORY  = "kitInventory.yml";
	private static final String MAPS_CONFIG_FOLDER     = "maps";
	private static final String SHOP_FOLDER     	   = "shops";

	
	@Getter@Setter
	private int 			     maxPlayers;
	@Getter
	private RushConfiguration    configuration;
	@Getter@Setter
	private RushMapConfiguration mapConfiguration;
	
	@Getter
	private Map<String, PlayerKit> kits;

	@Override
	public void onEnable(RunType runType){
		AchievementList list = RushAchievementList.instance;
		
		BadblockGame.RUSH.setGameData(new BadblockGameData() {
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

			BadblockGame.RUSH.use();

			File configFile    = new File(getDataFolder(), CONFIG);
			this.configuration = JsonUtils.load(configFile, RushConfiguration.class);
			
			JsonUtils.save(configFile, configuration, true);
			
			File 			  teamsFile 	= new File(getDataFolder(), TEAMS_CONFIG);
			FileConfiguration teams 		= YamlConfiguration.loadConfiguration(teamsFile);

			getAPI().registerTeams(configuration.maxPlayersInTeam, teams);
			getAPI().setDefaultKitContentManager(false);
			
			maxPlayers = getAPI().getTeams().size() * configuration.maxPlayersInTeam;
			try {
				BukkitUtils.setMaxPlayers(maxPlayers);
			} catch (Exception e) {
				e.printStackTrace();
			}
			kits	   = getAPI().loadKits(GameAPI.getInternalGameName());
			
			try { teams.save(teamsFile); } catch (IOException unused){}

			// Chargement des fonctionnalités de l'API non utilisées par défaut

			getAPI().getBadblockScoreboard().doBelowNameHealth();
			getAPI().getBadblockScoreboard().doTabListHealth();
			getAPI().getBadblockScoreboard().doTeamsPrefix();
			getAPI().getBadblockScoreboard().doOnDamageHologram();

			getAPI().formatChat(true, true);
			getAPI().manageShops(new File(getDataFolder(), SHOP_FOLDER));
			
			getAPI().getJoinItems().registerKitItem(0, kits, new File(getDataFolder(), KITS_CONFIG_INVENTORY));
			getAPI().getJoinItems().registerTeamItem(3, new File(getDataFolder(), TEAMS_CONFIG_INVENTORY));
			getAPI().getJoinItems().registerAchievementsItem(4, BadblockGame.RUSH);
			getAPI().getJoinItems().registerVoteItem(5);
			getAPI().getJoinItems().registerLeaveItem(8, configuration.fallbackServer);
			
			getAPI().setMapProtector(new RushMapProtector());
			getAPI().enableAntiSpawnKill();
			
			getAPI().getGameServer().whileRunningConnection(WhileRunningConnectionTypes.SPECTATOR);
			
			new MoveListener();
			new DeathListener();
			new JoinListener();
			new QuitListener();
			new PartyJoinListener();
			new BedExplodeListener();	// 
			new SheepListener();		// Gère les moutons en début de partie :3
			new FakeEntityInteractListener();
			
			File votesFile = new File(getDataFolder(), VOTES_CONFIG);

			if(!votesFile.exists())
				votesFile.createNewFile();

			getAPI().getBadblockScoreboard().beginVote(JsonUtils.loadArray(votesFile));
			
			new PreStartRunnable().runTaskTimer(GameAPI.getAPI(), 0, 30L);
			
			MAP = new File(getDataFolder(), MAPS_CONFIG_FOLDER);
			
			new RushCommand(MAP);
			new GameCommand();
			
			Bukkit.getWorlds().forEach(world -> {
				world.setTime(16000L);
				world.getEntities().forEach(entity -> entity.remove());
			});
			
			// Ranked
			RankedManager.instance.initialize(RankedManager.instance.getCurrentRankedGameName(), 
					RushScoreboard.KILLS, RushScoreboard.DEATHS, RushScoreboard.BROKENBEDS, RushScoreboard.WINS, RushScoreboard.LOOSES);
			
		} catch(Throwable e){
			e.printStackTrace();
		}
	}
	
	public void saveJsonConfig(){
		File configFile = new File(getDataFolder(), CONFIG);
		JsonUtils.save(configFile, configuration, true);
	}
	
	public void giveDefaultKit(BadblockPlayer player){
		player.clearInventory();
		PlayerKit kit = kits.get(configuration.defaultKit);
		
		if(kit == null){
			player.clearInventory();
			return;
		}
		
		player.getPlayerData().unlockNextLevel(kit);
		kit.giveKit(player);
	}
	
	public int getMinPlayers() {
		if (!configuration.enabledAutoTeamManager) return configuration.minPlayers;
		return configuration.minPlayersAutoTeam * getAPI().getTeams().size();
	}
	
}
