package fr.badblock.bukkit.games.badsoup.result;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import fr.badblock.bukkit.games.badsoup.players.SoupData;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayerData;

public class SoupResults {
	public SoupResults(String time){
		Collection<BadblockPlayerData> data  = GameAPI.getAPI().getGameServer().getSavedPlayers();
	
		List<BadblockPlayerData> inOrderPlayers = data.stream().sorted((a, b) -> {
			return a.inGameData(SoupData.class).compare( b.inGameData(SoupData.class) );
		}).collect(Collectors.toList());
		
		for(BadblockPlayerData playerData : inOrderPlayers){
			BadblockPlayer player = (BadblockPlayer) Bukkit.getPlayer(playerData.getUniqueId());
			
			if(player != null){
				SoupResult rushResult = new SoupResult(player);
				rushResult.doPlayersTop(inOrderPlayers);
				rushResult.doGeneral(time, inOrderPlayers.size());
				
				player.postResult(rushResult);
			}
		}
	}
}
