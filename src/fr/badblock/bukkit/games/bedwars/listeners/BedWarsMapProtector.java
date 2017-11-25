package fr.badblock.bukkit.games.bedwars.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.block.Action;

import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.bukkit.games.bedwars.entities.RushTeamData;
import fr.badblock.bukkit.games.bedwars.runnables.GameRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.configuration.values.MapMaterial;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.servers.MapProtector;

public class BedWarsMapProtector implements MapProtector {
	private boolean inGame(){
		return GameAPI.getAPI().getGameServer().getGameState() == GameState.RUNNING;
	}

	@Override
	public boolean blockPlace(BadblockPlayer player, Block block) {
		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean blockBreak(BadblockPlayer player, Block block) {
		if(!inGame()){
			return player.hasAdminMode();
		}
		
		if(block.getType() == Material.BED_BLOCK){
			BedListenerUtils.onBreakBed(player, block, true);
			return false;
		}

		boolean can = false;

		for(MapMaterial material : PluginBedWars.getInstance().getMapConfiguration().getBreakableBlocks()){
			if(material.getHandle() == block.getType()){
				can = true;

				break;
			}
		}

		return can || player.hasAdminMode();
	}

	@Override
	public boolean modifyItemFrame(BadblockPlayer player, Entity itemFrame) {
		return player.hasAdminMode();
	}

	@Override
	public boolean canLostFood(BadblockPlayer player) {
		return inGame();
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
				case CHEST: case TRAPPED_CHEST: case ENDER_CHEST: case FURNACE: case ENCHANTMENT_TABLE: case WORKBENCH:
					cancel = true;
					break;
				case BARRIER:
					cancel = true;
					player.sendTranslatedMessage("rush.youcantplaceablockthere");
					return false;
				default: break;
			}

			if(cancel)
				for(BadblockTeam team : GameAPI.getAPI().getTeams()){
					if(!team.equals(player.getTeam())){
						if(team.teamData(RushTeamData.class).getSpawnSelection().isInSelection(block)){
							return false;
						}
					}
				}
		}

		if(block != null && block.getType() == Material.BED_BLOCK && inGame() && action == Action.RIGHT_CLICK_BLOCK){
			BadblockTeam team = BedListenerUtils.parseBedTeam(block);

			if(team == null || !team.equals(player.getTeam())){
				player.sendTranslatedTitle("rush.noyourebed");
			} else {
				player.sendTranslatedTitle("rush.sleeping", player.getName());
			}

			return false;
		}

		if(block != null && block.getType() == Material.CHEST && inGame() && action == Action.RIGHT_CLICK_BLOCK){

			for(BadblockTeam team : GameAPI.getAPI().getTeams()){
				Location loc = team.teamData(RushTeamData.class).getRespawnLocation();

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
		return GameRunnable.damage;
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
		return false;
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
		return isPlugin;
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
		return false;
	}

	@Override
	public boolean canCombust(Entity entity) {
		return true;
	}

	@Override
	public boolean canEntityBeingDamaged(Entity entity) {
		return !inGame();
	}

	@Override
	public boolean destroyArrow() {
		return true;
	}
	
	@Override
	public boolean canEntityBeingDamaged(Entity entity, BadblockPlayer badblockPlayer) {
		return false;
	}

}
