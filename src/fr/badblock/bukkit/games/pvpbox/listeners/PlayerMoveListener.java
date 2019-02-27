package fr.badblock.bukkit.games.pvpbox.listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;

import fr.badblock.bukkit.games.pvpbox.PvPBox;
import fr.badblock.bukkit.games.pvpbox.config.BoxConfig;
import fr.badblock.bukkit.games.pvpbox.config.BoxCuboid;
import fr.badblock.bukkit.games.pvpbox.players.BoxPlayer;
import fr.badblock.bukkit.games.pvpbox.totems.Totem;
import fr.badblock.bukkit.games.pvpbox.totems.TotemManager;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.selections.CuboidSelection;

public class PlayerMoveListener extends BadListener
{

	private PvPBox					box = PvPBox.getInstance();
	private BoxConfig			boxConfig = box.getBoxConfig();

	private BoxCuboid			mainArena = boxConfig.getArenaCuboid();	
	private CuboidSelection	mainArenaCuboid = mainArena.getCuboidSelection();
	
	private TotemManager		totemManager = TotemManager.getInstance();
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event)
	{
		BadblockPlayer player = (BadblockPlayer) event.getPlayer();
		BoxPlayer boxPlayer = BoxPlayer.get(player);
		
		Location from = event.getFrom();
		Location to = event.getTo();
		
		boolean move = from.getBlockX() != to.getBlockX() || from.getBlockY() != to.getBlockY() || from.getBlockZ() != to.getBlockZ();
		
		if (move && boxPlayer.getLastSpawn() != 0)
		{
			boxPlayer.setLastSpawn(0);
			player.sendTranslatedMessage("pvpbox.spawnteleportcancelled");
		}
		
		if (mainArenaCuboid.isInSelection(to) && !mainArenaCuboid.isInSelection(from))
		{
			boxPlayer.arenaJoin();
		}
		
		totemWork(player, to);
	}
	
	private void totemWork(BadblockPlayer player, Location to)
	{
		if (!mainArenaCuboid.isInSelection(player))
		{
			return;
		}
		
		Totem totem = totemManager.getTotem(player);
		
		if (totem == null)
		{
			return;
		}
		
		if (player.hasPotionEffect(totem.getPotionEffectType()))
		{
			return;
		}
		
		PotionEffect potionEffect = new PotionEffect(totem.getPotionEffectType(), totem.getTicks(), totem.getLevel());
		player.addPotionEffect(potionEffect);
	}
	
}
