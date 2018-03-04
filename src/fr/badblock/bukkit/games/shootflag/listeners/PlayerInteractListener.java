package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.games.shootflag.PluginShootFlag;
import fr.badblock.bukkit.games.shootflag.commands.GameCommand;
import fr.badblock.bukkit.games.shootflag.configuration.ShootFlagMapConfiguration;
import fr.badblock.bukkit.games.shootflag.flags.FlagCreator;
import fr.badblock.bukkit.games.shootflag.shooters.ShootUtils;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;

public class PlayerInteractListener extends BadListener
{

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		BadblockPlayer player = (BadblockPlayer) event.getPlayer();
		// Shoot
		Action action = event.getAction();
		if (action.equals(Action.RIGHT_CLICK_AIR) || action.equals(Action.RIGHT_CLICK_BLOCK))
		{
			if (player.getItemInHand().getType().equals(Material.STONE_HOE))
			{
				ShootUtils.shoot(player);
			}
		}
		// Setup
		if (GameCommand.setup != -1)
		{
			ItemStack itemStack = player.getItemInHand();
			
			if (!itemStack.getType().equals(Material.STICK))
			{
				return;
			}
			
			// Cancel
			event.setCancelled(true);
			
			// Configure spawn locations
			if (GameCommand.setup == 0)
			{
				ShootFlagMapConfiguration mapConfiguration = PluginShootFlag.getInstance().getMapConfiguration();
				mapConfiguration.getRespawnLocations().add(player.getLocation());
				mapConfiguration.save(GameCommand.generatedFile);
			}
			else if (GameCommand.setup == 1)
			{
				FlagCreator flagCreator = new FlagCreator(player.getLocation());
				if (flagCreator.setup())
				{
					player.sendMessage("§aDrapeau ajouté.");
				}
				else
				{
					player.sendMessage("§cVeuillez être le plus près possible d'un drapeau pour l'ajouter.");
				}
			}
		}
	}

}
