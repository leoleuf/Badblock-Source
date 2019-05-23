package fr.badblock.bukkit.games.uhc.doublerun;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import com.google.gson.Gson;

import fr.badblock.bukkit.games.uhc.doublerun.commands.GameCommand;
import fr.badblock.bukkit.games.uhc.doublerun.commands.UHCCommand;
import fr.badblock.bukkit.games.uhc.doublerun.configuration.UHCConfiguration;
import fr.badblock.bukkit.games.uhc.doublerun.configuration.UHCConfiguration.MapCustomEnchantment;
import fr.badblock.bukkit.games.uhc.doublerun.configuration.UHCConfiguration.MapCustomRecipe;
import fr.badblock.bukkit.games.uhc.doublerun.listeners.ChunkUnloadListener;
import fr.badblock.bukkit.games.uhc.doublerun.listeners.CraftListener;
import fr.badblock.bukkit.games.uhc.doublerun.listeners.DamageListener;
import fr.badblock.bukkit.games.uhc.doublerun.listeners.DeathListener;
import fr.badblock.bukkit.games.uhc.doublerun.listeners.HostListener;
import fr.badblock.bukkit.games.uhc.doublerun.listeners.JoinListener;
import fr.badblock.bukkit.games.uhc.doublerun.listeners.MoveListener;
import fr.badblock.bukkit.games.uhc.doublerun.listeners.PartyJoinListener;
import fr.badblock.bukkit.games.uhc.doublerun.listeners.QuitListener;
import fr.badblock.bukkit.games.uhc.doublerun.listeners.UHCMapProtector;
import fr.badblock.bukkit.games.uhc.doublerun.players.UHCScoreboard;
import fr.badblock.bukkit.games.uhc.doublerun.runnables.PreStartRunnable;
import fr.badblock.bukkit.games.uhc.doublerun.runnables.game.ChunkLoaderRunnable;
import fr.badblock.bukkit.games.uhc.doublerun.utils.Schematic;
import fr.badblock.bukkit.games.uhc.doublerun.utils.SchematicLoader;
import fr.badblock.gameapi.BadblockPlugin;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.achievements.AchievementList;
import fr.badblock.gameapi.game.GameServer.WhileRunningConnectionTypes;
import fr.badblock.gameapi.game.rankeds.RankedManager;
import fr.badblock.gameapi.run.BadblockGame;
import fr.badblock.gameapi.run.BadblockGameData;
import fr.badblock.gameapi.run.RunType;
import fr.badblock.gameapi.utils.BorderUtils;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.GameRules;
import fr.badblock.gameapi.utils.general.JsonUtils;
import fr.badblock.gameapi.utils.selections.CuboidSelection;
import lombok.Getter;
import lombok.Setter;

public class PluginUHC extends BadblockPlugin {
	@Getter private static PluginUHC instance;

	public static 	     File   MAP;

	private static final String CONFIG 		   		   = "config.json";
	private static final String TEAMS_CONFIG 		   = "teams.yml";
	private static final String TEAMS_CONFIG_INVENTORY = "teamsInventory.yml";
	private static final String KITS_CONFIG_INVENTORY  = "kitInventory.yml";

	@Getter@Setter
	private int 			     maxPlayers;
	@Getter
	private UHCConfiguration    configuration;

	@Getter
	private Location defaultLoc;

	private Schematic schematic;

	@Getter
	private List<Block> spawnBlocks = new ArrayList<>();

