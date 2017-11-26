
package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.defaults;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.hub.v1.effectlib.Effect;
import fr.badblock.bukkit.hub.v1.effectlib.EffectManager;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.market.ownitems.OwnableItem;
import fr.badblock.bukkit.hub.v1.listeners.vipzone.RaceListener;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.ConfigUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ParticleItem extends OwnableItem {

	private String particleName;
	private boolean configurable;

	public ParticleItem(String particleName) {
		super("particles", particleName, ConfigUtils.getMaterial(main(), "particles." + particleName + ".material"),
				(byte) ConfigUtils.getInt(main(), "particles." + particleName + ".data"), true);
		this.setParticleName(particleName);
	}

	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK,
				ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock) {
		if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null
				&& player.getOpenInventory().getTopInventory().getSize() == 27)
			return;
		player.closeInventory();
		if (this.getEffectClass() == null) {
			player.sendTranslatedMessage("hub." + this.getParent() + ".invalid");
			return;
		}
		HubPlayer hubPlayer = HubPlayer.get(player);
		hubPlayer.setClickedParticleItem(this);
		// Buy system
		if (itemAction.equals(ItemAction.INVENTORY_RIGHT_CLICK) && isBuyable()) {
			onBuy(player);
			return;
		}
		// Mount settings system
		if (itemAction.equals(ItemAction.INVENTORY_WHEEL_CLICK) || itemAction.equals(ItemAction.INVENTORY_DROP)) {
			if (!this.isConfigurable()) {
				notConfigurable(player);
				return;
			}
			if (!has(player)) {
				player.sendTranslatedMessage("hub." + this.getParent() + ".youmusthavethisparticletoconfigit");
				return;
			}
			this.onConfig(player);
			return;
		}
		// Use system
		if (!has(player)) {
			if (hasPermission())
				player.sendTranslatedMessage("hub.nopermission", player.getTranslatedMessage(this.getName())[0]);
			else
				player.sendTranslatedMessage("hub." + this.getParent() + ".unowned", player.getTranslatedMessage(this.getName())[0]);
			return;
		}
		boolean isEnabled = false;
		for (Effect effect : hubPlayer.getParticles())
			if (effect.getClass().equals(this.getEffectClass()) && !effect.isDone())
				isEnabled = true;
		if (isEnabled) {
			player.sendTranslatedMessage("hub." + this.getParent() + ".alreadyactivated");
			return;
		}
		if (RaceListener.racePlayers.keySet().contains(player)) {
			player.sendTranslatedMessage("hub.race.youcannotdothatinrace");
			return;
		}
		hubPlayer.getParticles().add(run(player, hubPlayer));
		player.sendTranslatedMessage("hub." + this.getConfigPrefix() + ".activated",
				player.getTranslatedMessage(this.getName())[0]);
	}

	@Override
	public ItemStack toItemStack(BadblockPlayer player) {
		boolean configuratorInventory = false;
		HubPlayer hubPlayer = HubPlayer.get(player);
		if (hubPlayer.getCurrentInventory() != null && hubPlayer.getCurrentInventory().getLines() <= 3)
			configuratorInventory = true;
		String ownedKey = has(player) ? "hub." + this.getParent() + ".owned" : "hub." + this.getParent() + ".unowned";
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

	protected void onConfig(BadblockPlayer player) {
		notConfigurable(player);
	}

	private void notConfigurable(BadblockPlayer player) {
		player.sendTranslatedMessage("hub." + this.getParent() + ".notconfigurable");
	}

	protected EffectManager getEffectManager() {
		return main().getEffectManager();
	}

	protected abstract Class<? extends Effect> getEffectClass();

	protected abstract Effect run(BadblockPlayer player, HubPlayer hubPlayer);

}
