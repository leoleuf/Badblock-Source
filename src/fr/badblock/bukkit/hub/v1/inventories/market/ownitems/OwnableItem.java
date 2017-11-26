package fr.badblock.bukkit.hub.v1.inventories.market.ownitems;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.inventories.market.confirm.ConfirmCreator;
import fr.badblock.bukkit.hub.v1.objects.HubStoredPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.ConfigUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class OwnableItem extends CustomItem {

	private static char CONFIGURATION_SEPARATOR = '.';
	private static char KEY_SEPARATOR = '_';

	private String parent;
	private String ownableItemName;

	public OwnableItem(String parent, String ownableItemName, Material material, boolean itemFolder) {
		super("hub" + (itemFolder ? CONFIGURATION_SEPARATOR + "items" : "") + CONFIGURATION_SEPARATOR + parent
				+ CONFIGURATION_SEPARATOR + ownableItemName + CONFIGURATION_SEPARATOR + "displayname", material,
				(byte) 0, 1, "hub" + (itemFolder ? CONFIGURATION_SEPARATOR + "items" : "") + CONFIGURATION_SEPARATOR
						+ parent + CONFIGURATION_SEPARATOR + ownableItemName + CONFIGURATION_SEPARATOR + "lore");
		this.setParent(parent);
		this.setOwnableItemName(ownableItemName);
	}

	public OwnableItem(String parent, String ownableItemName, Material material, byte data, int amount,
			boolean itemFolder) {
		super("hub" + (itemFolder ? CONFIGURATION_SEPARATOR + "items" : "") + CONFIGURATION_SEPARATOR + parent
				+ CONFIGURATION_SEPARATOR + ownableItemName + CONFIGURATION_SEPARATOR + "displayname", material, data,
				amount, "hub" + (itemFolder ? CONFIGURATION_SEPARATOR + "items" : "") + CONFIGURATION_SEPARATOR + parent
						+ CONFIGURATION_SEPARATOR + ownableItemName + CONFIGURATION_SEPARATOR + "lore");
		this.setParent(parent);
		this.setOwnableItemName(ownableItemName);
	}

	public OwnableItem(String parent, String ownableItemName, Material material, byte data, boolean itemFolder) {
		super("hub" + (itemFolder ? CONFIGURATION_SEPARATOR + "items" : "") + CONFIGURATION_SEPARATOR + parent
				+ CONFIGURATION_SEPARATOR + ownableItemName + CONFIGURATION_SEPARATOR + "displayname", material, data,
				1, "hub" + (itemFolder ? CONFIGURATION_SEPARATOR + "items" : "") + CONFIGURATION_SEPARATOR + parent
						+ CONFIGURATION_SEPARATOR + ownableItemName + CONFIGURATION_SEPARATOR + "lore");
		this.setParent(parent);
		this.setOwnableItemName(ownableItemName);
	}

	public OwnableItem(String parent, String ownableItemName, Material material, int amount, boolean itemFolder) {
		super("hub" + (itemFolder ? CONFIGURATION_SEPARATOR + "items" : "") + CONFIGURATION_SEPARATOR + parent
				+ CONFIGURATION_SEPARATOR + ownableItemName + CONFIGURATION_SEPARATOR + "displayname", material,
				(byte) 0, amount,
				"hub" + (itemFolder ? CONFIGURATION_SEPARATOR + "items" : "") + CONFIGURATION_SEPARATOR + parent
						+ CONFIGURATION_SEPARATOR + ownableItemName + CONFIGURATION_SEPARATOR + "lore");
		this.setParent(parent);
		this.setOwnableItemName(ownableItemName);
	}

	public int getNeededBadcoins() {
		return ConfigUtils.getInt(main(), getConfigPrefix() + ".badcoins");
	}

	public boolean has(BadblockPlayer player) {
		return hasPermission() ? player.hasPermission("hub." + getConfigPrefix()) : own(player);
	}

	public boolean hasFunMode() {
		return ConfigUtils.getBoolean(main(), getConfigPrefix() + "hasFunmode");
	}

	public boolean hasPegasusMode() {
		return ConfigUtils.getBoolean(main(), getConfigPrefix() + "hasPegasus");
	}

	public boolean hasPermission() {
		return ConfigUtils.getBoolean(main(), getConfigPrefix() + "permission");
	}

	public boolean hasResizableMode() {
		return ConfigUtils.getBoolean(main(), getConfigPrefix() + "hasResizableMode");
	}

	public boolean hasReverseMode() {
		return ConfigUtils.getBoolean(main(), getConfigPrefix() + "hasReverseMode");
	}

	public boolean isBuyable() {
		return ConfigUtils.getBoolean(main(), getConfigPrefix() + "buyable");
	}

	// prefixes
	public String getConfigPrefix() {
		return getParent() + CONFIGURATION_SEPARATOR + getOwnableItemName() + CONFIGURATION_SEPARATOR;
	}

	private String getOwnKey() {
		return getParent() + KEY_SEPARATOR + getOwnableItemName();
	}

	protected boolean own(BadblockPlayer badblockPlayer) {
		HubStoredPlayer hubStoredPlayer = HubStoredPlayer.get(badblockPlayer);
		Set<String> properties = hubStoredPlayer.getProperties();
		return properties.contains(this.getOwnKey());
	}

	public void onBuy(BadblockPlayer player) {
		ConfirmCreator.buy(player, this);
	}

	public boolean haveCheck(BadblockPlayer player) {
		// Use system
		if (!has(player)) {
			if (hasPermission())
				player.sendTranslatedMessage("hub.nopermission", player.getTranslatedMessage(this.getName())[0]);
			else
				player.sendTranslatedMessage("hub.unowned", player.getTranslatedMessage(this.getName())[0]);
			return false;
		}
		return true;
	}

	public void addAsOwn(BadblockPlayer badblockPlayer) {
		HubStoredPlayer hubStoredPlayer = HubStoredPlayer.get(badblockPlayer);
		Set<String> properties = hubStoredPlayer.getProperties();
		properties.add(this.getOwnKey());
	}

	@Override
	public ItemStack toItemStack(BadblockPlayer player) {
		String ownedKey = has(player) ? "hub." + this.getParent() + ".owned" : "hub." + this.getParent() + ".unowned";
		String ownedColorKey = ownedKey + ".color";
		String ownedSuffixKey = ownedKey + ".suffix";
		if (!has(player))
			return build(Material.STAINED_GLASS_PANE, this.getAmount(), (byte) 7,
					player.getTranslatedMessage(ownedColorKey)[0] + player.getTranslatedMessage(this.getName(),
							player.getTranslatedMessage(ownedSuffixKey)[0])[0],
					player.getTranslatedMessage(this.getLore(), player.getTranslatedMessage(ownedKey),
							getNeededBadcoins()));
		return build(this.getMaterial(), this.getAmount(), this.getData(), player.getTranslatedMessage(ownedColorKey)[0]
				+ player.getTranslatedMessage(this.getName(), player.getTranslatedMessage(ownedSuffixKey)[0])[0],
				player.getTranslatedMessage(this.getLore(), player.getTranslatedMessage(ownedKey),
						getNeededBadcoins()));
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

}
