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

public class RushStatisticsItem extends CustomItem {

	public RushStatisticsItem() {
		// super("§bRush", Material.BED);
		super("hub.items.rushselectoritem", Material.BED);
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		StatisticsInventory.openAchievements(player, BadblockGame.RUSH);
	}

	@Override
	public ItemStack toItemStack(BadblockPlayer player) {
		TranslatableString prefix = new TranslatableString("hub.items.rushselectoritem");
		ItemStack itemStack = build(Material.BED, 1, (byte) 0, prefix.getAsLine(player),
				player.getTranslatedMessage("hub.items.rushstatistics",
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("rush", "wins")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("rush", "kills")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("rush", "brokenbeds")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("rush", "looses")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("rush", "deaths")),
						CalcUtil.getInstance().getRatio(player.getPlayerData().getStatistics("rush", "kills"),
								player.getPlayerData().getStatistics("rush", "deaths"))));
		return itemStack;
	}

}
