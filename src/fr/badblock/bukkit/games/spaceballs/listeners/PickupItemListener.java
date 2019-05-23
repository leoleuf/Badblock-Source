package fr.badblock.bukkit.games.spaceballs.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerPickupItemEvent;

import fr.badblock.bukkit.games.spaceballs.rockets.Rockets;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;

public class PickupItemListener extends BadListener{
	@EventHandler(ignoreCancelled=true)
	public void onPickup(PlayerPickupItemEvent e){
		BadblockPlayer player = (BadblockPlayer) e.getPlayer();
		
		Rockets.changeLanguage(e.getItem().getItemStack(), player.getPlayerData().getLocale());
	}
}
