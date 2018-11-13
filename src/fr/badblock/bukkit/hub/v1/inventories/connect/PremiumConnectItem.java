package fr.badblock.bukkit.hub.v1.inventories.connect;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.objects.HubStoredPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class PremiumConnectItem extends CustomItem {

	public PremiumConnectItem() {
		super("hub.items.connectinventory.premium", Material.EMERALD_BLOCK, "hub.items.connectinventory.premium_lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK, ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		HubStoredPlayer hubStoredPlayer = HubStoredPlayer.get(player);
		hubStoredPlayer.connectInventory = true;
		player.saveGameData();
		player.setOnlineMode(true);
		player.saveData();
		player.saveGameData();
		player.closeInventory();
		player.saveOnlineMode();
		player.sendTranslatedMessage("hub.items.connectinventory.savedpremium");
	}

}
