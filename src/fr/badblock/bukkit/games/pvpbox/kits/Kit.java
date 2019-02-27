package fr.badblock.bukkit.games.pvpbox.kits;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;

import fr.badblock.bukkit.games.pvpbox.players.BoxPlayer;
import fr.badblock.bukkit.games.pvpbox.utils.JsonUtil;
import fr.badblock.bukkit.games.pvpbox.utils.MaterialUtil;
import fr.badblock.gameapi.players.BadblockPlayer;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Kit
{

	private String								name;
	private boolean						isDefault;
	private String								permission;
	private Map<Integer, KitItem>		kitInventory;

	public Kit(BasicDBObject dbObject)
	{
		this.name = dbObject.getString("name");
		this.isDefault = dbObject.getBoolean("default");
		this.permission = dbObject.getString("permission");

		JsonElement jsonElement = JsonUtil.getJsonElement(dbObject, "inventory");
		loadKitInventory(jsonElement);
	}

	public void give(BadblockPlayer player)
	{
		BoxPlayer boxPlayer = BoxPlayer.get(player);

		if (boxPlayer == null)
		{
			return;
		}

		// Clear
		boxPlayer.clearAll();

		// Give
		kitInventory.entrySet().stream().forEach(entry ->
		{
			ItemStack to = entry.getValue().getItem();
			if (entry.getKey() == 103)
			{
				player.getInventory().setHelmet(to);
			}
			else if (entry.getKey() == 102)
			{
				player.getInventory().setChestplate(to);
			}
			else if (entry.getKey() == 101)
			{
				player.getInventory().setLeggings(to);
			}
			else if (entry.getKey() == 100)
			{
				player.getInventory().setBoots(to);
			}
			else
			{
				player.getInventory().setItem(entry.getKey(), to);
			}
		});
	}

	private void loadKitInventory(JsonElement jsonElement)
	{
		Map<Integer, KitItem> inventories = new HashMap<>();
		JsonArray jsonArray = jsonElement.getAsJsonArray();

		Iterator<JsonElement> iterator = jsonArray.iterator();

		while (iterator.hasNext())
		{
			JsonElement element = iterator.next();
			JsonObject object = element.getAsJsonObject();
			int slot = object.get("slot").getAsInt();

			String rawMaterial = object.get("material").getAsString();
			Material material = MaterialUtil.getMaterial(rawMaterial);

			int amount = object.get("amount").getAsInt();
			short dataShort = object.get("dataShort").getAsShort();



			List<KitEnchantment> enchantments = KitEnchantment.asEnchantments(object.get("enchantments"));

			KitItem kitItem = new KitItem(material, amount, dataShort, enchantments);
			inventories.put(slot, kitItem);
		}

		this.setKitInventory(inventories);
	}

	public boolean canUse(BadblockPlayer player)
	{
		boolean hasPerm = this.getPermission() == null || this.getPermission().isEmpty() || player.hasPermission(this.getPermission());

		if (hasPerm)
		{
			return true;
		}

		// usage check
		BoxPlayer boxPlayer = BoxPlayer.get(player);

		if (boxPlayer == null)
		{
			return false;
		}

		if (boxPlayer.getKitUsages() != null && boxPlayer.getKitUsages().containsKey(getName()))
		{
			long l = boxPlayer.getKitUsages().get(getName());
			if (l > 0)
			{
				return true;
			}
		}

		return false;
	}
	
	public void remove(BadblockPlayer player)
	{
		// usage check
		BoxPlayer boxPlayer = BoxPlayer.get(player);

		if (boxPlayer == null)
		{
			return;
		}
		
		if (boxPlayer.getKitUsages() != null && boxPlayer.getKitUsages().containsKey(getName()))
		{
			long l = boxPlayer.getKitUsages().get(getName());
			if (l > 0)
			{
				boxPlayer.getKitUsages().put(getName(), l - 1);
			}
		}
	}

}