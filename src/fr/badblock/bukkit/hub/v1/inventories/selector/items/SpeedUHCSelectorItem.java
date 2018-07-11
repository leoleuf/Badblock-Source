package fr.badblock.bukkit.hub.v1.inventories.selector.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import com.google.gson.Gson;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.run.BadblockGame;
import fr.badblock.rabbitconnector.RabbitPacketType;
import fr.badblock.rabbitconnector.RabbitService;
import fr.badblock.sentry.SEntry;
import fr.badblock.utils.Encodage;

public class SpeedUHCSelectorItem extends GameSelectorItem {

	public SpeedUHCSelectorItem() {
		// super("§bSpeedUHC", Material.GOLDEN_APPLE);
		super("hub.items.speeduhcselectoritem", Material.GOLDEN_APPLE, "hub.items.speeduhcselectoritem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public List<String> getGames() {
		return Arrays.asList("speeduhc", "speeduhcs", "speeduhct");
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		BadBlockHub instance = BadBlockHub.getInstance();
		RabbitService service = instance.getRabbitService();
		Gson gson = instance.getGson();
		service.sendAsyncPacket("networkdocker.sentry.join", gson.toJson(new SEntry(HubPlayer.getRealName(player), "speeduhct", false)),
				Encodage.UTF8, RabbitPacketType.PUBLISHER, 5000, false);
	}

	@Override
	public BadblockGame getGame() {
		return BadblockGame.UHCSPEED;
	}

	@Override
	public boolean isMiniGame() {
		return true;
	}
	
	@Override
	public String getGamePrefix() {
		return "speeduhc";
	}

}
