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

public class TowerStatisticsItem extends CustomItem {

	public TowerStatisticsItem() {
		// super("§bTower", Material.NETHER_FENCE);
		super("hub.items.towerselectoritem", Material.NETHER_FENCE);
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		StatisticsInventory.openAchievements(player, BadblockGame.TOWER);
	}

	@Override
	public ItemStack toItemStack(BadblockPlayer player) {
		TranslatableString prefix = new TranslatableString("hub.items.towerselectoritem");
		ItemStack itemStack = build(Material.NETHER_FENCE, 1, (byte) 0, prefix.getAsLine(player),
				player.getTranslatedMessage("hub.items.towerstatistics",
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("tower", "wins")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("tower", "kills")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("tower", "marks")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("tower", "looses")),
						CalcUtil.getInstance().convertInt(player.getPlayerData().getStatistics("tower", "deaths")),
						CalcUtil.getInstance().getRatio(player.getPlayerData().getStatistics("tower", "kills"),
								player.getPlayerData().getStatistics("tower", "deaths"))));
		return itemStack;
	}

}