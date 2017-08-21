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

@Data public class SignManager {

	@Getter@Setter private static SignManager instance;

	private Collection<ClickableObject> signs = new HashSet<>();
	private final File			   file  = new File(ShopLinker.getInstance().getDataFolder(), "signs.json");

	public SignManager(List<ClickableObject> signs) {
		setSigns(signs);
		getSigns().forEach(signObject -> signObject.genLocation());
	}

	public ClickableObject getSign(Location location) {
		Optional<ClickableObject> signObject = getSigns().parallelStream().filter(sign -> sign.getLocation().equals(location)).findFirst();
		if (!signObject.isPresent()) return null;
		return signObject.get();
	}

	public void addSign(ClickableObject signObject) {
		getSigns().add(signObject);
	}

	public void setSign(ClickableObject signObject) {
		removeSign(signObject.getLocation());
		addSign(signObject);
	}

	public void removeSign(Location location) {
		ClickableObject signO = getSign(location);
		if (signO != null) signs.remove(signO);
	}

	public void save() {
		JsonUtils.save(file, this.getSigns(), true);
	}

	public static SignManager load(List<ClickableObject> data) {
		setInstance(new SignManager(data));
		return getInstance();
	}

}
