
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.auracolor.items;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.game.core18R3.players.data.GamePlayerData;
import fr.badblock.gameapi.players.BadblockPlayer;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ChatColor;

@Getter
@Setter
public abstract class AuraColor2Item extends CustomItem {

	private String	colorName;
	private int		red;
	private int		green;
	private int		blue;
	
	public AuraColor2Item(String colorName, int red, int green, int blue, byte data) {
		super("hub.items.auracoloritem." + ChatColor.stripColor(colorName).toLowerCase() + "_name", Material.STAINED_CLAY, data,
				"hub.items.auracoloritem." + ChatColor.stripColor(colorName).toLowerCase() + "_lore", false);
		this.setColorName(colorName);
		this.setRed(red);
		this.setGreen(green);
		this.setBlue(blue);
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		player.closeInventory();
		GamePlayerData playerData = (GamePlayerData) player.getPlayerData();
		playerData.setAuraRed2(getRed());
		playerData.setAuraGreen2(getGreen());
		playerData.setAuraBlue2(getBlue());
		player.sendTranslatedMessage("hub.items.auracoloritem.changed", getColorName());
	}

}
