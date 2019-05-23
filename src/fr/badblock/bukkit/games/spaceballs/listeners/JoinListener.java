package fr.badblock.bukkit.games.spaceballs.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.badblock.bukkit.games.spaceballs.PluginSB;
import fr.badblock.bukkit.games.spaceballs.entities.SpaceTeamData;
import fr.badblock.bukkit.games.spaceballs.players.SpaceScoreboard;
import fr.badblock.bukkit.games.spaceballs.runnables.BossBarRunnable;
import fr.badblock.bukkit.games.spaceballs.runnables.GameRunnable;
import fr.badblock.bukkit.games.spaceballs.runnables.PreStartRunnable;
import fr.badblock.bukkit.games.spaceballs.runnables.StartRunnable;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.events.PlayerGameInitEvent;
import fr.badblock.gameapi.events.api.SpectatorJoinEvent;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;
import fr.badblock.gameapi.utils.selections.CuboidSelection;

public class JoinListener extends BadListener {
	@EventHandler
	public void onSpectatorJoin(SpectatorJoinEvent e){
		e.getPlayer().teleport(PluginSB.getInstance().getMapConfiguration().getSpawnLocation());

		new SpaceScoreboard(e.getPlayer());
	}

	@EventHandler(priority=EventPriority.LOWEST)
	public void onInteract(PlayerInteractEvent e){
		e.setCancelled(false);
	}

	@EventHandler
	public void onExplode(EntityExplodeEvent e){
		fix(e.blockList());
	}

	@EventHandler
	public void onExplode(BlockExplodeEvent e){
		fix(e.blockList());
	}

	private void fix(List<Block> blocks){
		List<CuboidSelection> toProtect = toProtect();

		for(int i=0;i<blocks.size();i++){

			Block b = blocks.get(i);

			for(CuboidSelection selec : toProtect){
				if(selec.isInSelection(b)){
					blocks.remove(i);
					i--;
					break;
				}
			}

		}
	}

	private List<CuboidSelection> toProtect(){
		List<CuboidSelection> toProtect = new ArrayList<>();

		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			toProtect.add( (team.teamData(SpaceTeamData.class).getSpawnSelection()) );
		}

		toProtect.add( (PluginSB.getInstance().getMapConfiguration().getTowerBounds()) );

		return toProtect;
	}


	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		e.setJoinMessage(null);

		if(inGame()){
			return;
		}

		BadblockPlayer player = (BadblockPlayer) e.getPlayer();

		if (!player.getBadblockMode().equals(BadblockMode.SPECTATOR) && !inGame()) {
			new BossBarRunnable(player.getUniqueId()).runTaskTimer(GameAPI.getAPI(), 0, 20L);

			player.setGameMode(GameMode.SURVIVAL);
			player.sendTranslatedTitle("spaceballs.join.title");
			player.teleport(PluginSB.getInstance().getConfiguration().spawn.getHandle());
			player.sendTimings(0, 80, 20);
			player.sendTranslatedTabHeader(new TranslatableString("spaceballs.tab.header"), new TranslatableString("spaceballs.tab.footer"));

			GameMessages.joinMessage(GameAPI.getGameName(), player.getTabGroupPrefix().getAsLine(player) + player.getName(), Bukkit.getOnlinePlayers().size(), PluginSB.getInstance().getMaxPlayers()).broadcast();
		}
		PreStartRunnable.doJob();
		StartRunnable.joinNotify(Bukkit.getOnlinePlayers().size(), PluginSB.getInstance().getMaxPlayers());
	}

	@EventHandler
	public void onGameInit(PlayerGameInitEvent event) {
		GameRunnable.handle(event.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		e.setQuitMessage(null);
	}
}
