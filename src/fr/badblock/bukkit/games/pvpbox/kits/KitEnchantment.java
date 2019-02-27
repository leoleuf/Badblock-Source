package fr.badblock.bukkit.games.pvpbox.kits;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.enchantments.Enchantment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KitEnchantment
{

	private transient	Enchantment	enchantment;
	
	private String								type;
	private int										level;
	
	public KitEnchantment(String type, int level)
	{
		this(toEnchantment(type), type, level);
	}
	
	public static Enchantment toEnchantment(String type)
	{
		return Enchantment.getByName(type);
	}
	
	public static KitEnchantment asEnchantment(JsonObject jsonObject)
	{
		String type = jsonObject.get("type").getAsString();
		int level = jsonObject.get("level").getAsInt();
	
		return new KitEnchantment(type, level);
	}
	
	public static List<KitEnchantment> asEnchantments(JsonElement jsonElement)
	{
		List<KitEnchantment> enchantments = new ArrayList<>();
		JsonArray jsonArray = jsonElement.getAsJsonArray();
		
		Iterator<JsonElement> iterator = jsonArray.iterator();
		
		while (iterator.hasNext())
		{
			JsonElement element = iterator.next();
			JsonObject jsonObject = element.getAsJsonObject();
			
			enchantments.add(asEnchantment(jsonObject));
		}
		
		return enchantments;
	}
	
}