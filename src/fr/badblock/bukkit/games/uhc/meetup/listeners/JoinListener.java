package fr.badblock.bukkit.games.uhc.meetup.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;

import fr.badblock.bukkit.games.uhc.meetup.PluginUHC;
import fr.badblock.bukkit.games.uhc.meetup.players.UHCScoreboard;
import fr.badblock.bukkit.games.uhc.meetup.runnables.BossBarRunnable;
import fr.badblock.bukkit.games.uhc.meetup.runnables.PreStartRunnable;
import fr.badblock.bukkit.games.uhc.meetup.runnables.StartRunnable;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.events.api.PlayerLoadedEvent;
import fr.badblock.gameapi.events.api.SpectatorJoinEvent;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class JoinListener extends BadListener {

	@EventHandler
	public void onChunkLoad(ChunkLoadEvent event)
	{
		if (!PluginUHC.getInstance().getCuboidSelection().isInSelection(event.getChunk().getBlock(0, 0, 0).getLocation()))
		{
			event.getChunk().unload();
		}
	}
	
	@EventHandler
	public void onSpectatorJoin(SpectatorJoinEvent e){
		e.getPlayer().teleport(PluginUHC.getInstance().getDefaultLoc());

		new UHCScoreboard(e.getPlayer());
	}


	@EventHandler
	public void onJoin(PlayerJoinEvent e)
	{
		e.setJoinMessage(null);
	}

	@EventHandler
	public void onLoaded(PlayerLoadedEvent e){
		if(inGame()){
			return;
		}

		BadblockPlayer player = (BadblockPlayer) e.getPlayer();
		if (!player.getBadblockMode().equals(BadblockMode.SPECTATOR) && !inGame()) {

			Location spawn = PluginUHC.getInstance().getConfiguration().spawn.getHandle();
			new BossBarRunnable(player.getUniqueId()).runTaskTimer(GameAPI.getAPI(), 0, 20L);

			player.setGameMode(GameMode.SURVIVAL);
			player.teleport(spawn);
			player.sendTimings(0, 80, 20);
			player.sendTranslatedTabHeader(new TranslatableString("uhcspeed.tab.header"), new TranslatableString("uhcspeed.tab.footer"));

			String display = player.getTabGroupPrefix().getAsLine(player) + player.getName();
			BukkitUtils.getAllPlayers().forEach(plo ->
			{
				plo.sendTranslatedMessage("uhcspeed.joined", display, Bukkit.getOnlinePlayers().size(), PluginUHC.getInstance().getMaxPlayers());
				plo.playSound(Sound.CLICK);
			});
		}

		if (player.getCustomObjective() == null)
			new UHCScoreboard(player);

		PreStartRunnable.doJob();
		StartRunnable.joinNotify(Bukkit.getOnlinePlayers().size(), PluginUHC.getInstance().getMaxPlayers());

	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		e.setQuitMessage(null);
	}

}
