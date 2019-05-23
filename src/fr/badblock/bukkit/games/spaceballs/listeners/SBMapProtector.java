package fr.badblock.bukkit.games.spaceballs.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.Action;

import fr.badblock.bukkit.games.spaceballs.PluginSB;
import fr.badblock.bukkit.games.spaceballs.entities.SpaceTeamData;
import fr.badblock.bukkit.games.spaceballs.players.SpaceData;
import fr.badblock.bukkit.games.spaceballs.rockets.Rockets;
import fr.badblock.bukkit.games.spaceballs.runnables.GameRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.configuration.values.MapMaterial;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.servers.MapProtector;
import fr.badblock.gameapi.utils.i18n.Locale;
import fr.badblock.gameapi.utils.itemstack.ItemStackUtils;

public class SBMapProtector implements MapProtector {
	private boolean inGame(){
		return GameAPI.getAPI().getGameServer().getGameState() == GameState.RUNNING;
	}

	@Override
	public boolean blockPlace(BadblockPlayer player, Block block) {
		if(inGame()){
			if(PluginSB.getInstance().getMapConfiguration().getTowerBounds().isInSelection(block)){
				return player.hasAdminMode();
			}

			for(BadblockTeam team : GameAPI.getAPI().getTeams()){
				if(team.teamData(SpaceTeamData.class).getSpawnSelection().isInSelection(block)){
					return player.hasAdminMode();
				}
			}
			
			if(block.getType() == Material.SANDSTONE){
				if(ItemStackUtils.isValid(player.getItemInHand()) && player.getItemInHand().getType() == Material.SANDSTONE){
					player.getItemInHand().setAmount(16);
				}
			}
		}

		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean blockBreak(BadblockPlayer player, Block block) {
		if(!inGame()){
			return player.hasAdminMode();
		}

		boolean can = true;

		for(MapMaterial material : PluginSB.getInstance().getMapConfiguration().getUnbreakableBlocks()){
			if(material.getHandle() == block.getType()){
				can = false;

				break;
			}
		}

		if(PluginSB.getInstance().getMapConfiguration().getTowerBounds().isInSelection(block)){
			return player.hasAdminMode();
		}

		for(BadblockTeam team : GameAPI.getAPI().getTeams()){
			if(team.teamData(SpaceTeamData.class).getSpawnSelection().isInSelection(block)){
				return player.hasAdminMode();
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
			for(BadblockTeam team : GameAPI.getAPI().getTeams()){
				if(!team.equals(player.getTeam())){
					if(team.teamData(SpaceTeamData.class).getSpawnSelection().isInSelection(block)){
						return false;
					}
				}
			}

			if(ItemStackUtils.hasDisplayname(player.getItemInHand()) && player.getItemInHand().getType() == Material.FIREWORK)
				return false;
		}

		if(inGame() && action == Action.RIGHT_CLICK_AIR && ItemStackUtils.hasDisplayname(player.getItemInHand()) && player.getItemInHand().getType() == Material.FIREWORK){
			Rockets type = Rockets.matchRocket(player.getItemInHand());

			if(type != null){
				ItemStackUtils.removeInHand(player, 1);
				player.inGameData(SpaceData.class).launchRocket(player);

				GameAPI.getAPI().getOnlinePlayers().forEach(p -> p.playSound(player.getPlayer().getLocation(), Sound.IRONGOLEM_HIT));
				
				Projectile p = player.launchProjectile(type.getRocketHandler().getProjectileClass(), (hitBlock, hitEntity) -> {
					if(!player.isOnline())
						return;

					if(hitBlock != null) {
						type.getRocketHandler().impact(player, hitBlock);
					} else if(hitEntity != null) {
						type.getRocketHandler().impact(player, hitEntity);
					}
				});
				
				type.getRocketHandler().customize(p);
			}
		}

		if(block != null && block.getType() == Material.CHEST && inGame() && player.getTeam() != null && action == Action.RIGHT_CLICK_BLOCK){
			player.getTeam().teamData(SpaceTeamData.class).putDiamond(player, block);
			return false;
		}

		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean canInteractArmorStand(BadblockPlayer player, ArmorStand entity) {
		return false; // sait on jamais :o
	}

	@Override
	public boolean canInteractEntity(BadblockPlayer player, Entity entity) {
		return true; // à priori rien à bloquer ... :o
	}

	@Override
	public boolean canEnchant(BadblockPlayer player, Block table) {
		return false; // à prioris pas d'enchant à faire :3
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
		if(inGame()){
			if(PluginSB.getInstance().getMapConfiguration().getTowerBounds().isInSelection(block)){
				return false;
			}


			for(BadblockTeam team : GameAPI.getAPI().getTeams()){
				if(team.teamData(SpaceTeamData.class).getSpawnSelection().isInSelection(block)){
					return false;
				}
			}
		}

		return true;
	}

	@Override
	public boolean allowPistonMove(Block block) {
		return false;
	}

	@Override
	public boolean allowBlockPhysics(Block block) {
		return false;
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
		if(item.getItemStack().getType() == Material.QUARTZ){
			item.setItemStack(Rockets.createRandomRocket());
		} else if(item.getItemStack().getType() == Material.FIREWORK){
			Rockets.changeLanguage(item.getItemStack(), Locale.FRENCH_FRANCE);
		} else if(item.getItemStack().getType() == Material.SANDSTONE)
			return false;

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
		return true;
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
