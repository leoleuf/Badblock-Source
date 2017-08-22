package fr.badblock.common.shoplinker.bukkit.listeners.bukkit;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import fr.badblock.common.shoplinker.bukkit.clickers.ClickableObject;
import fr.badblock.common.shoplinker.bukkit.clickers.managers.ArmorStandManager;
import fr.badblock.common.shoplinker.bukkit.commands.ShopLinkerCommand;
import fr.badblock.common.shoplinker.bukkit.inventories.BukkitInventories;

public class EntityDamageByEntityListener implements Listener {

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager() == null) return;
		if (!event.getDamager().getType().equals(EntityType.PLAYER)) return;
		Player player = (Player) event.getDamager();
		Entity entity = event.getEntity();
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
			event.setCancelled(true);
			// Open inventory
			BukkitInventories.openInventory(player, clickableObject.getInventoryName());
			return;
		}
		event.setCancelled(true);
		ArmorStandManager armorStandManager = ArmorStandManager.getInstance();
		ClickableObject signObject = new ClickableObject(entityLocation, inventoryName);	
		armorStandManager.setArmorStand(signObject);
		armorStandManager.save();
		player.sendMessage(ChatColor.RED + "[ShopLinker] You must right click to select an armor stand.");
	}
	
}
