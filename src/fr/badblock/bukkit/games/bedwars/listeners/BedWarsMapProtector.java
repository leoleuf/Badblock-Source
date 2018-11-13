package fr.badblock.bukkit.games.bedwars.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
import fr.badblock.bukkit.games.bedwars.runnables.GameRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.servers.MapProtector;

public class BedWarsMapProtector implements MapProtector {

	public static List<Location> breakableBlocks = new ArrayList<>();
	public static Map<Location, BadblockTeam> glassBlocks = new HashMap<>();

	private boolean inGame(){
		return GameAPI.getAPI().getGameServer().getGameState() == GameState.RUNNING;
	}

	@Override
	public boolean blockPlace(BadblockPlayer player, Block block) {

		boolean can = inGame() || player.hasAdminMode();

		for (BadblockTeam t : GameAPI.getAPI().getTeams())
		{
			if (t == null)
			{
				continue;
			}
			
			BedWarsTeamData td = t.teamData(BedWarsTeamData.class);
			
			if (td == null)
			{
				continue;
			}
			
			if (td.getRespawnLocation() != null && td.getRespawnLocation().distance(block.getLocation()) >= 3)
			{
				continue;
			}
			
			player.sendTranslatedMessage("bedwars.cantplacethere");
			return false;
		}
		
		if (can && block.getType() != null && block.getType().equals(Material.TNT))
		{
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

				if (!item.getType().equals(Material.TNT))
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
			
			BedExplodeListener.placedTnts.put(block.getLocation(), player.getUniqueId());
			block.getLocation().getWorld().spawn(block.getLocation(), TNTPrimed.class);
			return false;
		}

		if (can)
		{
			breakableBlocks.add(block.getLocation());

			if (block.getType().equals(Material.GLASS))
			{
				glassBlocks.put(block.getLocation(), player.getTeam());
			}
		}

		return can;
	}

	@Override
	public boolean blockBreak(BadblockPlayer player, Block block) {
		if(!inGame()){
			return player.hasAdminMode();
		}

		boolean can = false;

		if (block.getType() != null && block.getType().name().toLowerCase().contains("bed")
				&& BedListenerUtils.onBreakBed(player, block, false))
		{
			can = true;
		}
		
		if (breakableBlocks.contains(block.getLocation()))
		{
			can = true;
			breakableBlocks.remove(block.getLocation());
		}

		if (block.getType() != null && block.getType().name().toLowerCase().contains("leaves"))
		{
			can = true;
		}
		
		return can || player.hasAdminMode();
	}

	@Override
	public boolean modifyItemFrame(BadblockPlayer player, Entity itemFrame) {
		return player.hasAdminMode();
	}

	@Override
	public boolean canLostFood(BadblockPlayer player) {
		return false;
	}

	@Override
	public boolean canUseBed(BadblockPlayer player, Block bed) {
		return false;
	}

	@Override
	public boolean canUsePortal(BadblockPlayer player) {
		return false;
	}

