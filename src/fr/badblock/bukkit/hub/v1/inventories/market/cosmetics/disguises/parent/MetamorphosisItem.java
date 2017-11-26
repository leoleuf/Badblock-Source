
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.parent;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.disguises.utils.MaterialDisguise;
import fr.badblock.bukkit.hub.v1.inventories.market.ownitems.OwnableItem;
import fr.badblock.bukkit.hub.v1.listeners.vipzone.RaceListener;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.disguise.Disguise;
import fr.badblock.gameapi.players.BadblockPlayer;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class MetamorphosisItem extends OwnableItem {

	private Disguise disguise;
	private String metamorphosisName;

	public MetamorphosisItem(String metamorphosisName, Material material, byte data) {
		super("disguises", metamorphosisName, material, data, true);
		this.setMetamorphosisName(metamorphosisName);
		this.assignDisguise();
	}

	private void assignDisguise() {
		this.setDisguise(this.getMetamorphosisEntityType() == null
				? new MaterialDisguise(this.getMaterial(), this.getData(), null, true, true)
				: new Disguise(this.getMetamorphosisEntityType(), null, true, true));
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	protected abstract EntityType getMetamorphosisEntityType();

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null
				&& player.getOpenInventory().getTopInventory().getSize() == 27)
			return;
		player.closeInventory();
		if (this.getDisguise() == null) {
			player.sendTranslatedMessage("hub." + this.getParent() + ".invalid");
			return;
		}
		// Buy system
		if (itemAction.equals(ItemAction.INVENTORY_RIGHT_CLICK) && isBuyable()) {
			onBuy(player);
			return;
		}
		// Use system
		if (!has(player)) {
			if (hasPermission())
				player.sendTranslatedMessage("hub.nopermission", player.getTranslatedMessage(this.getName())[0]);
			else
				player.sendTranslatedMessage("hub.unowned", player.getTranslatedMessage(this.getName())[0]);
			return;
		}
		HubPlayer hubPlayer = HubPlayer.get(player);
		if (hubPlayer.getDisguise() != null) {
			MetamorphosisItem mountItem = hubPlayer.getDisguise();
			if (mountItem != null && mountItem.equals(this)) {
				player.sendTranslatedMessage("hub.alreadymounted", player.getTranslatedMessage(this.getName())[0]);
				return;
			}
		}
		if (RaceListener.racePlayers.keySet().contains(player)) {
			player.sendTranslatedMessage("hub.race.youcannotdothatinrace");
			return;
		}
		hubPlayer.setDisguise(this);
		player.disguise(this.getDisguise());
		player.sendTranslatedMessage("hub.disguised", player.getTranslatedMessage(this.getName())[0]);
	}

	public String getParentOwnSystem() {
		return "disguises";
	}

}
