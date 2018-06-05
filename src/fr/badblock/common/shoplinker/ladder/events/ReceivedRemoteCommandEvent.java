package fr.badblock.common.shoplinker.ladder.events;

import fr.badblock.common.shoplinker.api.objects.ShopData;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author xMalware
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper=false) public class ReceivedRemoteCommandEvent extends ShopDataEvent {

	private boolean cancelled;
	
	public ReceivedRemoteCommandEvent(ShopData shopData) {
		super(shopData);
	}

}
