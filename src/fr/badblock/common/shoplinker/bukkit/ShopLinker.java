package fr.badblock.common.shoplinker.bukkit;

import java.util.List;

import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.gson.Gson;

import fr.badblock.api.common.tech.rabbitmq.RabbitService;
import fr.badblock.common.shoplinker.bukkit.listeners.rabbitmq.ReceiveCommandListener;
import fr.badblock.common.shoplinker.mongodb.MongoService;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter public class ShopLinker extends JavaPlugin {

	@Getter@Setter 	 private static ShopLinker			 instance;
	@Getter@Setter   private static ConsoleCommandSender console;
	
	private RabbitService								 rabbitService;
	private MongoService							     mongoService;
	
	private ReceiveCommandListener			receiveCommandListener;
	private boolean										unloaded;
	
	// bordel
	private List<String>							     broadcastMessage;
	private List<String>							     backLore;
	private List<String>							     confirmLore;
	private List<String>							     cancelLore;
	private String										 cancelledMessage;
	private String										 backName;
	private String										 cancelName;
	private String										 confirmName;
	private String										 confirmInventoryName;
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
	private String			 							 animationMessage;
	private String			 							 webActionCompleteMessage;
	private String			 							 boughtMessage;
	private String			 							 rewardMessage;
	private List<String>			 					 singlePendingMessage;
	private List<String>			 					 pluralPendingMessage;
	private String			 							 errorMessage;
	private String			 							 pluralHoverMessage;
	private String			 							 singleHoverMessage;
	private String			 							 singleClaimMessage;
	private String			 							 pluralClaimMessage;
	private String			 							 pleaseWaitMessage;
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
