package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import fr.badblock.bukkit.games.shootflag.PluginShootFlag;
import fr.badblock.bukkit.games.shootflag.entities.ShootFlagTeamData;
import fr.badblock.bukkit.games.shootflag.players.ShootFlagScoreboard;
import fr.badblock.bukkit.games.shootflag.runnables.BossBarRunnable;
import fr.badblock.bukkit.games.shootflag.runnables.PreStartRunnable;
import fr.badblock.bukkit.games.shootflag.runnables.StartRunnable;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.events.PlayerGameInitEvent;
import fr.badblock.gameapi.events.api.SpectatorJoinEvent;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;

public class JoinListener extends BadListener {
	@EventHandler
	public void onSpectatorJoin(SpectatorJoinEvent e){
		e.getPlayer().teleport(PluginShootFlag.getInstance().getMapConfiguration().getSpawnLocation());

		new ShootFlagScoreboard(e.getPlayer());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		e.setJoinMessage(null);

		if(inGame()){
			return;
		}

		BadblockPlayer player = (BadblockPlayer) e.getPlayer();
		player.playSound(Sound.LEVEL_UP);

		new BossBarRunnable(player.getUniqueId()).runTaskTimer(GameAPI.getAPI(), 0, 20L);

		if (!player.getBadblockMode().equals(BadblockMode.SPECTATOR)) {
			player.setGameMode(GameMode.SURVIVAL);
			player.sendTranslatedTitle("shootflag.join.title");
			player.teleport(PluginShootFlag.getInstance().getConfiguration().spawn.getHandle());
			player.sendTimings(0, 80, 20);
			player.sendTranslatedTabHeader(new TranslatableString("shootflag.tab.header"), new TranslatableString("shootflag.tab.footer"));

			GameMessages.joinMessage(GameAPI.getGameName(), player.getName(), Bukkit.getOnlinePlayers().size(), PluginShootFlag.getInstance().getMaxPlayers()).broadcast();
		}
		PreStartRunnable.doJob();
		StartRunnable.joinNotify(Bukkit.getOnlinePlayers().size(), PluginShootFlag.getInstance().getMaxPlayers());
	}

	@EventHandler
	public void onPlayerGameInit(PlayerGameInitEvent event) {
		handle(event.getPlayer());
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		e.setQuitMessage(null);
	}

	@EventHandler
	public void craftItem(PrepareItemCraftEvent e) {
		// interdire le craft
		e.getInventory().setResult(null);
	}

	public static void handle(BadblockPlayer player)
	{
		BadblockTeam team = player.getTeam();
		if (team == null) return;
		Location location = team.teamData(ShootFlagTeamData.class).getSpawnLocation();
		player.changePlayerDimension(BukkitUtils.getEnvironment( PluginShootFlag.getInstance().getMapConfiguration().getDimension() ));
		player.teleport(location);
		player.setGameMode(GameMode.ADVENTURE);
		player.getCustomObjective().generate();
		player.setWalkSpeed(0.45F);
		player.playSound(Sound.LEVEL_UP);
		PluginShootFlag.getInstance().giveDefaultKit(player);
	}

}
