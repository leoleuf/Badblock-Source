package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.boosters.inventories.gameselector;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.CyanStainedGlassPaneItem;

public class BoosterGameSelectorInventory extends CustomInventory {

	public BoosterGameSelectorInventory() {
		super("hub.items.booster.gameselectorinventory", 4);
		BlueStainedGlassPaneItem blueStainedGlassPaneItem = new BlueStainedGlassPaneItem();
		this.setItem(blueStainedGlassPaneItem, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35);
		this.setItem(13, new BuildContestBoosterItem());
		this.setItem(19, new TowerBoosterItem());
		this.setItem(20, new RushBoosterItem());
		this.setItem(21, new SpeedUHCBoosterItem());
		this.setItem(22, new SurvivalGamesBoosterItem());
		this.setItem(23, new SpaceBallsBoosterItem());
		this.setItem(24, new PearlsWarBoosterItem());
		this.setItem(25, new CTSBoosterItem());
		this.setNoFilledItem(new CyanStainedGlassPaneItem());
	}

}
