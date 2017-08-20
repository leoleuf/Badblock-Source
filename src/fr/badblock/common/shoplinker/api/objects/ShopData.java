package fr.badblock.common.shoplinker.api.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class ShopData {

	private ShopType dataType;
	private String 	 playerName;
	private String   displayName;
	private String 	 objectName;
	private int[] 	 depends;
	private boolean  multibuy;
	
}
