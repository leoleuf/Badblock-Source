package fr.badblock.bukkit.hub.inventories.settings;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.badblock.bukkit.hub.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.inventories.settings.items.CyanStainedGlassPaneItem;
import fr.badblock.bukkit.hub.inventories.settings.statistics.BuildContestStatisticsItem;
import fr.badblock.bukkit.hub.inventories.settings.statistics.CTSStatisticsItem;
import fr.badblock.bukkit.hub.inventories.settings.statistics.PearlsWarStatisticsItem;
import fr.badblock.bukkit.hub.inventories.settings.statistics.RushStatisticsItem;
import fr.badblock.bukkit.hub.inventories.settings.statistics.SpaceBallsStatisticsItem;
import fr.badblock.bukkit.hub.inventories.settings.statistics.SpeedUHCStatisticsItem;
import fr.badblock.bukkit.hub.inventories.settings.statistics.SurvivalGamesStatisticsItem;
import fr.badblock.bukkit.hub.inventories.settings.statistics.TowerStatisticsItem;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.data.PlayerAchievementState;
import fr.badblock.gameapi.run.BadblockGame;
import fr.badblock.gameapi.utils.itemstack.ItemAction;
import fr.badblock.gameapi.utils.itemstack.ItemEvent;

public class StatisticsInventory extends CustomInventory {

	public static fr.badblock.gameapi.utils.itemstack.CustomInventory createInventory(BadblockPlayer player,
			BadblockGame game, int canUseSize, int size) {
		fr.badblock.gameapi.utils.itemstack.CustomInventory inv = GameAPI.getAPI().createCustomInventory(size,
				player.getTranslatedMessage("achievements.inventory." + game)[0]);

		if (game != null && game.getGameData() != null && game.getGameData().getAchievements() != null) {
			int line = (size - canUseSize) / 2;
			int column = 0;
			for (PlayerAchievement achievement : game.getGameData().getAchievements().getAllAchievements()) {
				PlayerAchievementState state = player.getPlayerData().getAchievementState(achievement);

				boolean has = state.isSucceeds();
				int progress = (int) state.getProgress();

				if (has)
					progress = achievement.getNeededValue();

				Material type = achievement.isTemp() ? (has ? Material.EMERALD : Material.COAL)
						: (has ? Material.EMERALD_BLOCK : Material.COAL_BLOCK);

				inv.addClickableItem(line * 9 + column, GameAPI.getAPI().createItemStackFactory().type(type)
						.doWithI18n(player.getPlayerData().getLocale()).displayName(achievement.getDisplayName())
						.lore(achievement.getDescription(progress)).asExtra(1).disallow(ItemAction.values())); // aucune
																												// action,
																												// juste
																												// lecture
																												// :o

				column++;

				if (column == 4) {
					line++;
					column = 0;
				} else if (column == 9) {
					line++;
					column = 5;
				}

				if (line == canUseSize + ((size - canUseSize) / 2)) {

					if (column == 0) {
						line = (size - canUseSize) / 2;
						column = 5;
					} else
						break;

				}
			}
		}
		return inv;
	}

	public static void openAchievements(BadblockPlayer badblockPlayer, BadblockGame game) {
		fr.badblock.gameapi.utils.itemstack.CustomInventory customInventory = createInventory(badblockPlayer, game, 4,
				6);
		ItemStack itemStack = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 11);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(" ");
		itemStack.setItemMeta(itemMeta);
		for (int i = 0; i < 9; i++)
			customInventory.addDecorationItem(i, itemStack);
		for (int i = 45; i < 53; i++)
			customInventory.addDecorationItem(i, itemStack);
		itemStack = new ItemStack(Material.BARRIER);
		itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(badblockPlayer.getTranslatedMessage("hub.items.backstatistics")[0]);
		itemMeta.setLore(Arrays.asList(badblockPlayer.getTranslatedMessage("hub.items.backstatistics.lore")));
		itemStack.setItemMeta(itemMeta);
		customInventory.addClickableItem(53, itemStack, new ItemEvent() {

			@Override
			public boolean call(ItemAction action, BadblockPlayer player) {
				CustomInventory.get(StatisticsInventory.class).open(player);
				return true;
			}

		});
		customInventory.openInventory(badblockPlayer);
	}

	public StatisticsInventory() {
		// super("§cParamètres/Statistiques", 5);
		super("hub.items.statisticsinventory", 5);
		BlueStainedGlassPaneItem blueStainedGlassPaneItem = new BlueStainedGlassPaneItem();
		this.setItem(blueStainedGlassPaneItem, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 37, 38, 39, 40, 41, 42, 43);
		this.setItem(13, new BuildContestStatisticsItem());
		this.setItem(19, new TowerStatisticsItem());
		this.setItem(20, new RushStatisticsItem());
		this.setItem(21, new SpeedUHCStatisticsItem());
		this.setItem(22, new CTSStatisticsItem());
		this.setItem(23, new SurvivalGamesStatisticsItem());
		this.setItem(24, new SpaceBallsStatisticsItem());
		this.setItem(25, new PearlsWarStatisticsItem());
		this.setItem(44, new BackSettingsItem());
		this.setNoFilledItem(new CyanStainedGlassPaneItem());
	}

}
