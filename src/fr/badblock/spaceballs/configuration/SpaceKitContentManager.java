package fr.badblock.spaceballs.configuration;

import java.util.List;

import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonObject;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.configuration.BadConfiguration;
import fr.badblock.gameapi.configuration.values.MapValue;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.kits.DefaultKitContentManager;
import fr.badblock.spaceballs.rockets.Rockets;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

public class SpaceKitContentManager extends DefaultKitContentManager {
	public SpaceKitContentManager(boolean allowDrop) {
		super(allowDrop);
	}

	@Override
	public void give(JsonObject content, BadblockPlayer player) {
		super.give(content, player);
		
		BadConfiguration configuration = GameAPI.getAPI().loadConfiguration(content);
		List<MapRocket> rockets = configuration.getValueList("rockets", MapRocket.class);
		
		for(MapRocket rocket : rockets){
			Rockets handle = rocket.getHandle();
			
			if(handle == null)
				continue;
			
			ItemStack stack = Rockets.createRocket(handle, rocket.count);
			stack = Rockets.changeLanguage(stack, player.getPlayerData().getLocale());
			
			if(rocket.placeInInventory == -1){
				player.getInventory().addItem(stack);
			} else {
				player.getInventory().setItem(rocket.placeInInventory, stack);
			}
		}

		player.updateInventory();
	}
	
	@AllArgsConstructor@NoArgsConstructor
	public static class MapRocket implements MapValue<Rockets> {
		public String rocket 		   = Rockets.TELEPORT.name();
		public int    count            = 1;
		public int    placeInInventory = -1;
		
		@Override
		public Rockets getHandle() {
			try {
				return Rockets.valueOf(rocket.toUpperCase());
			} catch(Exception e){
				System.out.println("Trying to access unknow rocket " + rocket + "!");
				return null;
			}
		}
		
	}
}
