package fr.badblock.bukkit.hub.v1.inventories.settings.settings.directmessages;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.gameapi.players.BadblockPlayer;

public class PrivateMessagesDescriptionSettingsItem extends CustomItem {

	public PrivateMessagesDescriptionSettingsItem() {
		// super("§bMessages privés", Material.PAPER);
		super("hub.items.privatemessagessettingsitem", Material.DIAMOND_CHESTPLATE,
				"hub.items.privatemessagesdescriptionsettingsitem.lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {

	}

	@Override
	public ItemStack toItemStack(BadblockPlayer player) {
		ItemStack itemStack = build(Material.SKULL_ITEM, 1, (byte) 3,
				player.getTranslatedMessage("hub.items.privatemessagessettingsitem")[0],
				player.getTranslatedMessage("hub.items.privatemessagesdescriptionsettingsitem.lore"));
		SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
		meta.setOwner("MHF_Question");
		itemStack.setItemMeta(meta);
		return itemStack;
	}

}
