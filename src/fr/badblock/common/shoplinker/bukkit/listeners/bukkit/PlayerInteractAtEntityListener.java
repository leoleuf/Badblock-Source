package fr.badblock.common.shoplinker.bukkit.listeners.bukkit;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

import fr.badblock.common.shoplinker.bukkit.clickers.ClickableObject;
import fr.badblock.common.shoplinker.bukkit.clickers.managers.ArmorStandManager;
import fr.badblock.common.shoplinker.bukkit.commands.ShopLinkerCommand;
import fr.badblock.common.shoplinker.bukkit.inventories.BukkitInventories;

public class PlayerInteractAtEntityListener implements Listener {

	@EventHandler
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();
		if (entity == null) return;
		if (!entity.getType().equals(EntityType.ARMOR_STAND)) return;
		UUID uniquePlayerId = player.getUniqueId();
		String inventoryName = ShopLinkerCommand.armorSet.get(uniquePlayerId);
		Location entityLocation = entity.getLocation();
		if (inventoryName == null) {
			// manage as an inventory opener
			ArmorStandManager armorStandManager = ArmorStandManager.getInstance();
			ClickableObject clickableObject = armorStandManager.getArmorStand(entityLocation);
			// Not registered sign
			if (clickableObject == null) return;
			// Open inventory
			BukkitInventories.openInventory(player, clickableObject.getInventoryName());
			return;
		}
		ArmorStandManager armorStandManager = ArmorStandManager.getInstance();
		ClickableObject signObject = new ClickableObject(entityLocation, inventoryName);	
		armorStandManager.setArmorStand(signObject);
		armorStandManager.save();
		player.sendMessage(ChatColor.GREEN + "[ShopLinker] You set the armor stand as an inventory opener.");
		ShopLinkerCommand.armorSet.remove(uniquePlayerId);
	}
	
}
