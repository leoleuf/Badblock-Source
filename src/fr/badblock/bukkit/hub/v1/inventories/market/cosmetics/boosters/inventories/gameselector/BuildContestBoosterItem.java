package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.boosters.inventories.gameselector;

import fr.badblock.gameapi.run.BadblockGame;

public class BuildContestBoosterItem extends BoosterItem {

	public BuildContestBoosterItem() {
		super("buildcontest", BadblockGame.BUILDCONTEST.createItemStack().getType());
		this.setFakeEnchantment(true);
	}

}
