
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.settings;

import org.bukkit.Material;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomPlayerInventory;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.defaults.MountItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.settings.defaults.MountConfig;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.settings.defaults.MountConfiguratorItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.settings.defaults.MountSettingsInventory;
import fr.badblock.gameapi.players.BadblockPlayer;

public class MountFunModeItem extends MountConfiguratorItem {

	@SuppressWarnings("deprecation")
	public MountFunModeItem() {
		super("funmode", Material.getMaterial(401), (byte) 0);
	}

	@Override
	public String getStateKey(MountConfig mountConfig) {
		return mountConfig.isFunMode() ? "hub.mounts.funmode.enabled.lore" : "hub.mounts.funmode.disabled.lore";
	}

	@Override
	public boolean isCompatible(MountItem mountItem) {
		return mountItem.hasFunMode();
	}

	@Override
	public void use(BadblockPlayer player, ItemAction itemAction, MountConfig mountConfig) {
		if (!player.hasPermission("hub.mounts.funmode")) {
			player.sendTranslatedMessage("hub.mounts.funmode.notpermission");
			return;
		}
		if (mountConfig.isFunMode()) {
			mountConfig.setFunMode(false);
			player.sendTranslatedMessage("hub.mounts.funmode.disabled");
			CustomPlayerInventory.get(MountSettingsInventory.class, player);
			return;
		}
		mountConfig.setFunMode(true);
		player.sendTranslatedMessage("hub.mounts.funmode.enabled");
		CustomPlayerInventory.get(MountSettingsInventory.class, player);
	}

}
