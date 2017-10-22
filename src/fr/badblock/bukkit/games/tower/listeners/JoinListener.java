package fr.badblock.bukkit.games.tower.listeners;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.badblock.bukkit.games.tower.PluginTower;
import fr.badblock.bukkit.games.tower.TowerAchievementList;
import fr.badblock.bukkit.games.tower.entities.TowerTeamData;
import fr.badblock.bukkit.games.tower.players.TowerScoreboard;
import fr.badblock.bukkit.games.tower.runnables.BossBarRunnable;
import fr.badblock.bukkit.games.tower.runnables.PreStartRunnable;
import fr.badblock.bukkit.games.tower.runnables.StartRunnable;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.events.PlayerGameInitEvent;
import fr.badblock.gameapi.events.api.SpectatorJoinEvent;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.players.data.InGameKitData;
import fr.badblock.gameapi.players.data.PlayerAchievementState;
import fr.badblock.gameapi.players.kits.PlayerKit;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;

public class JoinListener extends BadListener {
	
	public long time = -1;
	
	@EventHandler
	public void onSpectatorJoin(SpectatorJoinEvent e){
		e.getPlayer().teleport(PluginTower.getInstance().getMapConfiguration().getSpawnLocation());

		new TowerScoreboard(e.getPlayer());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.setJoinMessage(null);
		BadblockPlayer player = (BadblockPlayer) e.getPlayer();
		new BossBarRunnable(player.getUniqueId()).runTaskTimer(GameAPI.getAPI(), 0, 20L);

		if (!player.getBadblockMode().equals(BadblockMode.SPECTATOR)) {
			player.setGameMode(GameMode.SURVIVAL);
			player.sendTranslatedTitle("tower.join.title");
			player.teleport(PluginTower.getInstance().getConfiguration().spawn.getHandle());
			player.sendTimings(0, 80, 20);
			player.sendTranslatedTabHeader(new TranslatableString("tower.tab.header"), new TranslatableString("tower.tab.footer"));

			GameMessages.joinMessage(GameAPI.getGameName(), player.getName(), Bukkit.getOnlinePlayers().size(), PluginTower.getInstance().getMaxPlayers()).broadcast();
		}
	}

	@EventHandler
	public void onLogin(PlayerLoginEvent e){

		if(inGame()){
			return;
		}

		PreStartRunnable.doJob();
		StartRunnable.joinNotify(Bukkit.getOnlinePlayers().size(), PluginTower.getInstance().getMaxPlayers());
		PluginTower tower = PluginTower.getInstance();
		if (Bukkit.getOnlinePlayers().size() + 1 >= tower.getMaxPlayers()) {
			if (tower.getConfiguration().enabledAutoTeamManager) {
				int max = tower.getConfiguration().maxPlayersAutoTeam * tower.getAPI().getTeams().size();
				if (tower.getMaxPlayers() < max) {
					tower.getAPI().getTeams().forEach(team -> team.setMaxPlayers(team.getMaxPlayers() + 1));
					tower.setMaxPlayers(tower.getMaxPlayers() + tower.getAPI().getTeams().size());
					try {
						BukkitUtils.setMaxPlayers(tower.getMaxPlayers());
					} catch (Exception err) {
						err.printStackTrace();
					}
				}
			}
		}
		System.out.println("OnLogin: Tower");
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
		if (!PluginTower.getInstance().getMapConfiguration().getAllowBows()) {
			Material itemType = e.getRecipe().getResult().getType();
			if (itemType == Material.BOW || itemType == Material.ARROW) {
				e.getInventory().setResult(new ItemStack(Material.AIR));
			}
		}
	}

	public static void handle(BadblockPlayer player) {
		BadblockTeam team = player.getTeam();
		if (team == null) return;
		Location location = team.teamData(TowerTeamData.class).getRespawnLocation();
		player.leaveVehicle();
		player.eject();
		player.changePlayerDimension(BukkitUtils.getEnvironment( PluginTower.getInstance().getMapConfiguration().getDimension() ));
		player.teleport(location);
		player.setGameMode(GameMode.SURVIVAL);
		player.getCustomObjective().generate();
		player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0));

		boolean good = true;

		for(PlayerKit toUnlock : PluginTower.getInstance().getKits().values()){
			if(!toUnlock.isVIP()){
				if(player.getPlayerData().getUnlockedKitLevel(toUnlock) < 2){
					good = false; break;
				}
			}
		}

		if(good){
			PlayerAchievementState state = player.getPlayerData().getAchievementState(TowerAchievementList.TOWER_ALLKITS);

			if(!state.isSucceeds()){
				state.succeed();
				TowerAchievementList.TOWER_ALLKITS.reward(player);
			}
		}

		PlayerKit kit = player.inGameData(InGameKitData.class).getChoosedKit();

		if(kit != null){
			if (PluginTower.getInstance().getMapConfiguration().getAllowBows())
				kit.giveKit(player);
			else
				kit.giveKit(player, Material.BOW, Material.ARROW);
		} else {
			PluginTower.getInstance().giveDefaultKit(player);
		}
	}

}
