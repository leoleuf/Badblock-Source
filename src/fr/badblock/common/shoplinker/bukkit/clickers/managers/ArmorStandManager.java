package fr.badblock.common.shoplinker.bukkit.clickers.managers;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.bukkit.Location;

import fr.badblock.common.shoplinker.bukkit.ShopLinker;
import fr.badblock.common.shoplinker.bukkit.clickers.ClickableObject;
import fr.badblock.minecraftserver.JsonUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data public class ArmorStandManager {

	@Getter@Setter private static ArmorStandManager instance;

	private Collection<ClickableObject> armorStands = new HashSet<>();
	private final File			   		file  		= new File(ShopLinker.getInstance().getDataFolder(), "armorstands.json");

	public ArmorStandManager(List<ClickableObject> armorStands) {
		setArmorStands(armorStands);
		getArmorStands().forEach(clickableObject -> clickableObject.genLocation());
	}

	public ClickableObject getArmorStand(Location location) {
		Optional<ClickableObject> clickableObject = getArmorStands().parallelStream().filter(object -> object.getLocation().equals(location)).findFirst();
		if (!clickableObject.isPresent()) return null;
		return clickableObject.get();
	}

	public void addArmorStand(ClickableObject clickableObject) {
		getArmorStands().add(clickableObject);
	}

	public void setArmorStand(ClickableObject clickableObject) {
		removeArmorStand(clickableObject.getLocation());
		addArmorStand(clickableObject);
	}

	public void removeArmorStand(Location location) {
		ClickableObject armorStand = getArmorStand(location);
		if (armorStand != null) getArmorStands().remove(armorStand);
	}

	public void save() {
		JsonUtils.save(file, this.getArmorStands(), true);
	}

	public static ArmorStandManager load(List<ClickableObject> data) {
		setInstance(new ArmorStandManager(data));
		return getInstance();
	}

}
