
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.defaults;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class RemoveMountCosmeticsItem extends CustomItem {

	@SuppressWarnings("deprecation")
	public RemoveMountCosmeticsItem() {
		super("hub.items.removemountitem", Material.getMaterial(397), (byte) 3, "hub.items.removemountitem.lore");
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		HubPlayer hubPlayer = HubPlayer.get(player);
		if (hubPlayer.getMountEntity() == null || hubPlayer.getMountEntity().isDead()) {
			player.sendTranslatedMessage("hub.items.removemountitem.youarenot");
			return;
		}
		hubPlayer.getMountEntity().remove();
		hubPlayer.setMountEntity(null);
		player.sendTranslatedMessage("hub.items.removemountitem.success");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

}
