package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.hat;

import java.util.List;

import org.bukkit.Material;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.gameapi.utils.ConfigUtils;

public class HatMaterialsInventory extends CustomInventory {

	@SuppressWarnings({ "deprecation", "unused" })
	public HatMaterialsInventory() {
		super("hub.items.hatmaterialsinventory", 6);
		BlueStainedGlassPaneItem blueStainedGlassPaneItem = new BlueStainedGlassPaneItem();
		this.setItem(blueStainedGlassPaneItem, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48,
				49, 50, 51, 52);
		List<String> chosenMaterials = ConfigUtils.getStringList(main(), "disguises.chosenhat");
		chosenMaterials.parallelStream().forEach(material -> {
			String[] splitter = material.split(":");
			Material finalMaterial = Material.getMaterial(Integer.parseInt(splitter[0]));
			byte data = (byte) 0;
			if (splitter.length > 1)
				data = (byte) Integer.parseInt(splitter[1]);
			//this.addItem(new MetamorphosisMaterialItem(finalMaterial.name().toLowerCase(), finalMaterial, data));
		});
		this.setItem(45, new RemoveHatItem());
		this.setAsLastItem(new PlayerQuitHatItem());
	}

}
