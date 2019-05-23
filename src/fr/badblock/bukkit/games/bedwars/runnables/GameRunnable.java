package fr.badblock.bukkit.games.bedwars.runnables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.WorldBorder;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.bedwars.BedWarsAchievementList;
import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.bukkit.games.bedwars.configuration.BedWarsConfiguration.SpawnableItem;
import fr.badblock.bukkit.games.bedwars.configuration.BedWarsMapConfiguration;
import fr.badblock.bukkit.games.bedwars.configuration.floatingtexts.FloatingText;
import fr.badblock.bukkit.games.bedwars.configuration.floatingtexts.MapFloatingText;
import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
import fr.badblock.bukkit.games.bedwars.inventories.LinkedInventoryEntity;
import fr.badblock.bukkit.games.bedwars.players.BedWarsData;
import fr.badblock.bukkit.games.bedwars.players.BedWarsScoreboard;
import fr.badblock.bukkit.games.bedwars.result.BedWarsResults;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.game.rankeds.RankedManager;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.players.data.PlayerAchievementState;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.ConfigUtils;
import fr.badblock.gameapi.utils.general.TimeUnit;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class GameRunnable extends BukkitRunnable {
	public boolean forceEnd = false;
	public static int    time 	= 60 * 30;
	public static int	   size = 200;
	public static int    doneTime = 0;
	public static int    witherDeath = 60 * 3;
	public static boolean damage = false;

	public GameRunnable(BedWarsMapConfiguration config){
		GameAPI.getAPI().getGameServer().setGameState(GameState.RUNNING);
		GameAPI.getAPI().getGameServer().saveTeamsAndPlayersForResult();

		Bukkit.getWorlds().forEach(world -> {
			world.setTime(config.getTime());
			world.getEntities().forEach(entity -> {
				if(entity.getType() != EntityType.PLAYER && !entity.getType().equals(EntityType.ARMOR_STAND))
					entity.remove();
			});
		});

		PluginBedWars.getInstance().getConfiguration().items.forEach(item -> new ItemSpawnRunnable(Material.matchMaterial(item.item), item.ticks).start());
		if (!PluginBedWars.getInstance().getMapConfiguration().getAllowBows()) {
			remove(Material.BOW);
			remove(Material.ARROW);
		}

		new TierRunnable();

		LinkedInventoryEntity.load(PluginBedWars.getInstance().getMapConfiguration());

		for(BadblockTeam team : GameAPI.getAPI().getTeams()){


			for(BadblockPlayer p : team.getOnlinePlayers()){
				handle(p);

				PluginBedWars.getInstance().giveDefaultKit(p);
			}

		}

		Bukkit.getWorlds().forEach(world -> {
			world.setTime(config.getTime());
			world.getEntities().forEach(entity -> {
				if(entity.getType() != EntityType.PLAYER) entity.remove();
			});
		});
		for(BadblockTeam team : GameAPI.getAPI().getTeams()) for(BadblockPlayer p : team.getOnlinePlayers()) handle(p);
		GameAPI.getAPI().getJoinItems().doClearInventory(false);
		GameAPI.getAPI().getJoinItems().end();
		new TrackRunnable();

	}
	@Override
	public void run() {
		doneTime++;

		if (doneTime == 2)
		{

			new BlockRotationRunnable(PluginBedWars.getInstance().getMapConfiguration().getBlockRotations());

			for (MapFloatingText mapFloatingText : PluginBedWars.getInstance().getMapConfiguration().getFloatingTexts())
			{
				FloatingText floatingText = mapFloatingText.getHandle();
				Location location = ConfigUtils.convertStringToLocation(floatingText.location);
				String text = floatingText.text;
				spawnNametag(location, text);
			}

			for(SpawnableItem item : PluginBedWars.getInstance().getConfiguration().items){
				new ItemSpawnRunnable(Material.matchMaterial(item.item), item.ticks).start();
			}
		}

		GameAPI.setJoinable(GameRunnable.time < 900);
		BukkitUtils.getPlayers().stream().filter(player -> player.getCustomObjective() != null).forEach(player -> player.getCustomObjective().generate());
		if(time == 2){
			damage = true;
			for(BadblockPlayer player : GameAPI.getAPI().getRealOnlinePlayers()) if(player.getTeam() != null) player.pseudoJail(player.getTeam().teamData(BedWarsTeamData.class).getRespawnLocation(), 300.0d);
		}

		int size = GameAPI.getAPI().getTeams().size();
		List<BadblockTeam> to = new ArrayList<>();
		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			if(team.getOnlinePlayers().size() == 0){
				GameAPI.getAPI().getGameServer().cancelReconnectionInvitations(team);
				to.add(team);

				new TranslatableString("bedwars.team-loose", team.getChatName()).broadcast();
			}
		}

		if(GameAPI.getAPI().getTeams().size() > 1) to.forEach(GameAPI.getAPI()::unregisterTeam);
		if(GameAPI.getAPI().getTeams().stream().filter(team -> team.playersCurrentlyOnline() > 0).count() <= 1 || forceEnd){
			cancel();
			Iterator<BadblockTeam> iterator = GameAPI.getAPI().getTeams().iterator();
			BadblockTeam winner = null;
			if(iterator.hasNext()) winner = iterator.next();
			GameAPI.getAPI().getGameServer().setGameState(GameState.FINISHED);
			Location winnerLocation = PluginBedWars.getInstance().getMapConfiguration().getSpawnLocation();
			Location looserLocation = winnerLocation.clone().add(0d, 7d, 0d);
			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bp = (BadblockPlayer) player;
				bp.heal();
				bp.clearInventory();
				bp.setInvulnerable(true);
				double badcoins = bp.inGameData(BedWarsData.class).getScore() / 4; // 10
				double xp = bp.inGameData(BedWarsData.class).getScore() / 2; // 5
				if (winner != null) {
					if(winner.equals(bp.getTeam())){
						bp.getPlayerData().addRankedPoints(3);
						bp.teleport(winnerLocation);
						bp.setAllowFlight(true);
						bp.setFlying(true);
						new BukkitRunnable() {
							int count = 5;
							@Override
							public void run() {
								count--;
								bp.teleport(winnerLocation);
								bp.setAllowFlight(true);
								bp.setFlying(true);
								if(count == 0) cancel();
							}
						}.runTaskTimer(GameAPI.getAPI(), 5L, 5L);

						bp.sendTranslatedTitle("bedwars.title-win", winner.getChatName());
						bp.getPlayerData().incrementStatistic("bedwars", BedWarsScoreboard.WINS);
						bp.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), BedWarsScoreboard.WINS, 1);

						incrementAchievements(bp, BedWarsAchievementList.BEDWARS_WIN_1, BedWarsAchievementList.BEDWARS_WIN_2,
								BedWarsAchievementList.BEDWARS_WIN_3, BedWarsAchievementList.BEDWARS_WIN_4);

						if(time <= 600){
							incrementAchievements(bp, BedWarsAchievementList.BEDWARS_RUSHER_1,
									BedWarsAchievementList.BEDWARS_RUSHER_2,
									BedWarsAchievementList.BEDWARS_RUSHER_3,
									BedWarsAchievementList.BEDWARS_RUSHER_4);
						}

					} else {
						bp.getPlayerData().addRankedPoints(-2);
						badcoins = badcoins / 1.5d;
						bp.jailPlayerAt(looserLocation);
						bp.sendTranslatedTitle("bedwars.title-loose", winner.getChatName());

						if(bp.getBadblockMode() == BadblockMode.PLAYER) {
							bp.getPlayerData().incrementStatistic("bedwars", BedWarsScoreboard.LOOSES);
							bp.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), BedWarsScoreboard.LOOSES, 1);
						}
					}
				}
				if(badcoins > 20 * bp.getPlayerData().getBadcoinsMultiplier()) badcoins = 20 * bp.getPlayerData().getBadcoinsMultiplier();
				if(xp > 50 * bp.getPlayerData().getXpMultiplier()) xp = 50 * bp.getPlayerData().getXpMultiplier();
				int rbadcoins = badcoins < 2 ? 2 : (int) badcoins;
				int rxp	= xp < 5 ? 5 : (int) xp;
				bp.getPlayerData().addBadcoins(rbadcoins, true);
				bp.getPlayerData().addXp(rxp, true);
				new BukkitRunnable(){
					@Override
					public void run(){
						if(bp.isOnline()){
							bp.sendTranslatedActionBar("bedwars.win", rbadcoins, rxp);
						}
					}
				}.runTaskTimer(GameAPI.getAPI(), 0, 30L);
				if (bp.getCustomObjective() != null) bp.getCustomObjective().generate();
			}
			String rankedGameName = RankedManager.instance.getCurrentRankedGameName();
			for (BadblockPlayer player : BukkitUtils.getPlayers()) {
				RankedManager.instance.calcPoints(rankedGameName, player, () -> {
					double kills = RankedManager.instance.getData(rankedGameName, player, BedWarsScoreboard.KILLS);
					double deaths = RankedManager.instance.getData(rankedGameName, player, BedWarsScoreboard.DEATHS);
					double wins = RankedManager.instance.getData(rankedGameName, player, BedWarsScoreboard.WINS);
					double looses = RankedManager.instance.getData(rankedGameName, player, BedWarsScoreboard.LOOSES);
					double brokenBeds = RankedManager.instance.getData(rankedGameName, player, BedWarsScoreboard.BROKENBEDS);
					double total = ((kills / 0.5D) + (wins * 4) + ((kills * brokenBeds) + (brokenBeds * 2) * (kills / (deaths > 0 ? deaths : 1)))) / (1 + looses);
					return (long) total;
				});
			}
			RankedManager.instance.fill(rankedGameName);

			if (winner != null)
				new BedWarsResults(TimeUnit.SECOND.toShort(time, TimeUnit.SECOND, TimeUnit.HOUR), winner);
			if (winner != null)
				new EndEffectRunnable(winnerLocation, winner).runTaskTimer(GameAPI.getAPI(), 0, 1L);

			new KickRunnable().runTaskTimer(GameAPI.getAPI(), 0, 20L);
		} else if(size == 0){
			cancel();
			Bukkit.shutdown();
			return;
		}

		if (time > 0)
		{
			time--;
			if (time == 0)
			{
				for (BadblockTeam team : GameAPI.getAPI().getTeams())
				{
					BedWarsTeamData td = team.teamData(BedWarsTeamData.class);
					if (td == null)
					{
						continue;
					}

					if (td.getFirstBedPart() != null)
					{
						td.getFirstBedPart().getBlock().setType(Material.AIR);
					}

					if (td.getSecondBedPart() != null)
					{
						td.getSecondBedPart().getBlock().setType(Material.AIR);
					}

					td.broked(false, null);
					WorldBorder wb = PluginBedWars.getInstance().getMapConfiguration().getSpawnLocation().getWorld().getWorldBorder();

					wb.setCenter(PluginBedWars.getInstance().getMapConfiguration().getSpawnLocation());
					wb.setWarningTime(3);
					wb.setWarningDistance(2);
					wb.setDamageAmount(0.5d);
					wb.setSize(200 * 2, time);
				}

				BukkitUtils.getAllPlayers().forEach(player -> player.playSound(Sound.ENDERDRAGON_DEATH));
				BukkitUtils.getAllPlayers().forEach(player -> player.sendTranslatedTitle("bedwars.suddendeath"));
				BukkitUtils.getAllPlayers().forEach(player -> player.sendTranslatedMessage("bedwars.suddendeathchat"));
			}
		}
		else
		{
			if (witherDeath > 0)
			{
				witherDeath--;
			}
			else
			{

				if (witherDeath == 0)
				{

					// caca
					for (BadblockPlayer poo : BukkitUtils.getAllPlayers())
					{
						poo.playSound(Sound.WITHER_DEATH);
						BukkitUtils.getAllPlayers().forEach(player -> player.sendTranslatedTitle("bedwars.witherdeath"));
						BukkitUtils.getAllPlayers().forEach(player -> player.sendTranslatedMessage("bedwars.witherdeathchat"));
					}

					witherDeath = -1;
				}

				for (BadblockPlayer po : BukkitUtils.getPlayers())
				{
					if (GameMode.SPECTATOR.equals(po.getGameMode()))
					{
						continue;
					}

					if (po.getTeam() == null)
					{
						continue;
					}

					po.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 2));
				}

			}

			WorldBorder wb = PluginBedWars.getInstance().getMapConfiguration().getSpawnLocation().getWorld().getWorldBorder();
			wb.setCenter(PluginBedWars.getInstance().getMapConfiguration().getSpawnLocation());
			wb.setWarningTime(3);
			wb.setWarningDistance(2);
			wb.setDamageAmount(0.5d);
			size--;
			wb.setSize(size * 2, 60);
		}

		time++;
	}

	public static ArmorStand spawnNametag(Location location, String text) {
		ArmorStand as = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND); //Spawn the ArmorStand

		as.setGravity(false); //Make sure it doesn't fall
		as.setCanPickupItems(false); //I'm not sure what happens if you leave this as it is, but you might as well disable it
		as.setCustomName(text); //Set this to the text you want
		as.setCustomNameVisible(true); //This makes the text appear no matter if your looking at the entity or not
		as.setVisible(false); //Makes the ArmorStand invisible

		return as;
	}

	public static void handle(BadblockPlayer player) {
		BadblockTeam team = player.getTeam();
		if (team == null) return;
		Location location = team.teamData(BedWarsTeamData.class).getRespawnLocation();
		player.changePlayerDimension(BukkitUtils.getEnvironment( PluginBedWars.getInstance().getMapConfiguration().getDimension() ));
		player.teleport(location);
		player.setGameMode(GameMode.SURVIVAL);
		if (player.getCustomObjective() == null) new BedWarsScoreboard(player);
		player.getCustomObjective().generate();
	}

	private void remove(Material m) {
		Iterator<Recipe> it = Bukkit.getServer().recipeIterator();
		Recipe recipe;
		while(it.hasNext()) {
			recipe = it.next();
			if (recipe != null && recipe.getResult().getType() == m) it.remove();
		}
	}

	private static void incrementAchievements(BadblockPlayer player, PlayerAchievement... achievements){
		for(PlayerAchievement achievement : achievements){
			PlayerAchievementState state = player.getPlayerData().getAchievementState(achievement);
			state.progress(1.0d);
			state.trySucceed(player, achievement);
		}
		player.saveGameData();
	}
}
