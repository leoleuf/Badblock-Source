package fr.badblock.bukkit.hub.v1.inventories.selector;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.FreeBuildSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.GoogleAuthSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.HubChangerSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.PvPBoxSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.PvPFactionSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.QuitSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.RushSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.SkyBlockSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.SpaceBallsSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.SpawnSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.SpeedUHCSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.TowerRunSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.TowerSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.selector.items.VIPZoneSelectorItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.settings.LightBlueStainedGlassPaneItem;

public class SelectorInventory extends CustomInventory {

	public SelectorInventory() {
		super("hub.items.selectorinventory", 5);
		LightBlueStainedGlassPaneItem lightBlueStainedGlassPaneItem = new LightBlueStainedGlassPaneItem();
		this.setItem(lightBlueStainedGlassPaneItem, 31);
		this.setItem(0, new SpawnSelectorItem());
		this.setItem(4, new GoogleAuthSelectorItem());
		this.setItem(8, new VIPZoneSelectorItem());
		//this.setItem(20, new ShootFlagSelectorItem());
		this.setItem(20, new TowerRunSelectorItem());
		this.setItem(21, new TowerSelectorItem());
		this.setItem(22, new RushSelectorItem());
		this.setItem(23, new SpeedUHCSelectorItem());
		//this.setItem(31, new CTSSelectorItem());
		//this.setItem(32, new SurvivalGamesSelectorItem());
		this.setItem(24, new SpaceBallsSelectorItem());
		//this.setItem(34, new PearlsWarSelectorItem());
		//this.setItem(35, new BuildContestSelectorItem());
		//this.setItem(37, new SkyWarsSelectorItem());
		this.setItem(29, new PvPFactionSelectorItem());
		this.setItem(30, new PvPBoxSelectorItem());
		this.setItem(32, new SkyBlockSelectorItem());
		this.setItem(33, new FreeBuildSelectorItem());
		//this.setItem(42, new DayZSelectorItem());
		//this.setItem(43, new PointOutSelectorItem());
		this.setItem(36, new HubChangerSelectorItem());
		this.setItem(44, new QuitSelectorItem());
		this.setNoFilledItem(new BlueStainedGlassPaneItem());
	}

}
