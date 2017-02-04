package fr.badblock.rush.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.achievements.PlayerAchievement;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.players.data.PlayerAchievementState;
import fr.badblock.rush.RushAchievementList;
import fr.badblock.rush.entities.RushTeamData;
import fr.badblock.rush.players.RushData;
import fr.badblock.rush.players.RushScoreboard;

public class BedListenerUtils {
	public static BadblockTeam parseBedTeam(Block bed){
		if(bed.getType() != Material.BED_BLOCK) return null;

		BadblockTeam result = null;

		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			Location teamBed = team.teamData(RushTeamData.class).getFirstBedPart();

			if(teamBed == null) continue;

			Location teamBed2  = team.teamData(RushTeamData.class).findOtherBedPart();

			if(teamBed2 == null)
				teamBed2 = teamBed;

			if(teamBed.distance(bed.getLocation()) == 0 || teamBed2.distance(bed.getLocation()) == 0)
				result = team;
		}

		return result;
	}

	public static boolean onBreakBed(BadblockPlayer player, Block block, boolean explosion){
		BadblockTeam team = parseBedTeam(block);

		if(team != null && player.getTeam() != null){
			if(team.equals(player.getTeam())){
				if(!explosion)
					player.sendTranslatedTitle("rush.yourebed");
			} else {

				team.die();

				Block other = team.teamData(RushTeamData.class).findOtherBedPart().getBlock();

				if(other.equals(block))
					other = team.teamData(RushTeamData.class).getFirstBedPart().getBlock();

				block.setType(Material.AIR);
				other.setType(Material.AIR);

				team.teamData(RushTeamData.class).broked(explosion, player.getName());

				player.getPlayerData().incrementStatistic("rush", RushScoreboard.BROKENBEDS);
				player.inGameData(RushData.class).brokedBeds++;

				incrementAchievements(player, RushAchievementList.RUSH_EBED_1, RushAchievementList.RUSH_EBED_2, RushAchievementList.RUSH_EBED_3, RushAchievementList.RUSH_EBED_4, RushAchievementList.RUSH_EXPLODER);
				incrementAchievements(player, RushAchievementList.RUSH_BED_1, RushAchievementList.RUSH_BED_2, RushAchievementList.RUSH_BED_3, RushAchievementList.RUSH_BED_4, RushAchievementList.RUSH_BROKER);

				player.getTeam().teamData(RushTeamData.class).health+=4;
				player.getTeam().getOnlinePlayers().forEach(pl -> {
					pl.setMaxHealth(player.getMaxHealth() + 4);
					pl.setHealth(player.getHealth() + 4);
				});

				for(Player bukkitPlayer : Bukkit.getOnlinePlayers()){
					BadblockPlayer bPlayer = (BadblockPlayer) bukkitPlayer;
					bPlayer.getCustomObjective().generate();

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
	}
}
