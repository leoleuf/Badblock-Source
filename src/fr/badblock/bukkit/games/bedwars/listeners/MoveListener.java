package fr.badblock.bukkit.games.bedwars.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.players.BadblockTeam;

public class MoveListener extends BadListener {

	@EventHandler
	public void onClick(PrepareItemCraftEvent e)
	{
		if ((e.getRecipe() == null) || (e.getRecipe().getResult() == null)) {
			return;
		}
		e.getInventory().setItem(0, null);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent e){
		if(e.getTo().getY() <= 0.0d && !inGame()){
			Location spawn = PluginBedWars.getInstance().getConfiguration().spawn.getHandle();
			Entity vehicle = null;
			if(e.getPlayer().isInsideVehicle()){
				vehicle = e.getPlayer().getVehicle();
				vehicle.eject();
				vehicle.teleport(spawn);
			}

			e.setCancelled(true);
			e.getPlayer().teleport(spawn);

			if(vehicle != null)
				vehicle.setPassenger(e.getPlayer());
		}else if(e.getTo().getY() <= 0.0d && inGame()){
			BadblockPlayer player = (BadblockPlayer) e.getPlayer();
			if (player.getBadblockMode().equals(BadblockMode.PLAYER)) {
				@SuppressWarnings("deprecation")
				EntityDamageEvent event = new EntityDamageEvent(player, EntityDamageEvent.DamageCause.VOID, player.getHealth());
				player.setLastDamageCause(event);
				Bukkit.getServer().getPluginManager().callEvent(event);
			}
		}
		else if (inGame())
		{
			BadblockPlayer player = (BadblockPlayer) e.getPlayer();
			BadblockTeam team = player.getTeam();

			if (team == null)
			{
				return;
			}

			BedWarsTeamData tdo = team.teamData(BedWarsTeamData.class);

			if (tdo != null && tdo.getSpawnSelection() != null && tdo.getSpawnSelection().isInSelection(player))
			{
				if (tdo.heal > 0)
				{
					player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 5, tdo.heal - 1));
				}
			}

			for (BadblockTeam t : GameAPI.getAPI().getTeams())
			{
				if (t == null)
				{
					continue;
				}
				if (t.equals(team))
				{
					continue;
				}
				BedWarsTeamData td = t.teamData(BedWarsTeamData.class);
				if (td == null)
				{
					continue;
				}

				if (td.getSpawnSelection() != null && td.getSpawnSelection().isInSelection(e.getTo()))
				{
					if (td.trespassing)
					{
						player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 20, 3));
						player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 3));
						t.getOnlinePlayers().forEach(op -> op.playSound(Sound.ENDERMAN_DEATH));
						t.getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.trespassingchat", player.getName()));
						t.getOnlinePlayers().forEach(op -> op.sendTranslatedTitle("bedwars.trespassing", player.getName()));
						td.trespassing = false;
					}
					break;
				}
			}
		}
	}
}
