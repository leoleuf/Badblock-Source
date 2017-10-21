package fr.badblock.bukkit.hub.inventories.settings.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import fr.badblock.bukkit.hub.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.inventories.abstracts.items.CustomItem;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.i18n.TranslatableString;

public class SkullSettingsItem extends CustomItem {

	public SkullSettingsItem() {
		// super("[name]", Material.SKULL_ITEM, (byte) 3);
		super("hub.items.skullsettingsitem", Material.SKULL_ITEM, (byte) 3, "");
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
		TranslatableString prefix = new TranslatableString("permissions.tab." + player.getMainGroup());
		ItemStack itemStack = build(Material.SKULL_ITEM, 1, (byte) 3, "§b" + player.getName(),
				player.getTranslatedMessage("hub.skullsettingsitem.lore", prefix.getAsLine(player),
						player.getPlayerData().getBadcoins(), player.getShopPoints()));
		SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
		meta.setOwner(player.getName());
		itemStack.setItemMeta(meta);
		return itemStack;
	}

}
