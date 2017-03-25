package fr.badblock.bukkit.games.spaceballs.rockets;

import java.util.Arrays;
import java.util.List;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.i18n.Locale;
import fr.badblock.gameapi.utils.i18n.TranslatableString;
import fr.badblock.gameapi.utils.itemstack.ItemStackUtils;

public interface Rocket {
	public int getRange();
	
	public String getName();
	
	public void impact(BadblockPlayer launcher, Block block);
	
	public void impact(BadblockPlayer launcher, Entity entity);
	
	public default Class<? extends Projectile> getProjectileClass(){
		return Snowball.class;
	}
	
	public default void customize(Projectile projectile){
		new RocketRectileDirection(projectile, getRange()).runTaskTimer(GameAPI.getAPI(), 1L, 1L);
	}
	
	public default BadblockPlayer getNearbiestPlayer(Location location, double max, BadblockPlayer launcher){
		BadblockPlayer finded = null;
		
		for(BadblockPlayer player : location.getWorld().getEntitiesByClass(BadblockPlayer.class)){
			double distance = player.getLocation().distance(location);
			
			if(distance <= max && player.getGameMode() != GameMode.SPECTATOR && !player.equals(launcher)){
				finded = player;
				max = distance;
			}
		}
		
		return finded;
	}
	
	public default void messageNoTarget(BadblockPlayer player){
		player.sendTranslatedTitle("spaceballs.rockets.notarget");
	}
	
	public default String getDisplayName(Locale locale){
		return new TranslatableString("spaceballs.rockets." + getName() + ".displayname").getAsLine(locale);
	}
	
	public default List<String> getLore(Locale locale){
		return Arrays.asList( new TranslatableString("spaceballs.rockets." + getName() + ".lore", getRange()).get(locale) );
	}
	
	public default void applyI18N(ItemStack is, Locale locale, int id){
		ItemMeta meta 	 = is.getItemMeta();
		
		meta.setDisplayName( ItemStackUtils.encodeIDInName(getDisplayName(locale), id));
		meta.setLore(getLore(locale));
		
		is.setItemMeta(meta);
	}
}
