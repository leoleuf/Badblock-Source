package fr.badblock.bukkit.hub.v1;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.badblock.bukkit.hub.v1.commands.AdminCommand;
import fr.badblock.bukkit.hub.v1.commands.AuthCheckCommand;
import fr.badblock.bukkit.hub.v1.commands.AuthRemoveCommand;
import fr.badblock.bukkit.hub.v1.commands.HubGiveCommand;
import fr.badblock.bukkit.hub.v1.commands.NPCCommand;
import fr.badblock.bukkit.hub.v1.commands.RaceCommand;
import fr.badblock.bukkit.hub.v1.commands.SignCommand;
import fr.badblock.bukkit.hub.v1.commands.SpawnCommand;
import fr.badblock.bukkit.hub.v1.effectlib.EffectManager;
import fr.badblock.bukkit.hub.v1.inventories.LinkedInventoryEntity;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.hubchanger.HubChangerInventory;
import fr.badblock.bukkit.hub.v1.inventories.join.PlayerCustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.chests.objects.ChestLoader;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.BuildContestSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.CTSSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.DayZSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.FreeBuildSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.PearlsWarSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.PointOutSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.PvPBoxSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.PvPFactionSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.RushSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.SkyBlockSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.SkyWarsSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.SpeedUHCSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.SurvivalGamesSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.TowerRun4v4SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.TowerRunSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.TowerSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.buildcontest.BuildContest16SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.cts.CTS8v8SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.pearlswar.PearlsWar16SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.pointout.PointOut8v8SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.rush.Rush2v2NBSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.rush.Rush2v2SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.rush.Rush4v4NBSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.rush.Rush4v4SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.rush.Rush4x1SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.rush.Rush4x4NBSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.rush.Rush4x4SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.spaceballs.SpaceBalls4v4SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.speeduhc.SpeedUHCSoloSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.speeduhc.SpeedUHCTeamSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.survivalgames.SurvivalGames24NoTeamSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.survivalgames.SurvivalGames24SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.tower.Tower2v2SelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.tower.Tower4v4SelectorItem;
import fr.badblock.bukkit.hub.v1.listeners.entities.CreatureSpawnListener;
import fr.badblock.bukkit.hub.v1.listeners.entities.EntityCombustListener;
import fr.badblock.bukkit.hub.v1.listeners.entities.EntityExplodeListener;
import fr.badblock.bukkit.hub.v1.listeners.players.AsyncPlayerChatListener;
import fr.badblock.bukkit.hub.v1.listeners.players.DoubleJumpListener;
import fr.badblock.bukkit.hub.v1.listeners.players.InventoryClickListener;
import fr.badblock.bukkit.hub.v1.listeners.players.PlayerFakeEntityInteractListener;
import fr.badblock.bukkit.hub.v1.listeners.players.PlayerInteractListener;
import fr.badblock.bukkit.hub.v1.listeners.players.PlayerJoinListener;
import fr.badblock.bukkit.hub.v1.listeners.players.PlayerMoveListener;
import fr.badblock.bukkit.hub.v1.listeners.players.PlayerQuitListener;
import fr.badblock.bukkit.hub.v1.listeners.players.ReceiveCommandListener;
import fr.badblock.bukkit.hub.v1.listeners.protectors.HubMapProtector;
import fr.badblock.bukkit.hub.v1.listeners.vipzone.RaceListener;
import fr.badblock.bukkit.hub.v1.listeners.world.ServerSchedulerListener;
import fr.badblock.bukkit.hub.v1.rabbitmq.HubPacketThread;
import fr.badblock.bukkit.hub.v1.rabbitmq.listeners.BoosterUpdateListener;
import fr.badblock.bukkit.hub.v1.rabbitmq.listeners.BungeeWorkerListener;
import fr.badblock.bukkit.hub.v1.rabbitmq.listeners.DevPacketListener;
import fr.badblock.bukkit.hub.v1.rabbitmq.listeners.HubPacketListener;
import fr.badblock.bukkit.hub.v1.rabbitmq.listeners.SEntryInfosListener;
import fr.badblock.bukkit.hub.v1.rabbitmq.listeners.WorkerListener;
import fr.badblock.bukkit.hub.v1.tasks.BossBarTask;
import fr.badblock.bukkit.hub.v1.tasks.DropsTask;
import fr.badblock.bukkit.hub.v1.tasks.RebootTask;
import fr.badblock.bukkit.hub.v1.tasks.RequestBoosterTask;
import fr.badblock.bukkit.hub.v1.tasks.RequestNPCTask;
import fr.badblock.bukkit.hub.v1.tasks.RequestSignsTask;
import fr.badblock.bukkit.hub.v1.tasks.TaskListTask;
import fr.badblock.bukkit.hub.v1.utils.MountManager;
import fr.badblock.common.shoplinker.api.ShopLinkerAPI;
import fr.badblock.gameapi.BadblockPlugin;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.fakeentities.FakeEntity;
import fr.badblock.gameapi.packets.watchers.WatcherWitch;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.run.RunType;
import fr.badblock.gameapi.utils.ConfigUtils;
import fr.badblock.gameapi.utils.selections.CuboidSelection;
import fr.badblock.minecraftserver.BadblockSecurityManager;
import fr.badblock.rabbitconnector.RabbitConnector;
import fr.badblock.rabbitconnector.RabbitService;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadBlockHub extends BadblockPlugin {

	@Getter
	@Setter
	private static BadBlockHub instance;
	public CuboidSelection cuboid2;
	public CuboidSelection cuboid;
	public CuboidSelection vipPushCuboid;
	public CuboidSelection vipPortalCuboid;
	public EffectManager effectManager;
	public Gson gson = GameAPI.getGson();
	public Gson gsonExpose = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
	public HubPacketThread hubPacketThread;
	public RabbitConnector rabbitConnector;
	public RabbitService rabbitService;
	public ShopLinkerAPI	shopLinkerAPI;

	// NPCs
	private FakeEntity<?> npcxMalware;
	private FakeEntity<?> npcLeLanN;
	private Location npcxMalwareNormal;
	private Location npcxMalwareTurnBack;
	private Location npcLeLanNNormal;
	private Location npcLeLanNTurnBack;
	//private Location battleNpcLocation;
	//private CuboidSelection battleArena;
	//private EntityType battleNpcEntityType;
	//private int battlePlayersLimit;
	//private FakeEntity<?> battleNpc;

	@Override
	public void onLoad() {
		if (System.getSecurityManager() == null) System.setSecurityManager(new BadblockSecurityManager());
		System.out.println("Loaded BadblockHub! (onLoad())");
	}

	@Override
	public void onDisable() {
		this.getRabbitService().remove();
	}

	//@SuppressWarnings("deprecation")
	@Override
	public void onEnable(RunType runType) {
		if (Bukkit.getMaxPlayers() != 100) {
			Bukkit.shutdown();
			return;
		}
		long time = System.currentTimeMillis();
		// Singleton
		setInstance(this);
		// Configuration
		this.reloadConfig();
		// Working on fake players
		npcxMalwareNormal = ConfigUtils.getLocation(this, "npc.xmalware.normal");
		npcxMalwareTurnBack = ConfigUtils.getLocation(this, "npc.xmalware.turnback");
		npcLeLanNNormal = ConfigUtils.getLocation(this, "npc.lelann.normal");
		npcLeLanNTurnBack = ConfigUtils.getLocation(this, "npc.lelann.turnback");
		//battleNpcLocation = ConfigUtils.getLocation(this, "battle.npc.location");
		//battlePlayersLimit = ConfigUtils.get(this, "battle.playerslimit", 5);
		//battleArena = new CuboidSelection(ConfigUtils.getBlockLocationFromFile(this, "battle.arena.location1"), ConfigUtils.getBlockLocationFromFile(this, "battle.arena.location2"));
		//battleNpcEntityType = EntityType.fromName(ConfigUtils.get(this, "battle.npc.entitytype", EntityType.ZOMBIE.name()));
		//Battle.load();
		//battleNpc = MountManager.spawn(battleNpcLocation, battleNpcEntityType, WatcherZombie.class, false, false, false, false, GameAPI.i18n().get("hub.battle.npc_displayname")[0]);
		new ChestLoader(this);
		//npcLeLanN = GameAPI.getAPI().spawnFakePlayer(npcLeLanNNormal, new PlayerInfo(UUID.fromString("443fbe17-448c-4acd-bbed-2673652f18fc"), "LeLanN", new PropertyMap(), GameMode.ADVENTURE, 0, "LeLanN"));
		//npcxMalware = GameAPI.getAPI().spawnFakePlayer(npcxMalwareNormal, new PlayerInfo(UUID.fromString("89a1ccaf-baf0-4b4c-9cc0-d4e40f9b02aa"), "xMalware", new PropertyMap(), GameMode.ADVENTURE, 0, "xMalware"));
		npcLeLanN = GameAPI.getAPI().spawnFakeLivingEntity(npcLeLanNNormal, EntityType.WITCH, WatcherWitch.class);
		npcxMalware = GameAPI.getAPI().spawnFakeLivingEntity(npcxMalwareNormal, EntityType.WITCH, WatcherWitch.class);
		// Mounts
		new MountManager();
		// Effects
		effectManager = new EffectManager(this);
		// Tasks
		new TaskListTask();
		new RequestSignsTask();
		new RequestNPCTask();
		new RequestBoosterTask();
		new RebootTask();
		new BossBarTask();
		new DropsTask();
		
		// Player items
		PlayerCustomInventory.load();
		// Rabbit
		this.setRabbitConnector(RabbitConnector.getInstance());
		this.setRabbitService(this.getRabbitConnector().getService("default"));
		this.setHubPacketThread(new HubPacketThread(this.getRabbitService()));
		this.setShopLinkerAPI(new ShopLinkerAPI(this.getRabbitService()));
		// Hub changer
		CustomInventory.get(HubChangerInventory.class);
		new HubPacketListener();
		new DevPacketListener();
		new BoosterUpdateListener();
		new BungeeWorkerListener();
		new WorkerListener();
		// kamoulox
		Bukkit.getScheduler().runTaskLater(BadBlockHub.getInstance(), new Runnable()
		{
			@Override
			public void run()
			{
				new TowerSelectorItem();
				new CTSSelectorItem();
				new CTS8v8SelectorItem();
				new SkyWarsSelectorItem();
				new FreeBuildSelectorItem();
				new PearlsWarSelectorItem();
				new PvPBoxSelectorItem();
				new PvPFactionSelectorItem();
				new RushSelectorItem();
				new BuildContestSelectorItem();
				new BuildContest16SelectorItem();
				new SkyBlockSelectorItem();
				new DayZSelectorItem();
				new SpeedUHCSelectorItem();
				new SurvivalGamesSelectorItem();
				new TowerRunSelectorItem();
				new TowerSelectorItem();
				new Tower2v2SelectorItem();
				new TowerRunSelectorItem();
				new TowerRun4v4SelectorItem();
				new Tower4v4SelectorItem();
				new SurvivalGames24SelectorItem();
				new SurvivalGames24NoTeamSelectorItem();
				new SpeedUHCTeamSelectorItem();
				new SpeedUHCSoloSelectorItem();
				new SpaceBalls4v4SelectorItem();
				new Rush4x4SelectorItem();
				new Rush4x4NBSelectorItem();
				new Rush4x1SelectorItem();
				new Rush4v4SelectorItem();
				new Rush4v4NBSelectorItem();
				new Rush2v2SelectorItem();
				new Rush2v2NBSelectorItem();
				new PearlsWar16SelectorItem();
				new PointOut8v8SelectorItem();
				new PointOutSelectorItem();
			}
		}, 5 * 20);
		// SEntry
		new SEntryInfosListener();
		// Inventories interactions by fake-entities
		LinkedInventoryEntity.load();
		// Limit
		Location loc1 = ConfigUtils.getLocation(this, "limit.loc1");
		Location loc2 = ConfigUtils.getLocation(this, "limit.loc2");
		if (loc1 != null && loc2 != null) {
			loc1.setY(0);
			loc2.setY(256);
			this.setCuboid(new CuboidSelection(loc1, loc2));
		}
		loc1 = ConfigUtils.getLocation(this, "cuboid.loc1");
		loc2 = ConfigUtils.getLocation(this, "cuboid.loc2");
		if (loc1 != null && loc2 != null) {
			loc1.setY(0);
			loc2.setY(256);
			this.setCuboid2(new CuboidSelection(loc1, loc2));
		}
		// VIP Portal
		Location defaultLocation = new Location(Bukkit.getWorld("world"), 0, 0, 0);
		loc1 = ConfigUtils.getBlockLocationFromFile(this, "viplimit.portal.loc1");
		if (loc1 == null) {
			ConfigUtils.saveLocation(this, "viplimit.portal.loc1", defaultLocation);
			loc1 = defaultLocation;
		}
		loc2 = ConfigUtils.getBlockLocationFromFile(this, "viplimit.portal.loc2");
		if (loc2 == null) {
			ConfigUtils.saveLocation(this, "viplimit.portal.loc2", defaultLocation);
			loc2 = defaultLocation;
		}
		this.setVipPortalCuboid(new CuboidSelection(loc1, loc2));
		// VIP Push
		loc1 = ConfigUtils.getBlockLocationFromFile(this, "viplimit.push.loc1");
		if (loc1 == null) {
			ConfigUtils.saveLocation(this, "viplimit.push.loc1", defaultLocation);
			loc1 = defaultLocation;
		}
		loc2 = ConfigUtils.getBlockLocationFromFile(this, "viplimit.push.loc2");
		if (loc2 == null) {
			ConfigUtils.saveLocation(this, "viplimit.push.loc2", defaultLocation);
			loc2 = defaultLocation;
		}
		this.setVipPushCuboid(new CuboidSelection(loc1, loc2));
		// Commands
		new SignCommand();
		new NPCCommand();
		new HubGiveCommand();
		new AdminCommand();
		new SpawnCommand();
		new AuthCheckCommand();
		new AuthRemoveCommand();
		new RaceCommand();
		// Reload configuration
		this.reloadConfig();
		// World manage
		Bukkit.getWorlds().forEach(world -> {
			world.getEntitiesByClasses(Animals.class, Creature.class).forEach(entity -> entity.remove());
			world.setFullTime(600);
			world.setPVP(false);
			world.setStorm(false);
			world.setThunderDuration(0);
			world.setThundering(false);
			world.setWeatherDuration(0);
			world.setTime(600);
		});

		// API compatibility
		getAPI().formatChat(true, false, "hub");
		getAPI().getBadblockScoreboard().doGroupsPrefix();
		getAPI().setMapProtector(new HubMapProtector());
		getAPI().managePortals(new File(getDataFolder(), "portals"));

		// Registering all events
		new AsyncPlayerChatListener();
		new CreatureSpawnListener();
		new EntityCombustListener();
		new EntityExplodeListener();
		new InventoryClickListener();
		new DoubleJumpListener();
		new PlayerInteractListener();
		new PlayerJoinListener();
		new ServerSchedulerListener();
		new PlayerMoveListener();
		new ReceiveCommandListener();
		new PlayerQuitListener();
		new PlayerFakeEntityInteractListener();
		new RaceListener(this);
		//new BattleListener();

		// Reload players
		Bukkit.getOnlinePlayers().forEach(player -> PlayerJoinListener.load((BadblockPlayer) player));
		System.out.println("[ChunkLoader] Searching chunks...");
		GameAPI.getAPI().setLightChunks(cuboid2, false);
		GameAPI.getAPI().setEmptyChunks(cuboid2, true);
		GameAPI.getAPI().loadChunks(cuboid2, 1);
		System.out.println("[ChunkLoader] Loaded chunks");

		/*TaskManager.scheduleAsyncRepeatingTask("christmas_starparticles", new Runnable() {
		 * // remove after christmas
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				for (Location loc : locations) {
					Location location = loc.clone();
					float radius = 3f * 0.5f / MathUtils.SQRT_3;
					for (int i = 0; i < 3.5 * 2; i++) {
						double xRotation = i * Math.PI / 3;
						for (int x = 0; x < 50; x++) {
							double angle = 2 * Math.PI * x / 50;
							double height = RandomUtils.random.nextFloat() * 3.5;
							Vector v = new Vector(Math.cos(angle), 0, Math.sin(angle));
							v.multiply((3.5 - height) * radius / 3.5);
							v.setY(0.5 + height);
							VectorUtils.rotateAroundAxisX(v, xRotation);
							location.add(v);
							ParticleEffect.SNOW_SHOVEL.display(location, Long.MAX_VALUE);
							location.subtract(v);
							VectorUtils.rotateAroundAxisX(v, Math.PI);
							VectorUtils.rotateAroundAxisY(v, Math.PI / 2);
							location.add(v);
							ParticleEffect.SNOW_SHOVEL.display(location, 3);
							location.subtract(v);
						}
					}
				}
			}
		}, 0, 1);*/
		System.out.println("[HUB] Loaded BadBlockHub in " + String.format("%.2f", ((System.currentTimeMillis() - time) * 1.0F)) + " ms.");
		// christmas TODO remove
		//new CreeperTask();
		//new SnowmanTask();
	}

}
