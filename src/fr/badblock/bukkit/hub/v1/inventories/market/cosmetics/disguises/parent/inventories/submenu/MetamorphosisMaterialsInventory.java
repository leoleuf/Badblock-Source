package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.parent.inventories.submenu;

import java.util.List;

import org.bukkit.Material;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.parent.executors.BackMetamorphosisChoiceCosmeticsItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.parent.executors.RemoveMetamorphosisChoiceCosmeticsItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.submenu.data.abstracts.MetamorphosisMaterialItem;
import fr.badblock.bukkit.hub.v1.inventories.settings.items.BlueStainedGlassPaneItem;
import fr.badblock.gameapi.utils.ConfigUtils;

public class MetamorphosisMaterialsInventory extends CustomInventory {

	@SuppressWarnings("deprecation")
	public MetamorphosisMaterialsInventory() {
		super("hub.items.disguisesmaterialsinventory", 6);
		BlueStainedGlassPaneItem blueStainedGlassPaneItem = new BlueStainedGlassPaneItem();
		this.setItem(blueStainedGlassPaneItem, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 46, 47, 48,
				49, 50, 51, 52);
		this.setItem(45, new RemoveMetamorphosisChoiceCosmeticsItem());
		this.setAsLastItem(new BackMetamorphosisChoiceCosmeticsItem());
		List<String> chosenMaterials = ConfigUtils.getStringList(main(), "disguises.chosenmaterials");
		chosenMaterials.parallelStream().forEach(material -> {
			String[] splitter = material.split(":");
			Material finalMaterial = Material.getMaterial(Integer.parseInt(splitter[0]));
			byte data = (byte) 0;
			if (splitter.length > 1)
				data = (byte) Integer.parseInt(splitter[1]);
			this.addItem(new MetamorphosisMaterialItem(finalMaterial.name().toLowerCase(), finalMaterial, data));
		});
	}

}
