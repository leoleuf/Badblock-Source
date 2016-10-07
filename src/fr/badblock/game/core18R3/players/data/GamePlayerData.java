package fr.badblock.game.core18R3.players.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.data.GameData;
import fr.badblock.gameapi.players.data.PlayerAchievementState;
import fr.badblock.gameapi.players.data.PlayerData;
import fr.badblock.gameapi.players.data.boosters.PlayerBooster;
import fr.badblock.gameapi.players.kits.PlayerKit;
import fr.badblock.gameapi.utils.i18n.Locale;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

@Getter@ToString
public class GamePlayerData implements PlayerData {

	public static Locale								  defaultLocale	   = Locale.FRENCH_FRANCE;

	private int  				 						  badcoins     	   = 0;
	public int  				 						  shopPoints       = 0;
	private int  				 						  level	     	   = 1;
	private long 										  xp		       = 0L;
	private List<PlayerBooster>					  		  boosters		   = new ArrayList<>();
	private Map<String, Integer> 						  kits 		 	   = Maps.newConcurrentMap();
	private Map<String, String>							  lastUsedKits 	   = Maps.newConcurrentMap();
	private Map<String, PlayerAchievementState> 		  achievements 	   = Maps.newConcurrentMap();

	private Map<String, Map<String, Double>> 			  stats   	 	   = Maps.newConcurrentMap();

	private transient List<String>						  achloadeds	   = new ArrayList<>();

	private transient Map<String, GameData> 			  datas 		   = Maps.newConcurrentMap();
	private transient JsonObject 						  data		 	   = new JsonObject();
	private transient JsonObject 						  object		   = new JsonObject();

	private transient BadblockPlayer					  badblockPlayer;
	
	public void setData(JsonObject data){
		if(data.has("other")){
			this.data = data.get("other").getAsJsonObject();
		}
	}

	@Override
	public int addBadcoins(int badcoins, boolean applyBonus) {
		badcoins = Math.abs(badcoins);
		if (applyBonus) {
			GameAPI api = GameAPI.getAPI();
			double playerBonus = 0;
			for (Player playerz : Bukkit.getOnlinePlayers()) {
				BadblockPlayer bbPlayer = (BadblockPlayer) playerz;
				PlayerBooster playerBooster = null;
				for (PlayerBooster playerBoosterr : bbPlayer.getPlayerData().getBoosters())
					if (playerBoosterr.isEnabled() && !playerBoosterr.isExpired()) playerBooster = playerBoosterr;
				if (playerBooster != null) {
					playerBonus += playerBooster.getBooster().getCoinsMultiplier();
				}
			}
			if (playerBonus == 0) playerBonus = 1;
			double serverBonus = api.getServerBadcoinsBonus() <= 0 ? 1 : api.getServerBadcoinsBonus();
			badcoins *= serverBonus > playerBonus ? serverBonus : playerBonus;
		}
		return this.badcoins += badcoins;
	}

	@Override
	public void removeBadcoins(int badcoins) {
		this.badcoins -= Math.abs(badcoins);
	}

	@Override
	public long getXpUntilNextLevel() {
		Double doublet = Math.pow(1.1d, level + 1) * 100;
		return doublet.longValue();
	}

	@Override
	public long addXp(long xp, boolean applyBonus) {
		xp = Math.abs(xp);
		if (applyBonus) {
			GameAPI api = GameAPI.getAPI();
			double playerBonus = 0;
			for (Player playerz : Bukkit.getOnlinePlayers()) {
				BadblockPlayer bbPlayer = (BadblockPlayer) playerz;
				PlayerBooster playerBooster = null;
				for (PlayerBooster playerBoosterr : bbPlayer.getPlayerData().getBoosters())
					if (playerBoosterr.isEnabled() && !playerBoosterr.isExpired()) playerBooster = playerBoosterr;
				if (playerBooster != null) {
					playerBonus += playerBooster.getBooster().getXpMultiplier();
				}
			}
			if (playerBonus == 0) playerBonus = 1;
			double serverBonus = api.getServerXpBonus() <= 0 ? 1 : api.getServerXpBonus();
			xp *= serverBonus > playerBonus ? serverBonus : playerBonus;
		}
		long delta = getXpUntilNextLevel() - (xp + this.xp);
		// pas de passage de niveau
		if (delta > 0)
			return this.xp += xp;
		// passage de niveau jusqu'à ce qu'il y ait suffisament de niveau(x) passé(s) pour avoir une progression
		while (getXpUntilNextLevel() - (xp + this.xp) <= 0) level++;
		badblockPlayer.sendTranslatedMessage("game.level", level);
		badblockPlayer.playSound(Sound.LEVEL_UP);
		return this.xp = -(getXpUntilNextLevel() - (xp + this.xp));
	}

