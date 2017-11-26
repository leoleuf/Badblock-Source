package fr.badblock.bukkit.hub.v1.inventories.selector.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.ConfigUtils;

public class VIPZoneSelectorItem extends CustomItem {

	public VIPZoneSelectorItem() {
		// super("§bZone VIP", Material.DIAMOND);
		super("hub.items.vipzoneselectoritem", Material.DIAMOND, "hub.items.vipzoneselectoritem.lore");
		this.setNeededPermission("hub.vipzone");
		// this.setErrorNeededPermission("§cVous devez être VIP pour pouvoir
		// entrer dans cette zone.");
		this.setErrorNeededPermission("hub.items.vipzoneselectoritem.neededpermission");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		Location location = ConfigUtils.getLocation(BadBlockHub.getInstance(), "vipzone");
		if (location == null) // player.sendMessage("§cCe jeu est
								// indisponible.");
			player.sendTranslatedMessage("hub.gameunavailable");
		else
			player.teleport(location);
	}

}
