package fr.badblock.bukkit.games.uhc.meetup.listeners;

import java.util.List;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import fr.badblock.bukkit.games.uhc.meetup.PluginUHC;
import fr.badblock.bukkit.games.uhc.meetup.configuration.UHCConfiguration.MapCustomMaterial;
import fr.badblock.bukkit.games.uhc.meetup.runnables.game.GameRunnable;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.game.GameState;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.servers.MapProtector;

public class UHCMapProtector implements MapProtector {
	private boolean inGame(){
		return GameAPI.getAPI().getGameServer().getGameState() == GameState.RUNNING;
	}

	@Override
	public boolean blockPlace(BadblockPlayer player, Block block) {
		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean blockBreak(BadblockPlayer player, Block block) {
		if (inGame())
		{
			if (block.getType().equals(Material.DIAMOND_ORE) || block.getType().equals(Material.IRON_ORE)
					|| block.getType().equals(Material.GOLD_ORE) || block.getType().equals(Material.EMERALD_ORE))
			{
				ExperienceOrb orb = block.getWorld().spawn(block.getLocation().add(0.5, 0.5, 0.5), ExperienceOrb.class);
				orb.setExperience(block.getType().equals(Material.DIAMOND_ORE) ? 16 : 3); 
			}
			
			if (PluginUHC.getInstance().getConfiguration().randomBreaks.containsKey(block.getType().name())) {

				List<MapCustomMaterial> materials = PluginUHC.getInstance().getConfiguration().randomBreaks.get(block.getType().name());
				int randomTotal = materials.stream().mapToInt(i -> i.getProbability()).sum();
				int random = new Random().nextInt(randomTotal);

				int t = 0;
				for (MapCustomMaterial entry : materials)
				{
					t += entry.getProbability();

					if (random < t)
					{
						Material material = getFrom(entry.getName());
						if (material == null || material.equals(Material.AIR))
						{
							return true;
						}
						else
						{
							block.setType(Material.AIR);
							block.getWorld().dropItemNaturally(block.getLocation(), new ItemStack(material, entry.getAmount(), (byte) entry.getData()));
							break;
						}
					}
				}

				return false;
			}

			return true;
		}

		return player.hasAdminMode();
	}

	public static Material getFrom(String raw)
	{
		for (Material material : Material.values())
		{
			if (material.name().equalsIgnoreCase(raw))
			{
				return material;
			}
		}

		return Material.AIR;
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
		if(player.getItemInHand() != null && !GameRunnable.pve){
			switch(player.getItemInHand().getType()){
			case LAVA_BUCKET:
			case FLINT_AND_STEEL:
				return false;
			default: ;
			}
		}

		return inGame() || player.hasAdminMode();
	}

	@Override
	public boolean canInteractArmorStand(BadblockPlayer player, ArmorStand entity) {
		return inGame(); // sait on jamais :o
	}

	@Override
	public boolean canInteractEntity(BadblockPlayer player, Entity entity) {
		return true; // � priori rien � bloquer ... :o
	}

	@Override
	public boolean canEnchant(BadblockPlayer player, Block table) {
		return true;
	}

	@Override
	public boolean canBeingDamaged(BadblockPlayer player) {
		return inGame() && GameRunnable.pve;
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
		return inGame();
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
		return true;
	}

	@Override
	public boolean allowBlockPhysics(Block block) {
		return true;
	}

	@Override
	public boolean allowLeavesDecay(Block block) {
		return inGame();
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
		return true;
	}

	@Override
	public boolean canSpawn(Entity entity) {
		return true;
	}

	@Override
	public boolean canCreatureSpawn(Entity creature, boolean isPlugin) {
		return true;
	}

	@Override
	public boolean canItemSpawn(Item item) {
		return inGame();
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
		return true;
	}

	@Override
	public boolean canCombust(Entity entity) {
		return true;
	}

	@Override
	public boolean canEntityBeingDamaged(Entity entity) {
		if(entity.getType() != EntityType.PLAYER)
			return inGame();

		return inGame() && GameRunnable.pve;
	}

	@Override
	public boolean destroyArrow() {
		return true;
	}

	@Override
	public boolean canEntityBeingDamaged(Entity entity, BadblockPlayer badblockPlayer) {
		if(entity.getType() == EntityType.PLAYER)
			return inGame() && GameRunnable.pve;

		return inGame();
	}

}
