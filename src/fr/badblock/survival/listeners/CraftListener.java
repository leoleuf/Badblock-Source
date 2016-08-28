package fr.badblock.survival.listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;

import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.itemstack.ItemStackUtils;
import fr.badblock.survival.SGAchievementList;

public class CraftListener extends BadListener {
	@EventHandler
	public void onCraft(InventoryClickEvent e){
		if(ItemStackUtils.isValid(e.getCurrentItem()) && e.getWhoClicked().getType() == EntityType.PLAYER){
			BadblockPlayer player = (BadblockPlayer) e.getWhoClicked();
			
			if(e.getCurrentItem().getType() == Material.DIAMOND_SWORD){
				player.getPlayerData().incrementAchievements(player, SGAchievementList.SG_FORGERON);
			} else if(e.getCurrentItem().getType() == Material.FISHING_ROD){
				player.getPlayerData().incrementAchievements(player, SGAchievementList.SG_ARTISAN_FISHER);
			}
		}
	}
}
