package fr.badblock.common.shoplinker.bukkit.inventories.objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor

public class InventoryShopObject {

	private String				queueName;
	private String				action;
	private String				message;
	private int					neededCoins;
	private int[]				depends;
	private boolean				multibuy;
	
}
