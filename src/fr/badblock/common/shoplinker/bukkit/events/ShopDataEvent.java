package fr.badblock.common.shoplinker.bukkit.events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

import fr.badblock.common.shoplinker.api.objects.ShopData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data@EqualsAndHashCode(callSuper = false) @AllArgsConstructor
public abstract class ShopDataEvent extends Event implements Cancellable {
	
	private ShopData shopData;
	
}
