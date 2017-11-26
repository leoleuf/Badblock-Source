package fr.badblock.bukkit.hub.v1.inventories.market.properties;

import org.apache.commons.lang.Validate;

import fr.badblock.bukkit.hub.v1.inventories.market.properties.runnables.BadCoinsCustomPropertyRunnable;
import fr.badblock.bukkit.hub.v1.inventories.market.properties.runnables.BoosterCustomPropertyRunnable;
import fr.badblock.bukkit.hub.v1.inventories.market.properties.runnables.ChestCustomPropertyRunnable;
import fr.badblock.bukkit.hub.v1.inventories.market.properties.runnables.XpCustomPropertyRunnable;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.utils.BadValidator;
import lombok.Getter;
import lombok.Setter;

public enum CustomProperty {

	BADCOINS(new BadCoinsCustomPropertyRunnable()),
	XP(new XpCustomPropertyRunnable()),
	CHEST(new ChestCustomPropertyRunnable()),
	BOOSTER(new BoosterCustomPropertyRunnable());
	
	@Getter @Setter private CustomPropertyRunnable customPropertyRunnable;
	
	CustomProperty(CustomPropertyRunnable customPropertyRunnable) {
		this.setCustomPropertyRunnable(customPropertyRunnable);
	}
	
	public String getCustomI18n(BadblockPlayer player, String ownItem) {
		return this.getCustomPropertyRunnable().getCustomI18n(player, ownItem);
	}

	public CustomProperty run(BadblockPlayer player, String ownItem) {
		Validate.notNull(player);
		Validate.notNull(ownItem);
		Validate.notNull(this.getCustomPropertyRunnable());
		this.getCustomPropertyRunnable().work(player, ownItem);
		return this;
	}
	
	public static CustomProperty getPropertyTypeByName(String propertyName) {
		if (!propertyName.contains("_")) return null;
		String[] splitter = propertyName.split("_");
		for (CustomProperty customProperty : values())
			if (splitter[0].toLowerCase().equals(customProperty.name().toLowerCase())) return customProperty;
		return null;
	}
	
	public static boolean isACustomProperty(String propertyName) {
		return BadValidator.isNotNull(getPropertyTypeByName(propertyName));
	}

	
}