	@Override
	public boolean canDrop(BadblockPlayer player) {
		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean canPickup(BadblockPlayer player) {
		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean canFillBucket(BadblockPlayer player) {
		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean canEmptyBucket(BadblockPlayer player) {
		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean canInteract(BadblockPlayer player, Action action, Block block) {
		if(inGame() && action == Action.RIGHT_CLICK_BLOCK){
			boolean cancel = false;

			switch(block.getType()){
			case CHEST: case TRAPPED_CHEST: case FURNACE: case ENCHANTMENT_TABLE: case WORKBENCH:
				cancel = true;
				break;
			/*case BARRIER:
				cancel = true;
				player.sendTranslatedMessage("bedwars.youcantplaceablockthere");
				return false;*/
			default: break;
			}

			if(cancel)
				for(BadblockTeam team : GameAPI.getAPI().getTeams()){
					if(!team.equals(player.getTeam())){
						if(team.teamData(BedWarsTeamData.class).getSpawnSelection().isInSelection(block)){
							return false;
						}
					}
				}
		}
		
		if(block != null && block.getType() == Material.BED_BLOCK && inGame() && action == Action.RIGHT_CLICK_BLOCK){
			BadblockTeam team = BedListenerUtils.parseBedTeam(block);

			if(team == null || !team.equals(player.getTeam())){
				player.sendTranslatedTitle("bedwars.noyourebed");
			} else {
				player.sendTranslatedTitle("bedwars.sleeping", player.getName());
			}

			return false;
		}

		if(block != null && block.getType() == Material.CHEST && inGame() && action == Action.RIGHT_CLICK_BLOCK){

			for(BadblockTeam team : GameAPI.getAPI().getTeams()){
				Location loc = team.teamData(BedWarsTeamData.class).getRespawnLocation();

				if(loc.getWorld().equals(block.getWorld()) && loc.distance(block.getLocation()) < 10.0d){
					return team.equals(player.getTeam());
				}
			}

		}

		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean canInteractArmorStand(BadblockPlayer player, ArmorStand entity) {
		return false; // sait on jamais :o
	}

	@Override
	public boolean canInteractEntity(BadblockPlayer player, Entity entity) {
		return true; // � priori rien � bloquer ... :o
	}

	@Override
	public boolean canEnchant(BadblockPlayer player, Block table) {
		return false; // � prioris pas d'enchant � faire :3
	}

	@Override
	public boolean canBeingDamaged(BadblockPlayer player) {
		if (player.getActivePotionEffects() != null)
		{
			int count = 0;
			for (PotionEffect pe : player.getActivePotionEffects())
			{
				if (pe.getType().equals(PotionEffectType.INVISIBILITY))
				{
					count++;
				}
			}
			if (count > 0)
			{
				player.removePotionEffect(PotionEffectType.INVISIBILITY);
				player.playSound(Sound.ENDERMAN_HIT);
				player.sendTranslatedMessage("bedwars.invisibilitystop");
			}
		}
		return inGame() && GameRunnable.doneTime > 5;
	}

	@Override
	public boolean healOnJoin(BadblockPlayer player) {
		return !inGame();
	}

	@Override
	public boolean canBlockDamage(Block block) {
		return true;
	}

	@Override
	public boolean allowFire(Block block) {
		return true;
	}

	@Override
	public boolean allowMelting(Block block) {
		return false;
	}

	@Override
	public boolean allowBlockFormChange(Block block) {
		return true; //TODO test
	}

	@Override
	public boolean allowPistonMove(Block block) {
		return false;
	}

	@Override
	public boolean allowBlockPhysics(Block block) {
		return true;
	}

	@Override
	public boolean allowLeavesDecay(Block block) {
		return false;
	}

	@Override
	public boolean allowRaining() {
		return false;
	}

	@Override
	public boolean modifyItemFrame(Entity itemframe) {
		return false;
	}

	@Override
	public boolean canSoilChange(Block soil) {
		return false;
	}

	@Override
	public boolean canSpawn(Entity entity) {
		return true;
	}

	@Override
	public boolean canCreatureSpawn(Entity creature, boolean isPlugin) {
		if (creature != null && creature.getType().equals(EntityType.ARMOR_STAND))
		{
			return true;
		}
		return (creature.getType().equals(EntityType.IRON_GOLEM) || creature.getType().equals(EntityType.SILVERFISH)) && isPlugin;
	}

	@Override
	public boolean canItemSpawn(Item item) {
		return true;
	}

	@Override
	public boolean canItemDespawn(Item item) {
		return true;
	}

	@Override
	public boolean allowExplosion(Location location) {
		return inGame();
	}

	@Override
	public boolean allowInteract(Entity entity) {
		return inGame();
	}

	@Override
	public boolean canCombust(Entity entity) {
		return true;
	}

	@Override
	public boolean canEntityBeingDamaged(Entity entity) {
		return inGame();
	}

	@Override
	public boolean destroyArrow() {
		return true;
	}

	@Override
	public boolean canEntityBeingDamaged(Entity entity, BadblockPlayer badblockPlayer) {
		if (entity.getType().equals(EntityType.PLAYER))
		{
			BadblockPlayer target = (BadblockPlayer) entity;
			if (target.getTeam() != null && badblockPlayer.getTeam() != null)
			{
				if (target.getTeam().equals(badblockPlayer.getTeam()))
				{
					return false;
				}
			}
		}
		return entity.getType().equals(EntityType.SILVERFISH) || entity.getType().equals(EntityType.ARMOR_STAND) || entity.getType().equals(EntityType.PLAYER)
				|| entity.getType().equals(EntityType.IRON_GOLEM);
	}

}
