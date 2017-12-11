package fr.badblock.common.shoplinker.ladder.events;

import fr.badblock.common.shoplinker.api.objects.ShopData;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author xMalware
 */
@Data@EqualsAndHashCode(callSuper=false) public class ReceivedRemoteCommandEvent extends ShopDataEvent {

	private boolean cancelled;
	
	public ReceivedRemoteCommandEvent(ShopData shopData) {
		super(shopData);
	}

}
