package fr.badblock.bukkit.hub.v1.inventories.settings.statistics;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.StatisticsInventory;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.run.BadblockGame;
import fr.badblock.gameapi.utils.general.CalcUtil;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class SpaceBallsStatisticsItem extends CustomItem {

	@SuppressWarnings("deprecation")
	public SpaceBallsStatisticsItem() {
		// super("§bSpaceBalls", Material.getMaterial(153));
		super("hub.items.spaceballsselectoritem", Material.getMaterial(153));
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		StatisticsInventory.openAchievements(player, BadblockGame.SPACE_BALLS);
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack toItemStack(BadblockPlayer player) {
		TranslatableString prefix = new TranslatableString("hub.items.spaceballsselectoritem");
		ItemStack itemStack = build(Material.getMaterial(153), 1, (byte) 0, prefix.getAsLine(player),
				player.getTranslatedMessage("hub.items.spaceballsstatistics",
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("spaceballs", "wins")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("spaceballs", "kills")),
						CalcUtil.getInstance()
								.convertInt(player.getPlayerData().getStatistics("spaceballs", "emeralds")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("spaceballs", "looses")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("spaceballs", "deaths")),
						CalcUtil.getInstance().getRatio(player.getPlayerData().getStatistics("spaceballs", "kills"),
								player.getPlayerData().getStatistics("spaceballs", "deaths"))));
		return itemStack;
	}

}
