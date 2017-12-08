package fr.badblock.bukkit.games.bedwars.result;

import com.google.common.collect.Maps;
import fr.badblock.bukkit.games.bedwars.players.BedWarsData;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayerData;
import fr.badblock.gameapi.players.BadblockTeam;
import org.bukkit.Bukkit;

import java.util.*;

public class BedWarsResults {
	public BedWarsResults(String time, BadblockTeam winner){
		Collection<BadblockPlayerData> data = GameAPI.getAPI().getGameServer().getSavedPlayers();
		Collection<BadblockTeam> teams = GameAPI.getAPI().getGameServer().getSavedTeams();
		List<BadblockPlayerData> inOrderPlayers = new ArrayList<>();
		Map<BadblockTeam, Integer> scoredTeams = Maps.newConcurrentMap();
        data.stream().sorted((a, b) -> a.equals(b) ? 0 : a.inGameData(BedWarsData.class).getScore() < b.inGameData(BedWarsData.class).getScore() ? 1 : -1).forEach(inOrderPlayers::add);
		for(BadblockTeam team : teams){
			int score = 0;
			for(UUID uniqueId : team.getPlayersAtStart()) score += getScore(uniqueId, team, inOrderPlayers);
			scoredTeams.put(team, score);
		}
		Map<BadblockTeam, Integer> result = new LinkedHashMap<>();
		scoredTeams.entrySet().stream().sorted((a, b) -> a.equals(b) ? 0 : a.getValue() < b.getValue() ? 1 : -1).forEach(team -> result.put(team.getKey(), team.getValue()));
		for(BadblockPlayerData playerData : inOrderPlayers){
			BadblockPlayer player = (BadblockPlayer) Bukkit.getPlayer(playerData.getUniqueId());
			if(player != null){
				BedWarsResult rushResult = new BedWarsResult(player);
				rushResult.doPlayersTop(inOrderPlayers);
				rushResult.doTeamTop(result, winner);
				rushResult.doGeneral(time, teams.size(), inOrderPlayers.size());
				player.postResult(rushResult);
			}
		}
	}
	
	private int getScore(UUID player, BadblockTeam team, Collection<BadblockPlayerData> in){
		for(BadblockPlayerData p : in) {
            if(p.getUniqueId().equals(player)){
                if(p.getTeam() == null) p.setTeam(team);
                return p.inGameData(BedWarsData.class).getScore();
            }
        }
		return 0;
	}
}
