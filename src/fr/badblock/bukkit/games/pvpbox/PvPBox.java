package fr.badblock.bukkit.games.pvpbox;

import fr.badblock.bukkit.games.pvpbox.commands.GiveKitCommand;
import fr.badblock.bukkit.games.pvpbox.commands.PvPBoxCommand;
import fr.badblock.bukkit.games.pvpbox.commands.SpawnCommand;
import fr.badblock.bukkit.games.pvpbox.config.BoxConfig;
import fr.badblock.bukkit.games.pvpbox.listeners.EntityDamageListener;
import fr.badblock.bukkit.games.pvpbox.listeners.InventoryCloseListener;
import fr.badblock.bukkit.games.pvpbox.listeners.PlayerConsumeListener;
import fr.badblock.bukkit.games.pvpbox.listeners.PlayerDeathListener;
import fr.badblock.bukkit.games.pvpbox.listeners.PlayerInteractListener;
import fr.badblock.bukkit.games.pvpbox.listeners.PlayerInventoryClickListener;
import fr.badblock.bukkit.games.pvpbox.listeners.PlayerJoinListener;
import fr.badblock.bukkit.games.pvpbox.listeners.PlayerLoginListener;
import fr.badblock.bukkit.games.pvpbox.listeners.PlayerMoveListener;
import fr.badblock.bukkit.games.pvpbox.listeners.PlayerQuitListener;
import fr.badblock.game.core18R3.listeners.DoubleJumpListener;
import fr.badblock.gameapi.BadblockPlugin;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.achievements.AchievementList;
import fr.badblock.gameapi.run.BadblockGame;
import fr.badblock.gameapi.run.BadblockGameData;
import fr.badblock.gameapi.run.RunType;
import fr.badblock.gameapi.utils.GameRules;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PvPBox extends BadblockPlugin
{

	@Getter
	private static PvPBox		instance;

	private BoxConfig			boxConfig;

	@Override
	public void onEnable(RunType runType)
	{
		try
		{
			instance = this;

			BadblockGame.PVPBOX.setGameData(new BadblockGameData()
			{
				@Override
				public AchievementList getAchievements()
				{
					return PvPBoxAchievementList.instance;
				}
			});

			if (runType.equals(RunType.LOBBY))
			{
				return;
			}

			if (!getDataFolder().exists())
			{
				getDataFolder().mkdirs();
			}
			
			// Game rules
			GameRules.doDaylightCycle.setGameRule(false);
			GameRules.spectatorsGenerateChunks.setGameRule(false);
			GameRules.doFireTick.setGameRule(false);

			getAPI().getBadblockScoreboard().doBelowNameHealth();
			getAPI().getBadblockScoreboard().doTabListHealth();
			getAPI().formatChat(true, false, "pvpbox");
			getAPI().getBadblockScoreboard().doGroupsPrefix();
			getAPI().getBadblockScoreboard().doOnDamageHologram();
			
			getAPI().setAntiAfk(true);
			
			getAPI().formatChat(true, true);
			
			DoubleJumpListener.disabled = true;
			
			// Config load
			BoxConfig.reload(this);

			// Load listeners
			new PlayerConsumeListener();
			new EntityDamageListener();
			new PlayerDeathListener();
			new PlayerJoinListener();
			new InventoryCloseListener();
			new PlayerLoginListener();
			new PlayerMoveListener();
			new PlayerQuitListener();
			new PlayerInteractListener();
			new PlayerInventoryClickListener();

			new PvPBoxCommand();
			new GiveKitCommand();
			new SpawnCommand();

			GameAPI.logColor("§c[PvPBox] Loaded!");
		}
		catch (Exception error)
		{
			GameAPI.logError("Check the error & fix this.");
			error.printStackTrace();
		}
	}

	@Override
	public void onDisable()
	{

	}

}