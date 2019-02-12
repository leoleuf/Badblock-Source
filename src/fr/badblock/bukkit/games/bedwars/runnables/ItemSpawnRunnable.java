package fr.badblock.bukkit.games.bedwars.runnables;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.configuration.values.MapLocation;
import fr.badblock.gameapi.players.BadblockTeam;

public class ItemSpawnRunnable extends BukkitRunnable {
	private final Material material;
	private final long ticks;

	private Map<Location, ArmorStand>	armorStands1;
	private Map<Location, ArmorStand>	armorStands2;
	private Map<Location, ArmorStand>	armorStands3;

	private List<Item> items  = new ArrayList<>();

	public ItemSpawnRunnable(Material material, long ticks)
	{
		this.material = material;
		this.ticks = ticks;

		if (material.equals(Material.DIAMOND) || material.equals(Material.EMERALD))
		{
			PluginBedWars instance = PluginBedWars.getInstance();
			List<MapLocation> locations = material.equals(Material.DIAMOND) ? instance.getMapConfiguration().getSpawnDiamonds() :
				instance.getMapConfiguration().getSpawnEmeralds();

			armorStands1 = new HashMap<>();
			armorStands2 = new HashMap<>();
			armorStands3 = new HashMap<>();

			String key = material.equals(Material.DIAMOND) ? "bedwars.floatingdiamondtier" : "bedwars.floatingemeraldtier";
			int tier = material.equals(Material.DIAMOND) ? TierRunnable.diamondTier : TierRunnable.emeraldTier;
			String tierShown = tier == 1 ? "I" : tier == 2 ? "II" : tier == 3 ? "III" : " ";

			for(MapLocation location : locations)
			{
				Location loc = location.getHandle().clone();
				loc = loc.add(0, 1.5, 0);
				ArmorStand as = GameRunnable.spawnNametag(loc, GameAPI.i18n().get(key, tierShown)[0]);
				armorStands1.put(location.getHandle(), as);
			}

			key = material.equals(Material.DIAMOND) ? "bedwars.floatingdiamond" : "bedwars.floatingemerald";

			for(MapLocation location : locations)
			{
				Location loc = location.getHandle().clone();
				loc = loc.add(0, 0.75, 0);
				ArmorStand as = GameRunnable.spawnNametag(loc, GameAPI.i18n().get(key)[0]);
				armorStands2.put(location.getHandle(), as);
			}

			key = material.equals(Material.DIAMOND) ? "bedwars.floatingdiamondtime" : "bedwars.floatingemeraldtime";

			for(MapLocation location : locations)
			{
				Location loc = location.getHandle();
				ArmorStand as = GameRunnable.spawnNametag(loc, GameAPI.i18n().get(key, (int) (ticks / 20))[0]);
				armorStands3.put(location.getHandle(), as);
			}

			Bukkit.getScheduler().runTaskTimer(instance, new Runnable()
			{

				long t = ticks / 20;
				@Override
				public void run() {
					t--;
					if (t <= 0)
					{
						t = ticks / 20;
					}

					int tier = material.equals(Material.DIAMOND) ? TierRunnable.diamondTier : TierRunnable.emeraldTier;
					String tierShown = tier == 1 ? "I" : tier == 2 ? "II" : tier == 3 ? "III" : " ";

					String key = material.equals(Material.DIAMOND) ? "bedwars.floatingdiamondtier" : "bedwars.floatingemeraldtier";
					for(ArmorStand as : armorStands1.values())
					{
						as.setCustomName(GameAPI.i18n().get(key, tierShown)[0]);
					}

					key = material.equals(Material.DIAMOND) ? "bedwars.floatingdiamondtime" : "bedwars.floatingemeraldtime";

					for(ArmorStand as : armorStands3.values())
					{
						as.setCustomName(GameAPI.i18n().get(key, t)[0]);
					}
				}

			}, 20, 20);
		}


	}

	@Override
	public void run(){
		if (material.equals(Material.DIAMOND))
		{
			if (items.size() >= 15)
			{
				int o = items.size() - 15;
				Iterator<Item> is = items.iterator();
				while (is.hasNext())
				{
					Item item = is.next();
					if (o <= 0)
					{
						continue;
					}
					o--;
					item.remove();
					is.remove();
				}
			}
			for (int i = 0; i < TierRunnable.diamondTier; i++)
			{
				for(MapLocation location : PluginBedWars.getInstance().getMapConfiguration().getSpawnDiamonds())
				{
					Item item = location.getHandle().getWorld().dropItem(location.getHandle(), new ItemStack(material, 1));
					item.setVelocity(new Vector(0, 0, 0));

					items.add(item);
				}
			}
			for(Item item : items){
				if(!item.isDead()) item.remove();
			}
		}
		else if (material.equals(Material.EMERALD))
		{
			if (items.size() >= 15)
			{
				int o = items.size() - 15;
				Iterator<Item> is = items.iterator();
				while (is.hasNext())
				{
					Item item = is.next();
					if (o <= 0)
					{
						continue;
					}
					o--;
					item.remove();
					is.remove();
				}
				items.clear();
				for(BadblockTeam team : GameAPI.getAPI().getTeams()){
					for(Location location : team.teamData(BedWarsTeamData.class).getItemSpawnLocations()){
						Item item = location.getWorld().dropItem(location, new ItemStack(material, 1));
						item.setVelocity(new Vector(0, 0, 0));
						items.add(item);
					}
					for (int i = 0; i < TierRunnable.emeraldTier; i++)
					{
						for(MapLocation location : PluginBedWars.getInstance().getMapConfiguration().getSpawnEmeralds())
						{
							Item item = location.getHandle().getWorld().dropItem(location.getHandle(), new ItemStack(material, 1));
							item.setVelocity(new Vector(0, 0, 0));

							items.add(item);
						}
					}
				}
			}
			else
			{
				for (BadblockTeam team : GameAPI.getAPI().getTeams())
				{
					BedWarsTeamData teamData = team.teamData(BedWarsTeamData.class);
					for (Location location : teamData.getItemSpawnLocations())
					{
						for (int i = 0; i < teamData.resourceSpeedLevel; i++)
						{
							Item item = location.getWorld().dropItem(location, new ItemStack(material, 1));
							item.setVelocity(new Vector(0, 0, 0));

							items.add(item);
						}
					}
				}
			}
		}
	}

	void start()
	{
		runTaskTimer(GameAPI.getAPI(), ticks, ticks);
	}
	
}