	@Override
	public PlayerAchievementState getAchievementState(@NonNull PlayerAchievement achievement) {
		String name = achievement.getName().toLowerCase();

		if(achievements.containsKey(name)){
			PlayerAchievementState ach = achievements.get(name);
			if(achievement.isTemp() && !achloadeds.contains(name) && !ach.isSucceeds()){
				ach.setProgress(0.0d);
			}

			return ach;
		} else {
			PlayerAchievementState state = new PlayerAchievementState();
			achievements.put(name, state);
			achloadeds.add(name);

			return state;
		}
	}

	@Override
	public void incrementAchievements(BadblockPlayer player, PlayerAchievement... achievements) {
		for(PlayerAchievement achievement : achievements){
			PlayerAchievementState state = getAchievementState(achievement);
			state.progress(1.0d);
			state.trySucceed(player, achievement);
		}		
	}

	@Override
	public int getUnlockedKitLevel(@NonNull PlayerKit kit) {
		String name = kit.getKitName().toLowerCase();

		if(kits.containsKey(name))
			return kits.get(name);
		else return 0;
	}

	@Override
	public boolean canUnlockNextLevel(@NonNull PlayerKit kit) {
		int nextLevel = getUnlockedKitLevel(kit) + 1;

		if(kit.getMaxLevel() < nextLevel || kit.getBadcoinsCost(nextLevel) > badcoins)
			return false;

		for(PlayerAchievement achievement : kit.getNeededAchievements(nextLevel)){
			if(!getAchievementState(achievement).isSucceeds())
				return false;
		}

		return true;
	}

	@Override
	public void unlockNextLevel(@NonNull PlayerKit kit){
		if(!canUnlockNextLevel(kit)) return;

		int nextLevel = getUnlockedKitLevel(kit) + 1;

		removeBadcoins(kit.getBadcoinsCost(nextLevel));
		kits.put(kit.getKitName().toLowerCase(), nextLevel);
	}

	@Override
	public String getLastUsedKit(@NonNull String game) {
		game = game.toLowerCase();
		return lastUsedKits.containsKey(game) ? lastUsedKits.get(game) : null;
	}

	@Override
	public void setLastUsedKit(@NonNull String game, String kit) {
		game = game.toLowerCase();
		kit  = kit == null ? null : kit.toLowerCase();


		if(kit == null && lastUsedKits.containsKey(game)){
			lastUsedKits.remove(game);
		} else if(kit != null){
			lastUsedKits.put(game, kit);
		}
	}

	@Override
	public Locale getLocale() {
		return defaultLocale;
	}

	@Override
	public double getStatistics(String gameName, String stat) {
		stat = stat.toLowerCase();
		Map<String, Double> gameStats = getGameStats(gameName);

		if(!gameStats.containsKey(stat)){
			gameStats.put(stat, 0d);
		}

		return gameStats.get(stat);
	}

	@Override
	public void incrementStatistic(String gameName, String stat){
		increaseStatistic(gameName, stat, 1.0d);
	}

	@Override
	public void increaseStatistic(String gameName, String stat, double value){
		stat = stat.toLowerCase();
		Map<String, Double> gameStats = getGameStats(gameName);

		if(!gameStats.containsKey(stat)){
			gameStats.put(stat, 0d);
		}

		double newValue = value + gameStats.get(stat);
		gameStats.put(stat, newValue);
	}

	protected Map<String, Double> getGameStats(String gameName){
		gameName = gameName.toLowerCase();

		if(!stats.containsKey(gameName)){
			stats.put(gameName, Maps.newConcurrentMap());
		}

		return stats.get(gameName);
	}

	@SuppressWarnings("unchecked") @Override
	public <T extends GameData> T gameData(String key, Class<T> clazz) {
		key = key.toLowerCase();

		T result = null;

		if(datas.containsKey(key)){
			result = (T) datas.get(key);
		} else if(data.has(key)){
			//System.out.println(data.get(key));
			result = GameAPI.getGson().fromJson(data.get(key), clazz);
			datas.put(key, result);
		} else
			try {
				result = clazz.getConstructor().newInstance();
				datas.put(key, result);
			} catch (Exception e) {
				e.printStackTrace();
			}

		return result;
	}

	@Override
	public JsonObject saveData() {
		JsonObject object = GameAPI.getGson().toJsonTree(this).getAsJsonObject();

		for(Entry<String, GameData> entries : datas.entrySet()){
			if(data.has(entries.getKey())){
				data.remove(entries.getKey());
			}

			data.add(entries.getKey(), GameAPI.getGson().toJsonTree(entries.getValue()));
		}

		JsonObject result = new JsonObject();

		object.add("other", data);
		result.add("game", object);

		return result;
	}

	@Override
	public int getShopPoints() {
		return shopPoints;
	}
	
}