package fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.skyblock;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.google.gson.Gson;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.SubGameSelectorItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.rabbitconnector.RabbitPacketType;
import fr.badblock.rabbitconnector.RabbitService;
import fr.badblock.sentry.SEntry;
import fr.badblock.utils.Encodage;

public class SkyBlockNewSelectorItem extends SubGameSelectorItem {

	public SkyBlockNewSelectorItem() {
		super("hub.items.skyblockchooserinventory.new_name", Material.NETHER_STAR, 1, "hub.items.skyblockchooserinventory.new_lore");
		this.setFakeEnchantment(true);
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK, ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public String getGame() {
		return "skyb2";
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		player.sendPlayer("skyb2");
		player.sendMessage("§b➤ §7Téléportation §7au §6NOUVEAU §bSkyBlock§7...");
		player.closeInventory();
	}

	@Override
	public boolean isMiniGame() {
		return true;
	}

}
