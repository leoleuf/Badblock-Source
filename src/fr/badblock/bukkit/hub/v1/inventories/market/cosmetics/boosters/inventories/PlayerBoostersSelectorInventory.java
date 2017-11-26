package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.boosters.inventories;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomPlayerInventory;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.boosters.inventories.gameselector.BoosterGameSelectorInventory;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.inventoryitems.BackCosmeticsItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.data.PlayerData;
import fr.badblock.gameapi.players.data.boosters.Booster;
import fr.badblock.gameapi.utils.general.TimeUnit;

public class PlayerBoostersSelectorInventory extends CustomPlayerInventory {

	public PlayerBoostersSelectorInventory() {
		super("hub.items.boosterinventory", 3);
	}

	@Override
	public void init(BadblockPlayer player) {
		this.getItems().clear();
		BlueStainedGlassPaneItem blueStainedGlassPaneItem = new BlueStainedGlassPaneItem();
		for (int id = 0; id < this.getLines() * 9; id++)
			if ((id == 0 || id < 9 || id % 9 == 0 || id == 17 || id == 26 || id == 35 || id == 44 || id == 53
					|| id > (9 * (this.getLines() - 1)) - 1))
				this.setItem(blueStainedGlassPaneItem, id);
		this.setAsLastItem(new BackCosmeticsItem());
		PlayerData playerData = player.getPlayerData();
		if (playerData.getBoosters() == null)
			return;
		playerData.getBoosters().forEach(playerBooster -> {
			Material material = playerBooster.isExpired() ? Material.COAL_BLOCK
					: playerBooster.isEnabled() ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK;
			String status = playerBooster.isExpired() ? "expired" : playerBooster.isEnabled() ? "enabled" : "disabled";
			Booster booster = playerBooster.getBooster();
			this.addItem(new CustomItem("hub.boosters." + booster.getId() + ".name", material,
					"hub.boosters." + booster.getId() + ".lore") {
				@Override
				public void onClick(BadblockPlayer player, ItemAction action, Block clickedBlock) {
					if (playerBooster.isExpired()) {
						if (!action.equals(ItemAction.INVENTORY_RIGHT_CLICK)) {
							player.sendTranslatedMessage("hub.boosters.expired.rightclicktodeleteit");
							return;
						}
						playerData.getBoosters().remove(playerBooster);
						player.sendTranslatedMessage("hub.boosters.expired.deletedboostersuccessfully");
						player.saveGameData();
						init(player);
						open();
						player.playSound(Sound.EXPLODE);
						return;
					}
					if (playerBooster.isEnabled()) {
						player.sendTranslatedMessage("hub.boosters.enabled.cannotdisable");
						player.playSound(Sound.LAVA_POP);
						return;
					}
					if (!action.equals(ItemAction.INVENTORY_RIGHT_CLICK)) {
						player.sendTranslatedMessage("hub.boosters.disabled.availableactionslist");
						return;
					}/*
					playerBooster.setExpire(System.currentTimeMillis() + booster.getLength());
					playerBooster.setEnabled(true);
					player.sendTranslatedMessage("hub.boosters.disabled.enabledsuccessfully");
					player.playSound(Sound.NOTE_PLING);
					player.saveGameData();*/
					//init(player);
					//open();
					HubPlayer.get(player).lastBooster = playerBooster;
					CustomInventory.get(BoosterGameSelectorInventory.class).open(player);
				}

				@Override
				public List<ItemAction> getActions() {
					return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
							ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
				}

				@Override
				public ItemStack toItemStack(BadblockPlayer player) {
					return build(this.getMaterial(), this.getAmount(), this.getData(),
							player.getTranslatedMessage("hub.boosters." + booster.getId() + ".name_selector",
									player.getTranslatedMessage("hub.boosters.name." + status)[0],
									player.getTranslatedMessage("hub.boosters." + booster.getId() + ".name")[0])[0],
							player.getTranslatedMessage(this.getLore() + ".bought",
									player.getTranslatedMessage("hub.boosters." + status + ".statusname")[0],
									(playerBooster.getExpire() == -1
											? TimeUnit.MINUTE.toFrench(booster.getLength() / 60_000L)
											: playerBooster.getExpire() < System.currentTimeMillis() ? "-"
													: TimeUnit.MINUTE.toFrench((playerBooster.getExpire() / 60_000L)
															- (System.currentTimeMillis() / 60_000L))),
									((booster.getCoinsMultiplier() - 1) * 100), ((booster.getXpMultiplier() - 1) * 100), (playerBooster.getGameName() != null ? playerBooster.getGameName() : player.getTranslatedMessage("hub.boosters.noserver")[0])));
				}

			});
		});
	}

}