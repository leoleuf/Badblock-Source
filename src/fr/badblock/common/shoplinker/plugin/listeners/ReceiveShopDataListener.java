package fr.badblock.common.shoplinker.plugin.listeners;

import org.bukkit.Bukkit;

import com.google.gson.Gson;

import fr.badblock.common.shoplinker.api.ShopData;
import fr.badblock.common.shoplinker.api.ShopLinkerSettings;
import fr.badblock.common.shoplinker.plugin.ShopLinkWorker;
import fr.badblock.common.shoplinker.plugin.ShopLinker;
import fr.badblock.common.shoplinker.plugin.events.PlayerBuyEvent;
import fr.badblock.rabbitconnector.RabbitConnector;
import fr.badblock.rabbitconnector.RabbitListener;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Data @EqualsAndHashCode(callSuper=false)
public class ReceiveShopDataListener extends RabbitListener {

	@Getter public static Gson gson	= new Gson();
	
	private boolean enablePex;
	
	public ReceiveShopDataListener(String queueName) {
		super(RabbitConnector.getInstance().getService("default"), ShopLinkerSettings.QUEUE_PREFIX + queueName, ShopLinkerSettings.DEBUG, ShopLinkerSettings.LISTENER_TYPE);
		this.enablePex = ShopLinker.getInstance().getConfig().getBoolean("enablePex");
	}

	@Override
	public void onPacketReceiving(String body) {
		if (body == null) return;
		ShopData shopData = gson.fromJson(body, ShopData.class);
		if (shopData == null) return;
		// exécution
		if (enablePex) ShopLinkWorker.workWithPermissionsEx(shopData);
		Bukkit.getPluginManager().callEvent(new PlayerBuyEvent(shopData));
	}

}
