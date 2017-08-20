package fr.badblock.common.shoplinker.bukkit.signs;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.bukkit.Location;

import fr.badblock.common.shoplinker.bukkit.ShopLinker;
import fr.badblock.minecraftserver.JsonUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data public class SignManager {

	@Getter@Setter private static SignManager instance;

	private Collection<SignObject> signs = new HashSet<>();
	private final File			   file  = new File(ShopLinker.getInstance().getDataFolder(), "signs.json");

	public SignManager(List<SignObject> signs) {
		setSigns(signs);
		getSigns().forEach(signObject -> signObject.genLocation());
	}

	public SignObject getSign(Location location) {
		Optional<SignObject> signObject = getSigns().parallelStream().filter(sign -> sign.getLocation().equals(location)).findFirst();
		if (!signObject.isPresent()) return null;
		return signObject.get();
	}

	public void addSign(SignObject signObject) {
		getSigns().add(signObject);
	}

	public void setSign(SignObject signObject) {
		removeSign(signObject.getLocation());
		addSign(signObject);
	}

	public void removeSign(Location location) {
		SignObject signO = getSign(location);
		if (signO != null) signs.remove(signO);
	}

	public void save() {
		JsonUtils.save(file, this.getSigns(), true);
	}

	public static SignManager load(List<SignObject> data) {
		setInstance(new SignManager(data));
		return getInstance();
	}

}
