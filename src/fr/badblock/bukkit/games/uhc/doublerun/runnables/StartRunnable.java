package fr.badblock.bukkit.games.uhc.doublerun.runnables;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.uhc.doublerun.PluginUHC;
import fr.badblock.bukkit.games.uhc.doublerun.players.UHCScoreboard;
import fr.badblock.bukkit.games.uhc.doublerun.runnables.game.ChunkLoaderRunnable;
import fr.badblock.bukkit.games.uhc.doublerun.runnables.game.GameRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.particles.ParticleEffectType;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StartRunnable extends BukkitRunnable {
	public    static final int 		          TIME_BEFORE_START = 120;

	public static int PLAYERS_ON_START = -1;

	public static 	   StartRunnable      task 		       = null;
	public    static 	   GameRunnable 	  gameTask		   = null;

	public static int time = TIME_BEFORE_START;

	@Override
	public void run() {
		if (GameAPI.getAPI().getGameServer().getGameState().equals(GameState.FINISHED)
				|| GameAPI.getAPI().getGameServer().getGameState().equals(GameState.STOPPING))
		{
			return;
		}

		GameAPI.setJoinable(time >= 15);
		if(time == 0)
		{
			TeleportRunnable.freezeLoc.clear();
			for (Block block : TeleportRunnable.blocksToRemove)
			{
				block.setType(Material.AIR);
			}

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;
				bPlayer.sendParticle(bPlayer.getLocation(), GameAPI.getAPI().createParticleEffect(ParticleEffectType.EXPLOSION_NORMAL));
				bPlayer.playSound(Sound.EXPLODE);
			}

			cancel();
		} else if (time == 40)
		{
			ChunkLoaderRunnable.put(TeleportRunnable.teleportChunks);
		} else if (time == 15)
		{
			GameAPI.getAPI().balanceTeams(false);
			new TeleportRunnable().runTaskTimer(GameAPI.getAPI(), 20 * 5L, 4L);
		} else if (time == 10)
		{
			PLAYERS_ON_START = BukkitUtils.getPlayers().size();

			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;
				bPlayer.playSound(Sound.ORB_PICKUP);
			}
		} else if(time % 10 == 0 || time <= 5){
			TeleportRunnable.ensureLocations();

			sendTime(time);
		}

		if(time == 3){
			GameAPI.getAPI().getBadblockScoreboard().endVote();

			for(Player player : Bukkit.getOnlinePlayers()){
				new UHCScoreboard((BadblockPlayer) player);
			}
		}
		
		if (time == 2)
		{
			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;
				bPlayer.sendParticle(bPlayer.getLocation(), GameAPI.getAPI().createParticleEffect(ParticleEffectType.EXPLOSION_NORMAL));
				bPlayer.removePotionEffect(PotionEffectType.BLINDNESS);
				bPlayer.playSound(Sound.EXPLODE);
			}
		}

		sendTimeHidden(time);

		time--;
	}

	protected void start(){
		sendTime(time);

		runTaskTimer(GameAPI.getAPI(), 0, 20L);
	}

	private void sendTime(int time){
		ChatColor color = getColor(time);

		for(Player player : Bukkit.getOnlinePlayers()){
			BadblockPlayer bPlayer = (BadblockPlayer) player;

			bPlayer.playSound(Sound.NOTE_PLING);

			bPlayer.sendTitle(color + "" + ChatColor.BOLD + "" + time, "");
			bPlayer.sendTimings(0, 20 * 5, 0);
		}
	}

	private void sendTimeHidden(int time){
		ChatColor color = getColor(time);
		TranslatableString actionbar = GameMessages.startInActionBar(time, color);

		for(Player player : Bukkit.getOnlinePlayers()){
			BadblockPlayer bPlayer = (BadblockPlayer) player;

			if(time > 0)
				bPlayer.sendTranslatedActionBar(actionbar.getKey(), actionbar.getObjects());
			bPlayer.setLevel(time);
			bPlayer.setExp(0.0f);
		}
	}

	public static ChatColor getColor(int time){
		if(time == 1)
			return ChatColor.DARK_RED;
		else if(time <= 5)
			return ChatColor.RED;
		else return ChatColor.AQUA;
	}

	public static void joinNotify(int currentPlayers, int maxPlayers){
		if ((!GameAPI.getAPI().isHostedGame() && currentPlayers + 1 < PluginUHC.getInstance().getConfiguration().minPlayers)
				|| (GameAPI.getAPI().isHostedGame() && currentPlayers + 1 < PluginUHC.getInstance().getMaxPlayers())) return;

		startGame(false);
		int a = time - (TIME_BEFORE_START / Bukkit.getMaxPlayers());
		if (time >= 120 && (a <= 120 || Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers())) time = 120;
		else if (time >= 120) time = a;
	}

	public static void startGame(boolean force){
		GameRunnable.forceEnd = false;

		if(task == null){
			task = new StartRunnable();
			task.start();
			time = 45;
		}
		else if(force)
		{
			time = 45;
		}
	}

	public static void stopGame(){
		GameRunnable.forceEnd = true;

		if(task != null){
			task.cancel();
			time = time > 120 ? time : 120;
			GameAPI.setJoinable(true);
		}

		task = null;
		gameTask = null;
	}

	public static boolean started(){
		return task != null;
	}
}
