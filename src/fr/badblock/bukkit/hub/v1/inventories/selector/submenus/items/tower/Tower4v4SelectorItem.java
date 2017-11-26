package fr.badblock.bukkit.hub.v1.inventories.selector.submenus.items.tower;

import java.util.Arrays;
import java.util.List;

import org.bukkit.DyeColor;
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

public class Tower4v4SelectorItem extends SubGameSelectorItem {

	@SuppressWarnings("deprecation")
	public Tower4v4SelectorItem() {
		super("hub.items.towerselectoritem.4v4", Material.BANNER, DyeColor.LIME.getDyeData(), "hub.items.towerselectoritem.4v4.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK, ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public String getGame() {
		return "tower4v4";
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		BadBlockHub instance = BadBlockHub.getInstance();
		RabbitService service = instance.getRabbitService();
		Gson gson = instance.getGson();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				service.sendAsyncPacket("networkdocker.sentry.join", gson.toJson(new SEntry(HubPlayer.getRealName(player), getGame(), false)), Encodage.UTF8, RabbitPacketType.PUBLISHER, 5000, false);
			}
		};
		runnable.run();
		player.closeInventory();
	}

	@Override
	public boolean isMiniGame() {
		return true;
	}

}
