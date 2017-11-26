package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.mounts.settings.pegasus;

import java.util.Arrays;
import java.util.List;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Block;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.items.CustomItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.ColorConverter;

public class PegasusWingsColorSelectorInventory extends CustomInventory {

	@SuppressWarnings("deprecation")
	public PegasusWingsColorSelectorInventory() {
		super("hub.items.pegasuswingscolorselectorinventory", (DyeColor.values().length / 9) + 1);
		int i = -1;
		for (DyeColor dyeColor : DyeColor.values()) {
			i++;
			String name = "hub.items.pegasuswingscolorselectorinventory.color." + dyeColor.name().toLowerCase();
			this.setItem(i, new CustomItem(name, Material.WOOL, dyeColor.getWoolData(), name + ".lore") {

				@Override
				public List<ItemAction> getActions() {
					return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_RIGHT_CLICK,
							ItemAction.INVENTORY_LEFT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
				}

				@Override
				public void onClick(BadblockPlayer player, ItemAction action, Block clickedBlock) {
					HubPlayer hubPlayer = HubPlayer.get(player);
					hubPlayer.setDyeColor(dyeColor);
					player.sendTranslatedMessage("hub.items.pegasuswingscolorselectorinventory.choosedcolor",
							ColorConverter.dyeToChat(dyeColor), player.getTranslatedMessage(name));
					player.closeInventory();
				}

			});
		}

		/*
		 * this.setItem((this.getLines() * 9) - 2, new
		 * CustomItem("hub.items.pegasuswingscolorselectorinventory.glint",
		 * Material.COBBLE_WALL,
		 * "hub.items.pegasuswingscolorselectorinventory.glint.lore") {
		 * 
		 * @Override public List<ItemAction> getActions() { return
		 * Arrays.asList(ItemAction.INVENTORY_DROP,
		 * ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_LEFT_CLICK,
		 * ItemAction.INVENTORY_WHEEL_CLICK); }
		 * 
		 * @Override public void onClick(BadblockPlayer player, ItemAction
		 * action, Block clickedBlock) { HubPlayer hubPlayer =
		 * HubPlayer.get(player); if (hubPlayer.getClickedMountItem() == null)
		 * return; HubStoredPlayer hubStoredPlayer =
		 * HubStoredPlayer.get(player); if
		 * (hubStoredPlayer.getMountConfigs().containsKey(hubPlayer.
		 * getClickedMountItem().getName())) if
		 * (hubStoredPlayer.getMountConfigs().get(hubPlayer.getClickedMountItem(
		 * ).getName()).isGlint()) {
		 * hubStoredPlayer.getMountConfigs().get(hubPlayer.getClickedMountItem()
		 * .getName()).setGlint(false); player.sendTranslatedMessage(
		 * "hub.items.pegasuswingscolorselectorinventory.glint.disabled");
		 * }else{
		 * hubStoredPlayer.getMountConfigs().get(hubPlayer.getClickedMountItem()
		 * .getName()).setGlint(true); player.sendTranslatedMessage(
		 * "hub.items.pegasuswingscolorselectorinventory.glint.enabled"); }
		 * player.closeInventory(); }
		 * 
		 * });
		 */
		this.setItem((this.getLines() * 9) - 1, new BackMountSettingsItem());
	}

}
