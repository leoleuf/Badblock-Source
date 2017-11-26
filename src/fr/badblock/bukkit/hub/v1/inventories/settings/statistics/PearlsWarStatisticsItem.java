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

public class PearlsWarStatisticsItem extends CustomItem {

	public PearlsWarStatisticsItem() {
		// super("§bPearlsWar", Material.ENDER_PEARL);
		super("hub.items.pearlswarselectoritem", Material.ENDER_PEARL);
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		StatisticsInventory.openAchievements(player, BadblockGame.PEARLSWAR);
	}

	@Override
	public ItemStack toItemStack(BadblockPlayer player) {
		TranslatableString prefix = new TranslatableString("hub.items.pearlswarselectoritem");
		ItemStack itemStack = build(Material.ENDER_PEARL, 1, (byte) 0, prefix.getAsLine(player),
				player.getTranslatedMessage("hub.items.pearlswarstatistics",
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("pearlswar", "wins")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("pearlswar", "kills")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("pearlswar", "looses")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("pearlswar", "deaths")),
						CalcUtil.getInstance().getRatio(player.getPlayerData().getStatistics("pearlswar", "kills"),
								player.getPlayerData().getStatistics("pearlswar", "deaths"))));
		return itemStack;
	}

}
