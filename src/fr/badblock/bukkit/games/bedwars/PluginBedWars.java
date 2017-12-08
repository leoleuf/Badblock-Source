package fr.badblock.bukkit.games.bedwars;

import fr.badblock.bukkit.games.bedwars.commands.BedWarsCommand;
import fr.badblock.bukkit.games.bedwars.commands.GameCommand;
import fr.badblock.bukkit.games.bedwars.configuration.BedWarsConfiguration;
import fr.badblock.bukkit.games.bedwars.configuration.BedWarsMapConfiguration;
import fr.badblock.bukkit.games.bedwars.listeners.*;
import fr.badblock.bukkit.games.bedwars.players.BedWarsScoreboard;
import fr.badblock.bukkit.games.bedwars.runnables.PreStartRunnable;
import fr.badblock.gameapi.BadblockPlugin;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.achievements.AchievementList;
import fr.badblock.gameapi.game.GameServer.WhileRunningConnectionTypes;
import fr.badblock.gameapi.game.rankeds.RankedManager;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.kits.PlayerKit;
import fr.badblock.gameapi.run.BadblockGame;
import fr.badblock.gameapi.run.RunType;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.GameRules;
import fr.badblock.gameapi.utils.general.JsonUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;

import java.io.File;
import java.util.Map;

public class PluginBedWars extends BadblockPlugin {
	
	@Getter private static PluginBedWars instance;
	public static File MAP;
	private static final String CONFIG = "config.json";
	private static final String TEAMS_CONFIG = "teams.yml";
	private static final String TEAMS_CONFIG_INVENTORY = "teamsInventory.yml";
	private static final String VOTES_CONFIG = "votes.json";
	private static final String KITS_CONFIG_INVENTORY  = "kitInventory.yml";
	private static final String MAPS_CONFIG_FOLDER = "maps";
	private static final String SHOP_FOLDER = "shops";

	@Getter@Setter
	private int maxPlayers;
	@Getter
	private BedWarsConfiguration configuration;
	@Getter@Setter
	private BedWarsMapConfiguration mapConfiguration;
	@Getter
	private Map<String, PlayerKit> kits;

	@Override
	public void onEnable(RunType runType){
		if(runType == RunType.LOBBY) return; //Stop si lobby
		try {
            AchievementList list = BedWarsAchievementList.instance;
            BadblockGame.BEDWARS.setGameData(() -> list);
            instance = this;
			if(!getDataFolder().exists()) if (!getDataFolder().mkdir()) return;
            File votesFile = new File(getDataFolder(), VOTES_CONFIG);
            if(!votesFile.exists()) if (!votesFile.createNewFile()) return;
            File configFile = new File(getDataFolder(), CONFIG);
			GameRules.doDaylightCycle.setGameRule(false);
			GameRules.spectatorsGenerateChunks.setGameRule(false);
			GameRules.doFireTick.setGameRule(false);
			BadblockGame.BEDWARS.use();
			this.configuration = JsonUtils.load(configFile, BedWarsConfiguration.class);
			JsonUtils.save(configFile, configuration, true);
			File teamsFile = new File(getDataFolder(), TEAMS_CONFIG);
			FileConfiguration teams = YamlConfiguration.loadConfiguration(teamsFile);
			getAPI().registerTeams(configuration.maxPlayersInTeam, teams);
			getAPI().setDefaultKitContentManager(false);
			maxPlayers = getAPI().getTeams().size() * configuration.maxPlayersInTeam;
            BukkitUtils.setMaxPlayers(maxPlayers);
			kits = getAPI().loadKits(GameAPI.getInternalGameName());
            teams.save(teamsFile);
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
			getAPI().setMapProtector(new BedWarsMapProtector());
			getAPI().enableAntiSpawnKill();
			getAPI().getGameServer().whileRunningConnection(WhileRunningConnectionTypes.SPECTATOR);
			new MoveListener();
			new DeathListener();
			new JoinListener();
			new QuitListener();
			new PartyJoinListener();
			new BedExplodeListener();
			new SheepListener();
			new FakeEntityInteractListener();
			getAPI().getBadblockScoreboard().beginVote(JsonUtils.loadArray(votesFile));
			new PreStartRunnable().runTaskTimer(GameAPI.getAPI(), 0, 30L);
			MAP = new File(getDataFolder(), MAPS_CONFIG_FOLDER);
			new BedWarsCommand(MAP);
			new GameCommand();
			Bukkit.getWorlds().forEach(world -> {
				world.setTime(16000L);
				world.getEntities().forEach(Entity::remove);
			});
			RankedManager.instance.initialize(RankedManager.instance.getCurrentRankedGameName(), BedWarsScoreboard.KILLS, BedWarsScoreboard.DEATHS, BedWarsScoreboard.BROKENBEDS, BedWarsScoreboard.WINS, BedWarsScoreboard.LOOSES);
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
		if(kit == null) {
			player.clearInventory();
			return;
		}
		player.getPlayerData().unlockNextLevel(kit);
		kit.giveKit(player);
	}

	//jsais pas a quoi elle sert cette méthode
	/**public int getMinPlayers() {
		if (!configuration.enabledAutoTeamManager) return configuration.minPlayers;
		return configuration.minPlayersAutoTeam * getAPI().getTeams().size();
	}**/
	
}
