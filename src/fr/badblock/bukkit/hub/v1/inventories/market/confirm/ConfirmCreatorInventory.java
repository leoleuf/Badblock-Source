package fr.badblock.bukkit.hub.v1.inventories.market.confirm;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomPlayerInventory;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.market.ownitems.OwnableItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public class ConfirmCreatorInventory extends CustomPlayerInventory {

	public ConfirmCreatorInventory() {
		super("hub.items.confirmcreatorinventory", 1);
	}

	@Override
	public void init(BadblockPlayer player) {
		int item = new SecureRandom().nextInt(this.getLines() * 9);
		for (int i = 0; i < this.getLines() * 9; i++) {
			if (i == item) {
				this.setItem(i, new CustomItem("hub.items.confirm.yes.displayname", Material.EMERALD_BLOCK, "hub.items.confirm.yes.lore") {

					@Override
					public List<ItemAction> getActions() {
						return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK, ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
					}

					@Override
					public void onClick(BadblockPlayer player, ItemAction action, Block clickedBlock) {
						player.closeInventory();
						HubPlayer hubPlayer = HubPlayer.get(player);
						OwnableItem ownableItem = hubPlayer.getBuyItem();
						int badcoins = player.getPlayerData().getBadcoins();
						if (badcoins < ownableItem.getNeededBadcoins()) {
							player.sendTranslatedMessage("hub.notenoughbadcoins", ownableItem.getNeededBadcoins() - badcoins);
							return;
						}
						hubPlayer.updateScoreboard();
						player.getPlayerData().removeBadcoins(ownableItem.getNeededBadcoins());
						hubPlayer.updateScoreboard();
						ownableItem.addAsOwn(player);
						player.saveGameData();
						player.sendTranslatedMessage("hub.mounts.bought", player.getTranslatedMessage(this.getName())[0]);
						player.playSound(Sound.LEVEL_UP);
					}

					@Override
					public ItemStack toItemStack(BadblockPlayer player) {
						HubPlayer hubPlayer = HubPlayer.get(player);
						OwnableItem ownableItem = hubPlayer.getBuyItem();
						return build(this.getMaterial(), this.getAmount(), this.getData(), player.getTranslatedMessage(this.getName(), player.getTranslatedMessage(ownableItem.getName())[0], ownableItem.getNeededBadcoins())[0], player.getTranslatedMessage(this.getLore(), player.getTranslatedMessage(ownableItem.getName())[0], ownableItem.getNeededBadcoins()));
					}

				});
			}else{
				this.setItem(i, new CustomItem("hub.items.confirm.no.displayname", Material.REDSTONE_BLOCK, "hub.items.confirm.no.lore") {

					@Override
					public List<ItemAction> getActions() {
						return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK, ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
					}

					@Override
					public void onClick(BadblockPlayer player, ItemAction action, Block clickedBlock) {
						player.closeInventory();
						player.playSound(Sound.CHEST_CLOSE);
						player.sendTranslatedMessage("hub.shop.youabortedthepurchase");
					}
					
					@Override
					public ItemStack toItemStack(BadblockPlayer player) {
						HubPlayer hubPlayer = HubPlayer.get(player);
						OwnableItem ownableItem = hubPlayer.getBuyItem();
						return build(this.getMaterial(), this.getAmount(), this.getData(), player.getTranslatedMessage(this.getName(), player.getTranslatedMessage(ownableItem.getName())[0], ownableItem.getNeededBadcoins())[0], player.getTranslatedMessage(this.getLore(), player.getTranslatedMessage(ownableItem.getName())[0], ownableItem.getNeededBadcoins()));
					}

				});
			}
		}
	}

}
