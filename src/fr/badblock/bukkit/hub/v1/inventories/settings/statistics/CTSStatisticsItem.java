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

public class CTSStatisticsItem extends CustomItem {

	public CTSStatisticsItem() {
		super("hub.items.ctsselectoritem", Material.SHEARS);
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK, ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		StatisticsInventory.openAchievements(player, BadblockGame.CTS);
	}

	@Override
	public ItemStack toItemStack(BadblockPlayer player) {
		TranslatableString prefix = new TranslatableString("hub.items.ctsselectoritem");
		ItemStack itemStack = build(this.getMaterial(), 1, (byte) 0, prefix.getAsLine(player),
				player.getTranslatedMessage("hub.items.ctsstatistics",
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("cts", "wins")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("cts", "kills")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("cts", "capturedflags")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("cts", "looses")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("cts", "deaths")),
						CalcUtil.getInstance().getRatio(player.getPlayerData().getStatistics("cts", "kills"),
								player.getPlayerData().getStatistics("cts", "deaths"))));
		return itemStack;
	}

}
