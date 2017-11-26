package fr.badblock.bukkit.hub.v1.inventories.settings.settings.friends;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.utils.RabbitUtils;
import fr.badblock.gameapi.players.BadblockPlayer;

public class FriendsForNobodySettingsItem extends CustomItem {

	public FriendsForNobodySettingsItem() {
		super("hub.items.friendsfornobodysettingsitem", Material.STAINED_CLAY, (byte) 14,
				"hub.items.friendsfornobodysettingsitem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		RabbitUtils.forceCommand(player, "friends off");
		player.closeInventory();
	}

}