package fr.badblock.bukkit.hub.v1.inventories.settings.settings.directmessages;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.utils.RabbitUtils;
import fr.badblock.gameapi.players.BadblockPlayer;

public class PrivateMessagesForNobodySettingsItem extends CustomItem {

	public PrivateMessagesForNobodySettingsItem() {
		super("hub.items.privatemessagesfornobodysettingsitem", Material.STAINED_CLAY, (byte) 14,
				"hub.items.privatemessagesfornobodysettingsitem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		RabbitUtils.forceCommand(player, "msg set NOTHING");
		player.closeInventory();
	}

}