package fr.badblock.bukkit.games.uhc.meetup.runnables.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.uhc.meetup.PluginUHC;
import fr.badblock.bukkit.games.uhc.meetup.players.UHCData;
import fr.badblock.bukkit.games.uhc.meetup.players.UHCScoreboard;
import fr.badblock.bukkit.games.uhc.meetup.result.UHCResults;
import fr.badblock.bukkit.games.uhc.meetup.runnables.EndEffectRunnable;
import fr.badblock.bukkit.games.uhc.meetup.runnables.KickRunnable;
import fr.badblock.game.core18R3.players.data.GameKit;
import fr.badblock.game.core18R3.players.data.GameKit.KitLevel;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.game.rankeds.RankedCalc;
import fr.badblock.gameapi.game.rankeds.RankedManager;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.BorderUtils;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class GameRunnable extends BukkitRunnable {
	public static GameRunnable ins;
	public static boolean forceEnd = false;

	public static boolean firstTeamCheck = false;
	public static boolean pve;

	public int pastTime;
	public int totalTime;
	boolean enabled = true;

	public BadblockTeam winner;
	public BadblockPlayer winnerPlayer;

	public GameRunnable(){
		ins = this;

		this.totalTime = PluginUHC.getInstance().getConfiguration().time.totalTime * 60;
		this.pastTime  = 0;

		PluginUHC.getInstance().getSpawnBlocks().forEach(block -> block.setType(Material.AIR));
	}

	private int countEntities(){
		List<BadblockTeam> to = GameAPI.getAPI().getTeams().stream().filter(team -> team.getOnlinePlayers().isEmpty()).collect(Collectors.toList());

		for(BadblockTeam team : to){
			GameAPI.getAPI().getGameServer().cancelReconnectionInvitations(team);

			if (firstTeamCheck)
			{
				new TranslatableString("uhcspeed.team-loose", team.getChatName()).broadcast();;
			}
			GameAPI.getAPI().unregisterTeam(team);
		}

		firstTeamCheck = true;
		if (forceEnd) return 0;

		if(PluginUHC.getInstance().getConfiguration().allowTeams){
			return (int) GameAPI.getAPI().getTeams().stream().filter(team -> team.getOnlinePlayers().size() > 0).count();
		} else {
			return GameAPI.getAPI().getRealOnlinePlayers().size();
		}
	}

	private BadblockTeam getTeam(){
		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			if(team.getOnlinePlayers().size() == 0)
				continue;

			return team;
		}

		return null;
	}

	private BadblockPlayer getPlayer(){
		for(BadblockPlayer player : GameAPI.getAPI().getOnlinePlayers()){
			if(player.getBadblockMode() == BadblockMode.SPECTATOR)
				continue;

			return player;
		}

		return null;
	}

	private void doEnd(){
		int entities = countEntities();

		if(entities == 0){
			Bukkit.shutdown();
			return;
		}

		winner 		= getTeam();
		BadblockPlayer winnerPlayer = winner == null ? getPlayer() : null;
		if (winnerPlayer != null) {
			winnerPlayer.getPlayerData().addRankedPoints(3);
		}
		if (winner != null) {
			winner.getOnlinePlayers().forEach(plo -> plo.getPlayerData().addRankedPoints(3));
		}

		GameAPI.getAPI().getGameServer().setGameState(GameState.FINISHED);
		Location winnerLocation = PluginUHC.getInstance().getDefaultLoc();
		Location looserLocation = winnerLocation.clone().add(0d, 7d, 0d);

		for(BadblockPlayer player : GameAPI.getAPI().getOnlinePlayers()){
			try {
				BadblockPlayer bp = (BadblockPlayer) player;
				bp.heal();
				bp.setLevel(20);
				bp.clearInventory();
				bp.setInvulnerable(true);

				bp.inGameData(UHCData.class).doReward(bp, winner, winnerPlayer, winnerLocation, looserLocation);
			} catch(Exception e){
				e.printStackTrace();
			}
		}

		// Work with rankeds
		String rankedGameName = RankedManager.instance.getCurrentRankedGameName();
		for (BadblockPlayer player : BukkitUtils.getPlayers())
		{
			RankedManager.instance.calcPoints(rankedGameName, player, new RankedCalc()
			{

				@Override
				public long done() {
					double kills = RankedManager.instance.getData(rankedGameName, player, UHCScoreboard.KILLS);
					double deaths = RankedManager.instance.getData(rankedGameName, player, UHCScoreboard.DEATHS);
					double wins = RankedManager.instance.getData(rankedGameName, player, UHCScoreboard.WINS);
					double looses = RankedManager.instance.getData(rankedGameName, player, UHCScoreboard.LOOSES);
					double total = 
							( (kills * 2) + (wins * 4) + 
									((kills / (deaths > 0 ? deaths : 1) ) ) )
							/ (1 + looses);
					return (long) total;
				}

			});
		}
		RankedManager.instance.fill(rankedGameName);

		try {
			new UHCResults(winner, winnerPlayer);
			new EndEffectRunnable(winnerLocation, winner).runTaskTimer(GameAPI.getAPI(), 0, 1L);
		} catch(Exception e){
			e.printStackTrace();
		}

		new KickRunnable().runTaskTimer(GameAPI.getAPI(), 0, 20L);
	}

	@Override
	public void run() {

		if (pastTime == 10)
		{
			for (BadblockPlayer bPlayer : BukkitUtils.getPlayers())
			{
				GameKit kit = (GameKit) new ArrayList<>(PluginUHC.getInstance().getKits().values()).get(new Random().nextInt(PluginUHC.getInstance().getKits().size()));
				KitLevel kitLevel = kit.getLevels()[0];
				GameAPI.getAPI().getKitContentManager().give(kitLevel.getStuff(), bPlayer);
			}
		}

		if (pastTime >= 120)
		{
			BorderUtils.setBorder(5, totalTime - ((pastTime - 120)) - 30);
		}

		if (pastTime >= 15)
		{
			pve = true;
		}

		if (totalTime - pastTime == 0)
		{
			if(PluginUHC.getInstance().getConfiguration().allowTeams)
				GameAPI.getAPI().getTeams().stream().forEach(team -> team.getOnlinePlayers().forEach(player -> player.playSound(Sound.AMBIENCE_CAVE)));
			else GameAPI.getAPI().getRealOnlinePlayers().stream().forEach(player -> player.playSound(Sound.AMBIENCE_CAVE));
		}else if (totalTime - pastTime < 0) {
			if(PluginUHC.getInstance().getConfiguration().allowTeams)
				GameAPI.getAPI().getTeams().stream().forEach(team -> team.getOnlinePlayers().forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 86400 * 20, 1))));
			else GameAPI.getAPI().getRealOnlinePlayers().stream().forEach(player -> player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 86400 * 20, 1)));
		}

		if(countEntities() <= 1){
			doEnd();
			cancel();
		}

		pastTime++;
		for (BadblockPlayer bp : BukkitUtils.getPlayers())
			if (bp.getCustomObjective() != null)
				bp.getCustomObjective().generate();
	}
}