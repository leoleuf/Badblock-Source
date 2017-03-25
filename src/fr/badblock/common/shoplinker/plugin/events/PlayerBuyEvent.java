package fr.badblock.common.shoplinker.plugin.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.badblock.common.shoplinker.api.ShopData;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Event qui dit que le joueur a achet� quelque chose :o
 * @author xMalware
 *
 */
@AllArgsConstructor
public class PlayerBuyEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}
	
	@Getter
	public ShopData shopData;
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

}
