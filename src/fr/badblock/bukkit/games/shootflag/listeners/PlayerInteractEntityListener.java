package fr.badblock.bukkit.games.shootflag.listeners;

import org.bukkit.Location;
import org.bukkit.Rotation;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.badblock.bukkit.games.shootflag.flags.Flag;
import fr.badblock.bukkit.games.shootflag.players.ShootFlagData;
import fr.badblock.game.core18R3.players.GameTeam;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.players.BadblockPlayer;

public class PlayerInteractEntityListener extends BadListener
{

	@EventHandler
	public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event)
	{
		if (!ShootFlagMapProtector.inGame())
		{
			return;
		}

		BadblockPlayer player = (BadblockPlayer) event.getPlayer();

		// Steal a flag!
		if (event.getRightClicked() instanceof ItemFrame)
		{
			ItemFrame itemFrame = (ItemFrame) event.getRightClicked();
			Location location = event.getRightClicked().getLocation();
			Flag flag = null;

			// get flag
			for (Flag f : Flag.flags.values())
			{
				for (Location loc : f.getRealItemFrames())
				{
					if (locationEquals(location, loc))
					{
						flag = f;
						break;
					}
				}
			}

			System.out.println("FLAG: " + flag);

			if (flag == null)
			{
				event.setCancelled(true);
				return;
			}

			ShootFlagData playerData = player.inGameData(ShootFlagData.class);

			if (playerData.isItemFrameFlagged())
			{
				event.setCancelled(true);
				return;
			}

			GameTeam team = (GameTeam) player.getTeam();

			if (flag.getFlaggedBy() != null && flag.getFlaggedBy().equals(team))
			{
				player.sendTranslatedMessage("shootflag.alreadyflagged");
				event.setCancelled(true);
				return;
			}

			if (flag.getTeamFlagging() != null && !flag.getTeamFlagging().equals(team))
			{
				player.sendTranslatedMessage("shootflag.flaggingbyanotherteam");
				event.setCancelled(true);
				return;
			}

			if (flag.ownPart(team, player, itemFrame))
			{
				Rotation r = itemFrame.getRotation();

				System.out.println(r.name());

				int percent = flag.getPercent();
				String color = percent < 40 ? "§c" : percent < 60 ? "§e" : percent < 80 ? "§9" : "§a";

				ItemStack itemStack = itemFrame.getItem();
				ItemMeta itemMeta = itemStack.getItemMeta();
				String d = percent <= 33 ? "%§7.." : percent <= 66 ? "%.§7." : "%..";
				itemMeta.setDisplayName("§6Crochetage : " + color + percent + " " + d);
				itemStack.setItemMeta(itemMeta);
				itemFrame.setItem(itemStack);

				// Rotation
				for (Entity entity : player.getNearbyEntities(10, 10, 10))
				{
					if (entity instanceof ItemFrame)
					{
						for (Location loc : flag.getRealItemFrames())
						{
							if (locationEquals(loc, entity.getLocation()))
							{
								if (entity.getUniqueId().equals(itemFrame.getUniqueId()))
								{
									continue;
								}
								ItemFrame frame = (ItemFrame) entity;
								frame.setFacingDirection(itemFrame.getFacing());
								frame.setRotation(itemFrame.getRotation());
								frame.setItem(itemFrame.getItem());
							}
						}
					}
				}

				// Sound
				player.playSound(player.getLocation().clone(), Sound.NOTE_PIANO, 10F, 1F);

			}

			// Flag
			playerData.itemFrameFlag();
		}
	}

	private boolean locationEquals(Location loc1, Location loc2)
	{
		return loc1.getWorld().getName().equals(loc2.getWorld().getName())
				&& loc1.getBlockX() == loc2.getBlockX()
				&& loc1.getBlockY() == loc2.getBlockY()
				&& loc1.getBlockZ() == loc2.getBlockZ();
	}

}
