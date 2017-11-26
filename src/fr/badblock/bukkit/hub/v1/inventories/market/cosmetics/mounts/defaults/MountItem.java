
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.defaults;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomPlayerInventory;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.settings.defaults.MountConfig;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.settings.defaults.MountSettingsInventory;
import fr.badblock.bukkit.hub.v1.inventories.market.ownitems.OwnableItem;
import fr.badblock.bukkit.hub.v1.listeners.vipzone.RaceListener;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.bukkit.hub.v1.objects.HubStoredPlayer;
import fr.badblock.bukkit.hub.v1.utils.MountManager;
import fr.badblock.gameapi.packets.watchers.WatcherSheep;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.ConfigUtils;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class MountItem extends OwnableItem {

	private String mountName;

	public MountItem(String mountName, Material material, byte data) {
		super("mounts", mountName, material, data, true);
		this.setMountName(mountName);
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public boolean hasPegasusMode() {
		return ConfigUtils.getBoolean(main(), this.getConfigPrefix() + "hasPegasus");
	}

	@Override
	public boolean hasResizableMode() {
		return ConfigUtils.getBoolean(main(), this.getConfigPrefix() + "hasResizableMode");
	}

	@Override
	public boolean hasReverseMode() {
		return ConfigUtils.getBoolean(main(), this.getConfigPrefix() + "hasReverseMode");
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null
				&& player.getOpenInventory().getTopInventory().getSize() == 27)
			return;
		player.closeInventory();
		if (this.getMountEntityType() == null) {
			player.sendTranslatedMessage("hub." + getParent() + ".invalid");
			return;
		}
		HubStoredPlayer hubStoredPlayer = HubStoredPlayer.get(player);
		HubPlayer.get(player).setClickedMountItem(this);
		// Buy system
		if (itemAction.equals(ItemAction.INVENTORY_RIGHT_CLICK) && isBuyable()) {
			onBuy(player);
			return;
		}
		// Mount settings system
		if (itemAction.equals(ItemAction.INVENTORY_WHEEL_CLICK) || itemAction.equals(ItemAction.INVENTORY_DROP)) {
			if (!has(player)) {
				player.sendTranslatedMessage("hub." + getParent() + ".youmusthavethismounttoconfigit");
				return;
			}
			CustomPlayerInventory.get(MountSettingsInventory.class, player).open();
			return;
		}
		if (!haveCheck(player))
			return;
		HubPlayer hubPlayer = HubPlayer.get(player);
		if (hubPlayer.getMountEntity() != null && hubPlayer.getMountEntity().isValid()) {
			MountItem mountItem = hubPlayer.getMounted();
			if (mountItem != null && mountItem.equals(this)) {
				player.sendTranslatedMessage("hub." + getParent() + ".alreadymounted");
				return;
			}
		}
		boolean mounted = true;
		MountConfig mountConfig = hubStoredPlayer.getMountConfigs().get(this.getName());
		if (mountConfig == null) {
			mountConfig = new MountConfig(this.getName());
			hubStoredPlayer.getMountConfigs().put(this.getName(), mountConfig);
		}
		mounted = !mountConfig.isBaby();
		if (RaceListener.racePlayers.keySet().contains(player)) {
			player.sendTranslatedMessage("hub.race.youcannotdothatinrace");
			return;
		}
		if (mounted) {
			if (MountManager.rideEntity(player, this.getMountEntityType(), false, false, 0.3D, false, mounted)) {
				hubPlayer.setMounted(this);
				if (mounted)
					player.sendTranslatedMessage("hub.mounted", player.getTranslatedMessage(this.getName())[0]);
			}
		}else{
			hubPlayer.setFakeEntity(MountManager.spawn(player.getLocation(), this.getMountEntityType(), WatcherSheep.class, true, false, false, false, new TranslatableString(this.getName())));
			player.sendTranslatedMessage("hub.mountedpet", player.getTranslatedMessage(this.getName())[0]);
		}
	}

	@Override
	public ItemStack toItemStack(BadblockPlayer player) {
		boolean configuratorInventory = false;
		HubPlayer hubPlayer = HubPlayer.get(player);
		if (hubPlayer.getCurrentInventory() != null && hubPlayer.getCurrentInventory().getLines() <= 3)
			configuratorInventory = true;
		String ownedKey = has(player) ? "hub." + getParent() + ".owned" : "hub." + getParent() + ".unowned";
		String ownedColorKey = ownedKey + ".color";
		String ownedSuffixKey = ownedKey + ".suffix";
		String configuratorKeySuffix = configuratorInventory ? ".description" : "";
		if (!has(player))
			return build(Material.STAINED_GLASS_PANE, this.getAmount(), (byte) 7,
					player.getTranslatedMessage(ownedColorKey)[0] + player.getTranslatedMessage(this.getName(),
							player.getTranslatedMessage(ownedSuffixKey)[0])[0],
					player.getTranslatedMessage(this.getLore() + configuratorKeySuffix,
							player.getTranslatedMessage(ownedKey), getNeededBadcoins()));
		return build(this.getMaterial(), this.getAmount(), this.getData(), player.getTranslatedMessage(ownedColorKey)[0]
				+ player.getTranslatedMessage(this.getName(), player.getTranslatedMessage(ownedSuffixKey)[0])[0],
				player.getTranslatedMessage(this.getLore() + configuratorKeySuffix,
						player.getTranslatedMessage(ownedKey), getNeededBadcoins()));
	}

	protected abstract EntityType getMountEntityType();

}
