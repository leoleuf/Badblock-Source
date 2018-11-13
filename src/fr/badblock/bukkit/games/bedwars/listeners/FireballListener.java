package fr.badblock.bukkit.games.bedwars.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;

public class FireballListener extends BadListener {

	@EventHandler
	public void onUnloadChunk(ChunkUnloadEvent event)
	{
		event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerInteractt(PlayerInteractEvent event)
	{
		if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
		{
			return;
		}

		Player player = event.getPlayer();
		if (player.getItemInHand() == null || player.getItemInHand().getType() == null || !player.getItemInHand().getType().equals(Material.FIREBALL))
		{
			return;
		}

		event.setCancelled(true);

		int amount = 1;

		for (int i = 0; i < player.getInventory().getContents().length; i++)
		{
			ItemStack item = player.getInventory().getContents()[i];
			if (amount <= 0)
			{
				break;
			}

			if (item == null || item.getType() == null)
			{
				continue;
			}

			if (!item.getType().equals(Material.FIREBALL))
			{
				continue;
			}

			int a = item.getAmount();

			if (a > amount && amount > 0)
			{
				a -= amount;
				amount = 0;
				item.setAmount(a);
				player.getInventory().setItem(i, item);
			}
			else if (amount > 0)
			{
				amount -= a;
				player.getInventory().setItem(i, new ItemStack(Material.AIR, 1));
			}
			else
			{
				break;
			}
		}

		player.updateInventory();

		Fireball fireball = player.launchProjectile(Fireball.class);
		ThrowEggListener.fireballs.put(fireball, (BadblockPlayer) player);
	}

}