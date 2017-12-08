package fr.badblock.bukkit.games.bedwars.listeners;

import fr.badblock.bukkit.games.bedwars.BedWarsAchievementList;
import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
import fr.badblock.bukkit.games.bedwars.players.BedWarsData;
import fr.badblock.bukkit.games.bedwars.players.BedWarsScoreboard;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.game.rankeds.RankedManager;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.players.data.PlayerAchievementState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@SuppressWarnings("WeakerAccess")
public class BedListenerUtils {
	public static BadblockTeam parseBedTeam(Block bed){
		if(bed.getType() != Material.BED_BLOCK) return null;
		BadblockTeam result = null;
		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			Location teamBed = team.teamData(BedWarsTeamData.class).getFirstBedPart();
			if(teamBed == null) continue;
			Location teamBed2  = team.teamData(BedWarsTeamData.class).findOtherBedPart();
			if(teamBed2 == null) teamBed2 = teamBed;
			if(teamBed.distance(bed.getLocation()) == 0 || teamBed2.distance(bed.getLocation()) == 0) result = team;
		}
		return result;
	}

	@SuppressWarnings("deprecation")
    public static boolean onBreakBed(BadblockPlayer player, Block block, boolean explosion){
		BadblockTeam team = parseBedTeam(block);
		if(team != null && player.getTeam() != null){
			if(team.equals(player.getTeam())){
				if(!explosion) player.sendTranslatedTitle("bedwars.yourebed");
			} else {
				team.die();
				Block other = team.teamData(BedWarsTeamData.class).findOtherBedPart().getBlock();
				if(other.equals(block)) other = team.teamData(BedWarsTeamData.class).getFirstBedPart().getBlock();
				block.setType(Material.AIR);
				other.setType(Material.AIR);
				team.teamData(BedWarsTeamData.class).broked(explosion, player.getName());
				player.getPlayerData().incrementStatistic("rush", BedWarsScoreboard.BROKENBEDS);
				player.getPlayerData().incrementTempRankedData(RankedManager.instance.getCurrentRankedGameName(), BedWarsScoreboard.BROKENBEDS, 1);
				player.inGameData(BedWarsData.class).brokedBeds++;
				incrementAchievements(player, BedWarsAchievementList.bedwars_EBED_1, BedWarsAchievementList.bedwars_EBED_2, BedWarsAchievementList.bedwars_EBED_3, BedWarsAchievementList.bedwars_EBED_4, BedWarsAchievementList.bedwars_EXPLODER);
				incrementAchievements(player, BedWarsAchievementList.bedwars_BED_1, BedWarsAchievementList.bedwars_BED_2, BedWarsAchievementList.bedwars_BED_3, BedWarsAchievementList.bedwars_BED_4, BedWarsAchievementList.bedwars_BROKER);
				player.getTeam().teamData(BedWarsTeamData.class).health+=4;
				player.getTeam().getOnlinePlayers().forEach(pl -> {
                    pl.setMaxHealth(player.getMaxHealth() + 4);
					pl.setHealth(player.getHealth() + 4);
				});
				for(Player bukkitPlayer : Bukkit.getOnlinePlayers()){
					BadblockPlayer bPlayer = (BadblockPlayer) bukkitPlayer;
					if (bPlayer.getCustomObjective() != null) bPlayer.getCustomObjective().generate();
					String type = explosion ? "rush.explodeBed" : "rush.breakBed";
					bPlayer.sendTranslatedTitle(type, player.getName(), player.getTeam().getChatName(), team.getChatName());
					bPlayer.sendTimings(10, 40, 10);
				}

			}
			return false;
		}
		return false;
	}

	private static void incrementAchievements(BadblockPlayer player, PlayerAchievement... achievements){
		for(PlayerAchievement achievement : achievements){
			PlayerAchievementState state = player.getPlayerData().getAchievementState(achievement);
			state.progress(1.0d);
			state.trySucceed(player, achievement);
		}
		player.saveGameData();
	}
}
