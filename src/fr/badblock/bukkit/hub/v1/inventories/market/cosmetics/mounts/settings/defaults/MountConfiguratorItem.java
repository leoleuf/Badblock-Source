
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.settings.defaults;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.defaults.MountItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.bukkit.hub.v1.objects.HubStoredPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;

public abstract class MountConfiguratorItem extends CustomItem {

	public MountConfiguratorItem(String config, Material material, byte data) {
		super("hub.items.mounts.configurator." + config, material, data,
				"hub.items.mounts.configurator." + config + ".lore");
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	public MountConfig getConfig(BadblockPlayer player) {
		HubPlayer hubPlayer = HubPlayer.get(player);
		MountItem mountItem = hubPlayer.getClickedMountItem();
		if (mountItem == null)
			return null;
		HubStoredPlayer hubStoredPlayer = HubStoredPlayer.get(player);
		MountConfig mountConfig = hubStoredPlayer.getMountConfigs().get(mountItem.getName());
		if (mountConfig == null) {
			mountConfig = new MountConfig(mountItem.getName());
			hubStoredPlayer.getMountConfigs().put(mountItem.getName(), mountConfig);
		}
		return mountConfig;
	}

	public abstract String getStateKey(MountConfig config);

	public boolean isCompatible(BadblockPlayer player) {
		HubPlayer hubPlayer = HubPlayer.get(player);
		MountItem mountItem = hubPlayer.getClickedMountItem();
		if (mountItem == null)
			return false;
		return isCompatible(mountItem);
	}

	public abstract boolean isCompatible(MountItem mountItem);

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		if (!this.isCompatible(player)) {
			player.sendTranslatedMessage("hub.mounts.thismodeisnotcompatiblewiththatmount");
			return;
		}
		MountConfig mountConfig = this.getConfig(player);
		use(player, itemAction, mountConfig);
	}

	@Override
	public ItemStack toItemStack(BadblockPlayer player) {
		MountConfig mountConfig = getConfig(player);
		String stateKey = !this.isCompatible(player) ? "hub.mounts.thismodeisnotcompatiblewiththatmount.lore"
				: getStateKey(mountConfig);
		return build(this.getMaterial(), this.getAmount(), this.getData(),
				player.getTranslatedMessage(this.getName())[0],
				player.getTranslatedMessage(this.getLore(), player.getTranslatedMessage(stateKey)[0]));
	}

	public abstract void use(BadblockPlayer player, ItemAction itemAction, MountConfig mountConfig);

}
