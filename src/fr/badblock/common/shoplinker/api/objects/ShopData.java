package fr.badblock.common.shoplinker.api.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class ShopData {

	private ShopType dataType;
	private String 	 playerName;
	private String 	 command;
	private String   displayName;
	private int[] 	 depends;
	private boolean  multibuy;
	private boolean  ingame;
	private	double	 price;
	
}
