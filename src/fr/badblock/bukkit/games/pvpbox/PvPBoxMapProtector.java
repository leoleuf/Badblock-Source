package fr.badblock.bukkit.games.pvpbox;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.block.Action;

import fr.badblock.bukkit.games.pvpbox.config.BoxConfig;
import fr.badblock.bukkit.games.pvpbox.config.BoxCuboid;
import fr.badblock.bukkit.games.pvpbox.players.BoxPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.servers.MapProtector;

public class PvPBoxMapProtector implements MapProtector {

	@Override
	public boolean blockPlace(BadblockPlayer player, Block block)
	{
		return player.hasAdminMode();
	}

	@Override
	public boolean blockBreak(BadblockPlayer player, Block block)
	{
		return player.hasAdminMode();
	}

	@Override
	public boolean modifyItemFrame(BadblockPlayer player, Entity itemFrame)
	{
		return player.hasAdminMode();
	}

	@Override
	public boolean canLostFood(BadblockPlayer player)
	{
		return false;
	}

	@Override
	public boolean canUseBed(BadblockPlayer player, Block bed)
	{
		return false;
	}

	@Override
	public boolean canUsePortal(BadblockPlayer player)
	{
		return false;
	}

	@Override
	public boolean canDrop(BadblockPlayer player)
	{
		boolean can = false; // TODO
		
		
		
		return can || player.hasAdminMode();
	}

	@Override
	public boolean canPickup(BadblockPlayer player)
	{
		boolean can = false; // TODO
		
		
		
		return can || player.hasAdminMode();
	}

	@Override
	public boolean canFillBucket(BadblockPlayer player)
	{
		return player.hasAdminMode();
	}

	@Override
	public boolean canEmptyBucket(BadblockPlayer player)
	{
		return player.hasAdminMode();
	}

	@Override
	public boolean canInteract(BadblockPlayer player, Action action, Block block)
	{
		// TODO
		return !action.equals(Action.PHYSICAL) && arenaCheck(player);
	}

	@Override
	public boolean canInteractArmorStand(BadblockPlayer player, ArmorStand entity)
	{
		return false;
	}

	@Override
	public boolean canInteractEntity(BadblockPlayer player, Entity entity)
	{
		return true;
	}

	@Override
	public boolean canEnchant(BadblockPlayer player, Block table)
	{
		return false;
	}

	@Override
	public boolean canBeingDamaged(BadblockPlayer player)
	{
		boolean can = true;
		
		if (!arenaCheck(player))
		{
			return false;
		}

		// TODO
		
		return can;
	}

	@Override
	public boolean healOnJoin(BadblockPlayer player)
	{
		return true;
	}

	@Override
	public boolean canBlockDamage(Block block)
	{
		return false;
	}

	@Override
	public boolean allowFire(Block block)
	{
		return false;
	}

	@Override
	public boolean allowMelting(Block block)
	{
		return false;
	}

	@Override
	public boolean allowBlockFormChange(Block block)
	{
		return false;
	}

	@Override
	public boolean allowPistonMove(Block block)
	{
		return false;
	}

	@Override
	public boolean allowBlockPhysics(Block block)
	{
		return false;
	}

	@Override
	public boolean allowLeavesDecay(Block block)
	{
		return false;
	}

	@Override
	public boolean allowRaining()
	{
		return false;
	}

	@Override
	public boolean modifyItemFrame(Entity itemframe)
	{
		return false;
	}

	@Override
	public boolean canSoilChange(Block soil)
	{
		return false;
	}

	@Override
	public boolean canSpawn(Entity entity)
	{
		return entity.getType().equals(EntityType.PLAYER) || entity.getType().equals(EntityType.ARROW);
	}

	@Override
	public boolean canCreatureSpawn(Entity creature, boolean isPlugin)
	{
		return isPlugin;
	}

	@Override
	public boolean canItemSpawn(Item item)
	{
		return true;
	}

	@Override
	public boolean canItemDespawn(Item item)
	{
		return true;
	}

	@Override
	public boolean allowExplosion(Location location)
	{
		return false;
	}

	@Override
	public boolean allowInteract(Entity entity)
	{
		return entity.getType().equals(EntityType.PLAYER);
	}

	@Override
	public boolean canCombust(Entity entity)
	{
		return false;
	}

	@Override
	public boolean canEntityBeingDamaged(Entity entity)
	{
		return arenaCheck(entity);
	}

	@Override
	public boolean destroyArrow() {
		return false;
	}

	@Override
	public boolean canEntityBeingDamaged(Entity entity, BadblockPlayer damagerPlayer)
	{
		if (!entity.getType().equals(EntityType.PLAYER))
		{
			return false;
		}
		
		return damageCheck(damagerPlayer, (BadblockPlayer) entity);
	}
	
	@SuppressWarnings("deprecation")
	public static boolean damageCheck(BadblockPlayer killer, BadblockPlayer killed)
	{
		if (!arenaCheck(killer) || !arenaCheck(killed))
		{
			return false;
		}
		
		BadblockPlayer damagedPlayer = (BadblockPlayer) killed;
		
		BoxPlayer damaged = BoxPlayer.get(damagedPlayer);
		BoxPlayer damager = BoxPlayer.get(killer);
		
		if (damaged == null || damager == null)
		{
			return false;
		}
		
		// check teleport time
		long time = System.currentTimeMillis();

		long damagerJoinDiff = time - damager.getArenaJoinTimestamp();
		long damagedJoinDiff = time - damaged.getArenaJoinTimestamp();
		
		PvPBox box = PvPBox.getInstance();
		BoxConfig config = box.getBoxConfig();

		if (damagerJoinDiff < config.getAntiSpawnKillTime())
		{
			killer.sendTranslatedMessage("pvpbox.youjustjoined");
			return false;
		}
		
		if (damagedJoinDiff < config.getAntiSpawnKillTime())
		{
			killer.sendTranslatedMessage("pvpbox.antispawnkill");
			return false;
		}
		
		if (!killer.isVisible() || !damagedPlayer.canSee(killer))
		{
			killer.sendTranslatedMessage("pvpbox.youarevanished");
			return false;
		}
		
		if (!damagedPlayer.isVisible() || !killer.canSee(damagedPlayer))
		{
			return false;
		}

		damaged.setLastHit(System.currentTimeMillis());
		damager.getLastAttacks().put(damagedPlayer.getName().toLowerCase(), System.currentTimeMillis());
		
		return true;
	}
	
	public static boolean arenaCheck(Entity entity)
	{
		if (!entity.getType().equals(EntityType.PLAYER))
		{
			return false;
		}
		
		BadblockPlayer player = (BadblockPlayer) entity;
		PvPBox pvpBox = PvPBox.getInstance();
		BoxConfig config = pvpBox.getBoxConfig();
				
		BoxCuboid arenaCuboid = config.getArenaCuboid();
		
		return arenaCuboid.getCuboidSelection().isInSelection(player);
	}

}
