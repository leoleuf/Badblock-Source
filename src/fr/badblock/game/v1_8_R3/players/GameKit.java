package fr.badblock.game.v1_8_R3.players;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import com.google.gson.JsonObject;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.PlayerAchievement;
import fr.badblock.gameapi.players.data.InGameKitData;
import fr.badblock.gameapi.players.kits.PlayerKit;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.itemstack.ItemAction;
import fr.badblock.gameapi.utils.itemstack.ItemEvent;
import fr.badblock.gameapi.utils.itemstack.ItemStackExtra;
import fr.badblock.gameapi.utils.itemstack.ItemStackExtra.ItemPlaces;
import lombok.Data;

@Data
public class GameKit implements PlayerKit {
	private String 	   kitName;
	private boolean    VIP;

	private Material   kitItemType;
	private short	   kitItemData;

	private KitLevel[] levels;

	private String[] lore(BadblockPlayer player){
		List<String> result = new ArrayList<>();

		for(String lore : GameAPI.i18n().get(player.getPlayerData().getLocale(), "kits." + kitName + ".itemLore")){
			result.add(lore);
		}

		result.add("");

		int 	level 	  = player.getPlayerData().getUnlockedKitLevel(this);
		boolean canUnlock = player.getPlayerData().canUnlockNextLevel(this);


		if(!canUnlock){
			result.add(GameAPI.i18n().get(player.getPlayerData().getLocale(), "kits.toUnlock")[0]);

			for(PlayerAchievement achievement : getNeededAchievements(level + 1)){
				if(!player.getPlayerData().getAchievementState(achievement).isSucceeds()){
					result.add(GameAPI.i18n().get(player.getPlayerData().getLocale(), "kits.missingAchievement", achievement.getDisplayName())[0]);
				}
			}
			
			if(getBadcoinsCost(level + 1) < player.getPlayerData().getBadcoins()){
				result.add(GameAPI.i18n().get(player.getPlayerData().getLocale(), "kits.missingBadcoins", getBadcoinsCost(level + 1))[0]);
			}
			
			if(level >= 1) result.add("");
		}
		
		if(level >= 1){
			if(!kitName.equals(player.getPlayerData().getLastUsedKit(GameAPI.getInternalGameName())))
				result.add(GameAPI.i18n().get(player.getPlayerData().getLocale(), "kits.canChoose")[0]);
			else result.add(GameAPI.i18n().get(player.getPlayerData().getLocale(), "kits.alreadyChoosed")[0]);
		}

		return result.toArray(new String[0]);
	}

	@Override
	public ItemStackExtra getKitItem(BadblockPlayer player){
		return GameAPI.getAPI().createItemStackFactory()
				.type(kitItemType)
				.durability(kitItemData)
				.displayName(GameAPI.getAPI().getI18n().get(player.getPlayerData().getLocale(), "kits." + kitName + ".itemDisplayname")[0])
				.lore(lore(player))
				.asExtra(1)
				.listenAs(new ItemEvent(){
					@Override
					public boolean call(ItemAction action, BadblockPlayer player) {
						if(player.getPlayerData().getUnlockedKitLevel(GameKit.this) == 0){
							if(player.getPlayerData().canUnlockNextLevel(GameKit.this)){
								player.getPlayerData().removeBadcoins(levels[0].getBadcoinsCost());
								player.sendTranslatedMessage("kits.unlockLevel", new TranslatableString("kits." + kitName + ".name"), 1);
							} else {
								player.sendTranslatedMessage("kits.canNotUnlockLevel", new TranslatableString("kits." + kitName + ".name"), 1);
								player.closeInventory();
								return true;
							}
						} else {
							player.sendTranslatedMessage("kits.selected", new TranslatableString("kits." + kitName + ".name"));	
						}

						player.inGameData(InGameKitData.class).setChoosedKit(GameKit.this);
						player.closeInventory();

						return true;
					}
				}, ItemPlaces.INVENTORY_CLICKABLE)
				.listen(new ItemEvent(){
					@Override
					public boolean call(ItemAction action, BadblockPlayer player) {
						int next = player.getPlayerData().getUnlockedKitLevel(GameKit.this) + 1;

						if(player.getPlayerData().canUnlockNextLevel(GameKit.this)){
							player.getPlayerData().removeBadcoins(levels[0].getBadcoinsCost());
							player.sendTranslatedMessage("kits.unlockLevel", new TranslatableString("kits." + kitName + ".name"), next);
						} else {
							player.sendTranslatedMessage("kits.canNotUnlockLevel", new TranslatableString("kits." + kitName + ".name"), next);
							player.closeInventory();
							return true;
						}

						player.inGameData(InGameKitData.class).setChoosedKit(GameKit.this);
						player.closeInventory();

						return true;
					}
				}, ItemAction.INVENTORY_RIGHT_CLICK);
	}

	@Override
	public PlayerAchievement[] getNeededAchievements(int level) {
		if(level <= 0 || level > getMaxLevel()){
			throw new IllegalArgumentException("Level must be between 1 and " + getMaxLevel() + ", not " + level);
		}

		KitLevel kitLevel = levels[level - 1];
		PlayerAchievement[] result = new PlayerAchievement[kitLevel.getNeededAchievements().length];


		for(int i=0;i<kitLevel.getNeededAchievements().length;i++){
			String 			  name 		  = kitLevel.getNeededAchievements()[i];
			PlayerAchievement achievement = GameAPI.getAPI().getAchievement(name);
			result[i]					  = achievement;
		}

		return result;
	}

	@Override
	public int getBadcoinsCost(int level) {
		if(level <= 0 || level > getMaxLevel()){
			throw new IllegalArgumentException("Level must be between 1 and " + getMaxLevel() + ", not " + level);
		}

		KitLevel kitLevel = levels[level - 1];

		return kitLevel.getBadcoinsCost();
	}

	@Override
	public int getMaxLevel(){
		return levels.length;
	}

	@Override
	public void giveKit(BadblockPlayer player) {
		int level = player.getPlayerData().getUnlockedKitLevel(this);

		if(level == 0) return; // le joueur n'a pas d�bloqu� le kit :o

		if(level <= 0 || level > getMaxLevel()){
			throw new IllegalArgumentException("Level must be between 1 and " + getMaxLevel() + ", not " + level);
		}

		KitLevel kitLevel = levels[level - 1];
		GameAPI.getAPI().getKitContentManager().give(kitLevel.getStuff(), player);
	}

	@Data public static class KitLevel {
		private String[]   neededAchievements;
		private int 	   badcoinsCost;

		private JsonObject stuff;
	}
}
