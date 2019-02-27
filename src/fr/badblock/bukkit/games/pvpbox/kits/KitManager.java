package fr.badblock.bukkit.games.pvpbox.kits;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import fr.badblock.api.common.tech.mongodb.MongoService;
import fr.badblock.gameapi.GameAPI;
import lombok.Getter;

public class KitManager
{

	@Getter
	private static KitManager		instance;
	
	private Map<String, Kit>			kits;
	
	public KitManager()
	{
		instance = this;
		
		kits = new HashMap<>();
		
		MongoService mongoService = GameAPI.getAPI().getMongoService();
		
		DB db = mongoService.getDb();
		DBCollection kitCollection = db.getCollection("pvpbox_kits");
		
		DBCursor cursor = kitCollection.find();
		
		while (cursor.hasNext())
		{
			BasicDBObject dbObject = (BasicDBObject) cursor.next();
			Kit kit = new Kit(dbObject);
			
			addKit(kit);
		}
	}
	
	public boolean exists(String kitName)
	{
		kitName = kitName.toLowerCase();
		return kits.containsKey(kitName) && kits.get(kitName) != null;
	}
	
	public Kit getKit(String kitName)
	{
		kitName = kitName.toLowerCase();
		return kits.get(kitName);
	}
	
	public Kit getDefaultKit()
	{
		Optional<Kit> optionalKit = kits.values().parallelStream().filter(kit -> kit.isDefault()).findAny();
		
		if (!optionalKit.isPresent())
		{
			return null;
		}
		
		return optionalKit.get();
	}
	
	public void addKit(Kit kit)
	{
		kits.put(kit.getName().toLowerCase(), kit);
		System.out.println("Loaded kit " + kit.getName());
	}
	
	public static KitManager load()
	{
		return new KitManager();
	}
	
}
