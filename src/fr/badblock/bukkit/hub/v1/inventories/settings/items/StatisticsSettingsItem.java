package fr.badblock.bukkit.hub.v1.inventories.settings.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.StatisticsInventory;
import fr.badblock.gameapi.players.BadblockPlayer;

public class StatisticsSettingsItem extends CustomItem {

	public StatisticsSettingsItem() {
		// super("§cStatistiques", Material.BOOK_AND_QUILL, "", "§7Découvrez
		// simplement de votre", "§7nombre de §bkills§7, votre §bratio",
		// "§7jusqu'à des statistiques", "§bpersonnelles §7insolites");
		super("hub.items.statisticssettingsitem", Material.EMERALD, "hub.items.statisticsettingsitem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		CustomInventory.get(StatisticsInventory.class).open(player);
	}

}
