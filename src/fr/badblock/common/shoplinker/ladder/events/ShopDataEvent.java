package fr.badblock.common.shoplinker.ladder.events;

import fr.badblock.common.shoplinker.api.objects.ShopData;
import fr.badblock.ladder.api.events.Cancellable;
import fr.badblock.ladder.api.events.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data@EqualsAndHashCode(callSuper = false) @AllArgsConstructor
public abstract class ShopDataEvent extends Event implements Cancellable {
	
	private ShopData shopData;
	
}
