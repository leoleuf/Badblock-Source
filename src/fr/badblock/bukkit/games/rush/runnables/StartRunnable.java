package fr.badblock.bukkit.games.rush.runnables;

import java.io.File;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.scheduler.BukkitRunnable;

import fr.badblock.bukkit.games.rush.PluginRush;
import fr.badblock.bukkit.games.rush.configuration.RushMapConfiguration;
import fr.badblock.bukkit.games.rush.entities.RushTeamData;
import fr.badblock.bukkit.games.rush.listeners.JoinListener;
import fr.badblock.bukkit.games.rush.players.RushScoreboard;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;
import fr.badblock.gameapi.utils.selections.CuboidSelection;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StartRunnable extends BukkitRunnable {
	public    static final int 		     TIME_BEFORE_START = 25;
	protected static 	   StartRunnable task 		       = null;
	public 	  static 	   GameRunnable  gameTask		   = null;

	public static int time = TIME_BEFORE_START;
	public static RushMapConfiguration config;

	@Override
	public void run() {
		GameAPI.setJoinable(time >= 10);
		
		if(time == 0){
			for(Player player : Bukkit.getOnlinePlayers()){
				BadblockPlayer bPlayer = (BadblockPlayer) player;
				bPlayer.playSound(Sound.ORB_PICKUP);
			}

			cancel();
		} else if(time % 10 == 0 || time == 15 || time <= 5){
			sendTime(time);
		}

		if (time == 8)
		{
			for(Sheep sheep : JoinListener.sheeps){
				sheep.eject();
				sheep.remove();
			}

			JoinListener.sheeps.clear();
			
			String winner = GameAPI.getAPI().getBadblockScoreboard().getWinner().getInternalName();
			File   file   = new File(PluginRush.MAP, winner + ".json");

			RushMapConfiguration cc = new RushMapConfiguration(GameAPI.getAPI().loadConfiguration(file));
			cc.save(file);
			config = cc;
			PluginRush.getInstance().setMapConfiguration(cc);

			GameAPI.getAPI().getBadblockScoreboard().endVote();

			for(Player player : Bukkit.getOnlinePlayers())
			{
				new RushScoreboard((BadblockPlayer) player);
			}

			for (BadblockTeam team : GameAPI.getAPI().getTeams())
			{
				Location location = team.teamData(RushTeamData.class).getRespawnLocation();
				HashSet<Chunk> chunks = new HashSet<>();
				chunks.add(location.getChunk());

				int mapSize = 100;
				for (int x = -(mapSize / 2); x < (mapSize / 2); x += 2)
					for (int z = -(mapSize / 2); z < (mapSize / 2); z += 2)
					{
						Location l = location.clone().add(x, 0, z);
						Chunk c = l.getChunk();
						if (!chunks.contains(c))
						{
							chunks.add(c);
						}
					}
				
				ChunkLoaderRunnable.put(chunks);
			}
			
			for(BadblockTeam team : GameAPI.getAPI().getTeams()){

				Location location = team.teamData(RushTeamData.class).getRespawnLocation();
				location.getChunk().load();

				Location firstLoc = location.clone();
				firstLoc.setX(firstLoc.getX() - 64);
				firstLoc.setZ(firstLoc.getZ() + 64);
				

				Location secLoc = location.clone();
				secLoc.setX(secLoc.getX() + 64);
				secLoc.setZ(secLoc.getZ() - 64);
				CuboidSelection cuboid = new CuboidSelection(firstLoc, secLoc);
				
				ChunkLoaderRunnable.toReload.clear();
				HashSet<Chunk> chunks = new HashSet<>();
				
				for (Block b : cuboid.getBlocks())
				{
					Location l = b.getLocation();
					if (chunks.contains(l.getChunk()))
					{
						continue;
					}
					
					chunks.add(location.getChunk());
				}
				
				ChunkLoaderRunnable.put(chunks);
			}
		}
		
		if (time == 5)
		{
			GameAPI.getAPI().balanceTeams(false);
			
			gameTask = new GameRunnable(config);
			gameTask.runTaskTimer(GameAPI.getAPI(), 5 * 20L, 20L);
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

			bPlayer.sendTranslatedMessage("rush.startingtimeleft", color + ""  + time, time > 1 ? "s" : "");
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

	private ChatColor getColor(int time){
		if(time == 1)
			return ChatColor.DARK_RED;
		else if(time <= 5)
			return ChatColor.RED;
		else return ChatColor.AQUA;
	}

	public static void joinNotify(int currentPlayers, int maxPlayers){
		if ((!GameAPI.getAPI().isHostedGame() && currentPlayers + 1 < PluginRush.getInstance().getMinPlayers())
				|| (GameAPI.getAPI().isHostedGame() && currentPlayers + 1 < PluginRush.getInstance().getMaxPlayers())) return;
		
		startGame(false);
		if (time >= 15 && Bukkit.getOnlinePlayers().size() >= Bukkit.getMaxPlayers()) time = 15;
	}

	public static void startGame(boolean force){
		if(task == null){
			task = new StartRunnable();
			task.start();
		}
	}

	public static void stopGame(){
		if(gameTask != null){
			gameTask.forceEnd = true;
			time = TIME_BEFORE_START;
		} else if(task != null){
			task.cancel();
			time = time > 5 ? time : 5;
			GameAPI.setJoinable(true);
		}

		task = null;
		gameTask = null;
	}

	public static boolean started(){
		return task != null;
	}
}
