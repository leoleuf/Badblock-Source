package fr.badblock.bukkit.games.rush.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Sheep;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.games.rush.PluginRush;
import fr.badblock.bukkit.games.rush.players.RushScoreboard;
import fr.badblock.bukkit.games.rush.runnables.BossBarRunnable;
import fr.badblock.bukkit.games.rush.runnables.GameRunnable;
import fr.badblock.bukkit.games.rush.runnables.PreStartRunnable;
import fr.badblock.bukkit.games.rush.runnables.StartRunnable;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.events.PlayerGameInitEvent;
import fr.badblock.gameapi.events.api.SpectatorJoinEvent;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.entities.CustomCreature;
import fr.badblock.gameapi.utils.entities.CustomCreature.CreatureBehaviour;
import fr.badblock.gameapi.utils.entities.CustomCreature.CreatureFlag;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;

public class JoinListener extends BadListener {
	public static final List<Sheep> sheeps = new ArrayList<>();

	@EventHandler
	public void onSpectatorJoin(SpectatorJoinEvent e){
		e.getPlayer().teleport(PluginRush.getInstance().getMapConfiguration().getSpawnLocation());

		new RushScoreboard(e.getPlayer());
		e.getPlayer().changePlayerDimension(BukkitUtils.getEnvironment( PluginRush.getInstance().getMapConfiguration().getDimension() ));
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		BadblockPlayer player = (BadblockPlayer) e.getPlayer();

		new BossBarRunnable(player.getUniqueId()).runTaskTimer(GameAPI.getAPI(), 0, 20L);

		if (!player.getBadblockMode().equals(BadblockMode.SPECTATOR)) {
			player.setGameMode(GameMode.SURVIVAL);
			player.sendTranslatedTitle("rush.join.title");
			player.teleport(PluginRush.getInstance().getConfiguration().spawn.getHandle());
			player.sendTimings(0, 80, 20);
			player.sendTranslatedTabHeader(new TranslatableString("rush.tab.header"), new TranslatableString("rush.tab.footer"));

			GameMessages.joinMessage(GameAPI.getGameName(), player.getName(), Bukkit.getOnlinePlayers().size(), PluginRush.getInstance().getMaxPlayers()).broadcast();
		}
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e){

		if(inGame()){
			return;
		}

		PluginRush rush = PluginRush.getInstance();
		if (Bukkit.getOnlinePlayers().size() + 1 >= rush.getMaxPlayers()) {
			if (rush.getConfiguration().enabledAutoTeamManager) {
				int max = rush.getConfiguration().maxPlayersAutoTeam * rush.getAPI().getTeams().size();
				if (rush.getMaxPlayers() < max) {
					rush.getAPI().getTeams().forEach(team -> team.setMaxPlayers(team.getMaxPlayers() + 1));
					rush.setMaxPlayers(rush.getMaxPlayers() + rush.getAPI().getTeams().size());
					try {
						BukkitUtils.setMaxPlayers(rush.getMaxPlayers());
					} catch (Exception err) {
						err.printStackTrace();
					}
				}
			}
		}
		PreStartRunnable.doJob();
		StartRunnable.joinNotify(Bukkit.getOnlinePlayers().size(), PluginRush.getInstance().getMaxPlayers());
	}

	@EventHandler
	public void onGameInit(PlayerGameInitEvent event) {
		GameRunnable.handle(event.getPlayer());
	}

	@EventHandler(priority=EventPriority.HIGHEST)
	public void onJoinHighest(PlayerJoinEvent e){
		if(sheeps.isEmpty()){

			for(MapLocation sheepLocation : PluginRush.getInstance().getConfiguration().sheeps){
				CustomCreature custom = GameAPI.getAPI().spawnCustomEntity(sheepLocation.getHandle(), EntityType.SHEEP);
				Sheep 		   sheep  = (Sheep) custom.getBukkit();


				sheep.setAdult();
				sheep.setMaxHealth(0.20d);

				custom.addCreatureFlags(CreatureFlag.RIDEABLE);
				custom.setCreatureBehaviour(CreatureBehaviour.NORMAL);

				sheeps.add(sheep);
			}

		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e){
		e.setQuitMessage(null);
	}

	@EventHandler
	public void craftItem(PrepareItemCraftEvent e) {
		if (!PluginRush.getInstance().getMapConfiguration().getAllowBows()) {
			Material itemType = e.getRecipe().getResult().getType();
			if (itemType == Material.BOW || itemType == Material.ARROW) {
				e.getInventory().setResult(new ItemStack(Material.AIR));
			}
		}
	}

}
