package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.boosters.inventories.gameselector;

import fr.badblock.gameapi.run.BadblockGame;

public class BedWarsBoosterItem extends BoosterItem {

	public BedWarsBoosterItem() {
		super("bedwars", BadblockGame.BEDWARS.createItemStack().getType());
		this.setFakeEnchantment(true);
	}

}
