package fr.badblock.common.shoplinker.bukkit;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;

import fr.badblock.rabbitconnector.RabbitService;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter public class ShopLinker extends JavaPlugin {

	@Getter@Setter 	 private static ShopLinker			 instance;
	@Getter@Setter   private static ConsoleCommandSender console;
	
	private RabbitService								 rabbitService;
	// bordel
	private String			 							 notEnoughCoinsMessage;
	private String			 							 dependNeededMessage;
	private String			 							 unknownDependOfferNameMessage;
	private String			 							 alreadyBoughtMessage;
	private String			 							 youBoughtMessage;
	private String			 							 unknownOfferNameMessage;
	private String			 							 checkTransactionMessage;
	private String			 							 websiteConnectionMessage;
	private String			 							 searchOfferMessage;
	private String			 							 notRegisteredMessage;
	private String			 							 nothingToClaimMessage;
	private String			 							 boughtMessage;
	private String			 							 rewardMessage;
	private String			 							 singlePendingMessage;
	private String			 							 pluralPendingMessage;
	private String			 							 errorMessage;
	private String			 							 pluralHoverMessage;
	private String			 							 singleHoverMessage;
	private String			 							 singleClaimMessage;
	private String			 							 pluralClaimMessage;
	private Gson										 notRestrictiveGson;

	@Override
	public void onEnable() {
		new ShopLinkerLoader(this);
	}
	
	@Override
	public void onDisable() {
		if (this.getRabbitService() != null)
			this.getRabbitService().remove();
	}
	
}
