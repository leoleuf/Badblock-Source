package fr.badblock.bukkit.games.bedwars.listeners;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
import fr.badblock.bukkit.games.bedwars.entities.Pathway;
import fr.badblock.bukkit.games.bedwars.entities.PathwayHandler;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;

public class ThrowEggListener extends BadListener {

	Map<Egg, BadblockPlayer> silverfishs = new HashMap<Egg, BadblockPlayer>();
	Map<Egg, BadblockPlayer> golems = new HashMap<Egg, BadblockPlayer>();
	static Map<Fireball, BadblockPlayer> fireballs = new HashMap<Fireball, BadblockPlayer>();

	@EventHandler
	public void onHit(ProjectileHitEvent event)
	{
		if ((event.getEntity() instanceof Egg))
		{
			Egg egg = (Egg)event.getEntity();
			if (PathwayHandler.pathways.containsKey(egg))
			{
				PathwayHandler.pathways.remove(egg);
				return;
			}
			else if (silverfishs.containsKey(egg))
			{
				BadblockPlayer player = silverfishs.get(egg);

				if (player.getTeam() == null)
				{
					return;
				}

				Entity entity = egg.getWorld().spawnEntity(egg.getLocation(), EntityType.SILVERFISH);

				if (entity == null)
				{
					return;
				}

				Silverfish silverfish = (Silverfish) entity;
				SilverfishListener.silverfishs.put(silverfish, player.getTeam());

				silverfishs.remove(egg);
			}
			else if (golems.containsKey(egg))
			{
				BadblockPlayer player = golems.get(egg);

				if (player.getTeam() == null)
				{
					return;
				}

				/*net.minecraft.server.v1_8_R3.World mcWorld = ((org.bukkit.craftbukkit.v1_8_R3.CraftWorld) egg.getWorld()).getHandle();
				AngryIronGolem golem = new AngryIronGolem(mcWorld);
				golem.spawnIn(mcWorld);
				golem.setPosition(egg.getLocation().getX(), egg.getLocation().getY(), egg.getLocation().getZ());
				mcWorld.addEntity(golem, SpawnReason.CUSTOM);*/

				Entity entity = egg.getWorld().spawnEntity(egg.getLocation(), EntityType.IRON_GOLEM);

				if (entity == null)
				{
					return;
				}

				GolemListener.golems.put((IronGolem) entity, player.getTeam());

				golems.remove(egg);
			}
			return;
		}
		else if (event.getEntityType().equals(EntityType.FIREBALL))
		{
			if (fireballs.containsKey(event.getEntity()))
			{
				BadblockPlayer po = fireballs.get(event.getEntity());
				BadblockTeam t = po.getTeam();
				if (t == null)
				{
					return;
				}

				BedWarsTeamData td = t.teamData(BedWarsTeamData.class);
				if (td == null)
				{
					return;
				}

				for (int x = -5; x <= 5; x++) {
					for (int y = -5; y <= 5; y++) {
						for (int z = -5; z <= 5; z++) {
							Block block = event.getEntity().getLocation().getBlock().getRelative(x, y, z);
							
							if (BedWarsMapProtector.breakableBlocks.contains(block.getLocation()) && (block.getType().equals(Material.WOOL) || block.getType().equals(Material.WOOD)))
							{
								if (td.getSpawnSelection() != null && td.getSpawnSelection().isInSelection(block))
								{
									continue;
								}
								block.setType(Material.AIR);
							}
						}
					}
				}
				event.getEntity().getLocation().getWorld().playEffect(event.getEntity().getLocation(), Effect.EXPLOSION_HUGE, 1);
				
				fireballs.remove(event.getEntity());
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onLaunch(ProjectileLaunchEvent event)
	{
		if ((event.getEntity() instanceof Egg))
		{
			Egg egg = (Egg)event.getEntity();
			if ((egg.getShooter() instanceof Player))
			{
				Player player = (Player)egg.getShooter();
				ItemStack itemStack  = player.getItemInHand();
				if (itemStack != null && itemStack.getItemMeta() != null)
				{
					ItemMeta itemMeta = itemStack.getItemMeta();
					if (itemMeta.getDisplayName() != null && itemMeta.getDisplayName().equalsIgnoreCase("Auto-Bridge"))
					{
						BadblockPlayer badblockPlayer = (BadblockPlayer) player;
						BadblockTeam team = badblockPlayer.getTeam();
						PathwayHandler.pathways.put(egg, new Pathway(Material.WOOL, team.getDyeColor().getWoolData()));
					}
					else if (itemMeta.getDisplayName() != null && itemMeta.getDisplayName().equalsIgnoreCase("Silverfish"))
					{
						silverfishs.put(egg, (BadblockPlayer) player);
					}
					else if (itemMeta.getDisplayName() != null && itemMeta.getDisplayName().equalsIgnoreCase("Golem"))
					{
						golems.put(egg, (BadblockPlayer) player);
					}
				}
				return;
			}
			return;
		}
	}

}