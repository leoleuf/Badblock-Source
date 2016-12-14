package fr.badblock.rush.listeners;

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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.events.PlayerGameInitEvent;
import fr.badblock.gameapi.events.api.SpectatorJoinEvent;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.utils.entities.CustomCreature;
import fr.badblock.gameapi.utils.entities.CustomCreature.CreatureBehaviour;
import fr.badblock.gameapi.utils.entities.CustomCreature.CreatureFlag;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;
import fr.badblock.rush.PluginRush;
import fr.badblock.rush.players.RushScoreboard;
import fr.badblock.rush.runnables.BossBarRunnable;
import fr.badblock.rush.runnables.GameRunnable;
import fr.badblock.rush.runnables.PreStartRunnable;
import fr.badblock.rush.runnables.StartRunnable;

public class JoinListener extends BadListener {
	public static final List<Sheep> sheeps = new ArrayList<>();

	@EventHandler
	public void onSpectatorJoin(SpectatorJoinEvent e){
		e.getPlayer().teleport(PluginRush.getInstance().getMapConfiguration().getSpawnLocation());

		new RushScoreboard(e.getPlayer());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		e.setJoinMessage(null);

		if(inGame()){
			return;
		}

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
