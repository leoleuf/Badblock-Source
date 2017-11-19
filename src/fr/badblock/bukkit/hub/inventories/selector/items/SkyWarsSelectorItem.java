package fr.badblock.bukkit.hub.inventories.selector.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.BadBlockHub;
import fr.badblock.bukkit.hub.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.run.BadblockGame;
import fr.badblock.gameapi.sentry.SEntry;
import fr.badblock.rabbitconnector.RabbitPacketType;
import fr.badblock.utils.Encodage;

public class SkyWarsSelectorItem extends GameSelectorItem {

	public SkyWarsSelectorItem() {
		super("hub.items.skywarsselectoritem", Material.CHEST, "hub.items.skywarsselectoritem.lore");
		this.setFakeEnchantment(true);
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public List<String> getGames() {
		return Arrays.asList("sw8");
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock)
	{
		BadBlockHub hub = BadBlockHub.getInstance();
		hub.getRabbitService().sendAsyncPacket("networkdocker.sentry.join", hub.getGson().toJson(new SEntry(HubPlayer.getRealName(player), getGamePrefix(), false)), Encodage.UTF8, RabbitPacketType.PUBLISHER, 5000, false);
	}

	@Override
	public BadblockGame getGame() {
		return BadblockGame.SKYWARS;
	}

	@Override
	public boolean isMiniGame() {
		return true;
	}

	@Override
	public String getGamePrefix() {
		return "sw8";
	}

}
