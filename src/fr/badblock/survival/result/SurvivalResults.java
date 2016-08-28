package fr.badblock.survival.result;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayerData;
import fr.badblock.survival.players.SurvivalData;

public class SurvivalResults {
	public SurvivalResults(String time){
		Collection<BadblockPlayerData> data  = GameAPI.getAPI().getGameServer().getSavedPlayers();
	
		List<BadblockPlayerData> inOrderPlayers = data.stream().sorted((a, b) -> {
			return a.inGameData(SurvivalData.class).compare( b.inGameData(SurvivalData.class) );
		}).collect(Collectors.toList());
		
		for(BadblockPlayerData playerData : inOrderPlayers){
			BadblockPlayer player = (BadblockPlayer) Bukkit.getPlayer(playerData.getUniqueId());
			
			if(player != null){
				SurvivalResult rushResult = new SurvivalResult(player);
				rushResult.doPlayersTop(inOrderPlayers);
				rushResult.doGeneral(time, inOrderPlayers.size());
				
				player.postResult(rushResult);
			}
		}
	}
}