	@Override
	public void onEnable(RunType runType){
		AchievementList list = UHCAchievementList.instance;

		BadblockGame.UHCSPEED.setGameData(new BadblockGameData() {
			@Override
			public AchievementList getAchievements() {
				return list;
			}
		});

		instance = this;

		if(runType == RunType.LOBBY)
			return;

		this.defaultLoc = new Location(Bukkit.getWorlds().get(0), 0, 90, 0);

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

			BadblockGame.UHCSPEED.use();

			File configFile    = new File(getDataFolder(), CONFIG);
			this.configuration = JsonUtils.load(configFile, UHCConfiguration.class);

			JsonUtils.save(configFile, configuration, true);

			if(configuration.allowTeams){

				File 			  teamsFile 	= new File(getDataFolder(), TEAMS_CONFIG);
				FileConfiguration teams 		= YamlConfiguration.loadConfiguration(teamsFile);

				getAPI().registerTeams(configuration.maxPlayersInTeam, teams);

				try { teams.save(teamsFile); } catch (IOException unused){}
			}

			getAPI().setDefaultKitContentManager(true);

			maxPlayers = !configuration.allowTeams ? configuration.maxPlayersInTeam : getAPI().getTeams().size() * configuration.maxPlayersInTeam;
			try {
				BukkitUtils.setMaxPlayers(maxPlayers);
			} catch (ReflectiveOperationException e) {
				e.printStackTrace();
			}

			// Chargement des fonctionnalités de l'API non utilisées par défaut

			getAPI().getBadblockScoreboard().doBelowNameHealth();
			getAPI().getBadblockScoreboard().doTabListHealth();
			if(configuration.allowTeams)
				getAPI().getBadblockScoreboard().doTeamsPrefix();
			else getAPI().getBadblockScoreboard().doGroupsPrefix();;

			getAPI().getBadblockScoreboard().doOnDamageHologram();

			getAPI().formatChat(true, true);

			if(configuration.allowTeams)
				getAPI().getJoinItems().registerTeamItem(3, new File(getDataFolder(), TEAMS_CONFIG_INVENTORY));
			getAPI().getJoinItems().registerAchievementsItem(configuration.allowTeams ? 5 : 4, BadblockGame.UHCSPEED);
			getAPI().getJoinItems().registerLeaveItem(8, configuration.fallbackServer);

			getAPI().setMapProtector(new UHCMapProtector());
			getAPI().enableAntiSpawnKill();

			getAPI().getGameServer().whileRunningConnection(WhileRunningConnectionTypes.SPECTATOR);

			System.out.println(new Gson().toJson(getConfiguration().randomBreaks));

			World w = Bukkit.getWorlds().get(0);
			Location location = new Location(w, 0, 200, 0);

			new ChunkUnloadListener();

			File file = new File(getDataFolder(), "uhcspawn.schematic");
			schematic = SchematicLoader.loadSchematic(file);
			spawnBlocks = SchematicLoader.pasteSchematic(w, location, schematic);

			ChunkLoaderRunnable.put(location.getChunk());
			ChunkLoaderRunnable.loadedChunks.add(location.getChunk());

			Location lbo = location.clone().add(-120, 0, 120);
			Location lbz = location.clone().add(120, 0, -120);
			CuboidSelection sl = new CuboidSelection(lbo, lbz);
			for (Block b : sl.getBlocks())
			{
				ChunkLoaderRunnable.loadedChunks.add(b.getChunk());
				ChunkLoaderRunnable.put(b.getChunk());
			}

			new MoveListener();
			new DeathListener();
			new JoinListener();
			new HostListener();
			new QuitListener();
			new PartyJoinListener();
			new CraftListener();
			new DamageListener();
			new QuitListener();

			new PreStartRunnable().runTaskTimer(GameAPI.getAPI(), 0, 30L);
			new GameCommand();
			new UHCCommand();

			BorderUtils.setBorder(configuration.map.overworldSize);

			if(configuration.map.manageNether)
				BorderUtils.setBorder(configuration.getNether(), configuration.map.netherSize);

			Bukkit.getWorlds().forEach(world -> {
				world.setTime(2000L);
				world.getEntities().forEach(entity -> entity.remove());
			});

			for (MapCustomRecipe cr : getConfiguration().recipes)
			{
				Iterator<Recipe> recipes = getServer().recipeIterator();

				while (recipes.hasNext())
				{
					Recipe recipe = recipes.next();

					if (recipe.getResult().getType().name().equalsIgnoreCase(cr.getResult().getName()))
					{
						recipes.remove();
					}
				}

				ItemStack result = new ItemStack(UHCMapProtector.getFrom(cr.getResult().getName()), cr.getResult().getAmount(), (byte) cr.getResult().getData());
				if (cr.getResult().getEnchantments() != null)
				{
					for (MapCustomEnchantment enchantment : cr.getResult().getEnchantments())
					{
						result.addEnchantment(enchantment.toEnchantment(), enchantment.getLevel());
					}
				}

				ShapedRecipe expBottle = new ShapedRecipe(result);

				expBottle.shape(cr.getFirstLine(), cr.getSecondLine(), cr.getThirdLine());

				for (Entry<String, String> entry : cr.getIdentifiers().entrySet())
				{
					expBottle.setIngredient(entry.getKey().toCharArray()[0], UHCMapProtector.getFrom(entry.getValue())	);
				}

				getServer().addRecipe(expBottle);
			}

			// Ranked
			RankedManager.instance.initialize(RankedManager.instance.getCurrentRankedGameName(), 
					UHCScoreboard.KILLS, UHCScoreboard.DEATHS, UHCScoreboard.WINS, UHCScoreboard.LOOSES);

		} catch(Throwable e){
			e.printStackTrace();
		}
	}

	public void saveJsonConfig(){
		File configFile = new File(getDataFolder(), CONFIG);
		JsonUtils.save(configFile, configuration, true);
	}


	public void removeSpawn(){
		getConfiguration().spawnZone.getHandle().getBlocks().forEach(block -> block.setType(Material.AIR));

	}
}
