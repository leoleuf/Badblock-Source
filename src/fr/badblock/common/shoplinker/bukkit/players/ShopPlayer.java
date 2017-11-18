package fr.badblock.common.shoplinker.bukkit.players;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import fr.badblock.common.shoplinker.api.objects.TempBuyObject;
import fr.badblock.common.shoplinker.bukkit.inventories.objects.TempInventoryObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class ShopPlayer
{

	private static Map<Player, ShopPlayer> players	= new HashMap<>();
	
	private Player					player;
	private TempBuyObject			buy;
	private TempInventoryObject		lastInventory;
	
	public ShopPlayer(Player player)
	{
		setPlayer(player);
		put();
	}
	
	private void put()
	{
		players.put(getPlayer(), this);
	}
	
	public void remove()
	{
		players.remove(getPlayer());
	}
	
	public static ShopPlayer get(Player player)
	{
		return !players.containsKey(player) ? new ShopPlayer(player) : players.get(player);
	}
	
}
