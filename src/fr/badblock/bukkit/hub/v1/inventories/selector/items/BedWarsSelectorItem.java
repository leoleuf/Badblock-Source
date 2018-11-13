package fr.badblock.bukkit.hub.v1.inventories.selector.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.selector.submenus.inventories.BedWarsChooserInventory;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.run.BadblockGame;

public class BedWarsSelectorItem extends GameSelectorItem {

	public BedWarsSelectorItem() {
		// super("§bTower", Material.NETHER_FENCE);
		super("hub.items.bedwarsselectoritem", Material.BED, "hub.items.bedwarsselectoritem.lore");
		this.setFakeEnchantment(true);
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public List<String> getGames() {
		return Arrays.asList("bw2x4", "bw1x8", "bw4x4");
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		CustomInventory.get(BedWarsChooserInventory.class).open(player);
		/*BadBlockHub instance = BadBlockHub.getInstance();
		RabbitService service = instance.getRabbitService();
		Gson gson = instance.getGson();
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				service.sendAsyncPacket("networkdocker.sentry.join", gson.toJson(new SEntry(HubPlayer.getRealName(player), "bw2x4", false)), Encodage.UTF8, RabbitPacketType.PUBLISHER, 5000, false);
			}
		};
		runnable.run();
		player.closeInventory();*/
	}

	@Override
	public BadblockGame getGame() {
		return BadblockGame.BEDWARS;
	}

	@Override
	public boolean isMiniGame() {
		return true;
	}

	@Override
	public String getGamePrefix() {
		return "bw";
	}

	@Override
	public String getBoosterPrefix() {
		return "bedwars";
	}

}
