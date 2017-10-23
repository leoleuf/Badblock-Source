package fr.badblock.bukkit.games.spaceballs.players;

import fr.badblock.bukkit.games.spaceballs.SBAchievementList;
import fr.badblock.gameapi.game.rankeds.RankedManager;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.data.InGameData;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SpaceData implements InGameData {
	public int kills 		= 0;
	public int deaths		= 0;
	public int diamonds     = 0;
	public int rockets      = 0;

	boolean bejwelerIncremented = false;

	public int getScore(){
		return (kills * 20 + diamonds * 5) / (deaths == 0 ? 1 : (/*10 * */deaths));
	}

	public void launchRocket(BadblockPlayer player){
		rockets++;
		player.getPlayerData().incrementAchievements(player, SBAchievementList.SB_ROCKET_1, SBAchievementList.SB_ROCKET_2, SBAchievementList.SB_ROCKET_3, SBAchievementList.SB_ROCKET_4, SBAchievementList.SB_ROCKETER);
	}

	public void putDiamant(BadblockPlayer player, int count){
		diamonds += count;

		player.getPlayerData().increaseStatistic("spaceballs", SpaceScoreboard.DIAMONDS, count);
		player.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), SpaceScoreboard.DIAMONDS, 1);
		for (int i = 0; i <= count; i++) {
			player.getPlayerData().incrementAchievements(player, SBAchievementList.SB_JEWELER_1, SBAchievementList.SB_JEWELER_2, SBAchievementList.SB_JEWELER_3, SBAchievementList.SB_JEWELER_4, SBAchievementList.SB_VBJEWELER);
		}

		if(diamonds >= 20 && !bejwelerIncremented){
			bejwelerIncremented = true;
			player.getPlayerData().incrementAchievements(player, SBAchievementList.SB_BJEWELER_1, SBAchievementList.SB_BJEWELER_2, SBAchievementList.SB_BJEWELER_3, SBAchievementList.SB_BJEWELER_4);
		}
	}
}
