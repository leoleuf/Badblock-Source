package fr.badblock.bukkit.games.spaceballs.rockets;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;

public class RocketDiamondsErase implements Rocket {
	@Override
	public int getRange() {
		return 30;
	}

	@Override
	public String getName() {
		return "diamondserase";
	}

	@Override
	public void impact(BadblockPlayer launcher, Block block) {
		BadblockPlayer with = getNearbiestPlayer(block.getLocation(), 3.0d, launcher);

		if(with != null && with.getTeam() != null && with.getTeam().equals(launcher.getTeam()))
			with = null;
		
		if(with == null){
			messageNoTarget(launcher);
		} else removeDiams(launcher, with);
	}

	@Override
	public void impact(BadblockPlayer launcher, Entity entity) {
		if(entity instanceof BadblockPlayer){
			BadblockPlayer with = (BadblockPlayer) entity;
			
			if(test(with, launcher) != null){
				removeDiams(launcher, (BadblockPlayer) with);
				return;
			}
		}
			
		impact(launcher, entity.getLocation().getBlock());
	}
	
	protected BadblockPlayer test(BadblockPlayer player, BadblockPlayer launcher){
		if(player != null && player.getTeam() != null && player.getTeam().equals(launcher.getTeam()))
			return null;
		
		return player;
	}
	
	protected void removeDiams(BadblockPlayer launcher, BadblockPlayer with){
		int count = with.countItems(Material.DIAMOND, (byte) 0);
	
		if(count == 0){
			launcher.sendMessage("spaceballs.rockets.diamondserase.nodiams");
		} else {
			with.removeItems(Material.DIAMOND, (byte) 0, -1);
			launcher.sendTranslatedMessage("spaceballs.rockets.diamondserase.removed", with.getName());
			with.sendTranslatedMessage("spaceballs.rockets.diamondserase.removed-by", launcher.getName());
			
			GameAPI.getAPI().getOnlinePlayers().forEach(player -> player.sendTranslatedMessage("spaceballs.rockets.diamondserase.removeinfo", count,
					launcher.getTeam().getChatName(), launcher.getName(),
					with.getTeam().getChatName(), with.getName()));
		}
	}
}
