package fr.badblock.bukkit.games.pvpbox.totems;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import fr.badblock.api.common.tech.mongodb.MongoService;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import lombok.Data;
import lombok.Getter;

@Data
public class TotemManager
{

	@Getter
	private static TotemManager		instance;
	
	private List<Totem>						totems;

	public TotemManager()
	{
		instance = this;

		totems = new ArrayList<>();
		
		MongoService mongoService = GameAPI.getAPI().getMongoService();

		DB db = mongoService.getDb();
		DBCollection kitCollection = db.getCollection("pvpbox_totems");

		DBCursor cursor = kitCollection.find();

		while (cursor.hasNext())
		{
			BasicDBObject dbObject = (BasicDBObject) cursor.next();
			Totem totem = new Totem(dbObject);

			addTotem(totem);
		}
	}
	
	public void addTotem(Totem totem)
	{
		totems.add(totem);
	}
	
	public Totem getTotem(BadblockPlayer player)
	{
		Optional<Totem> optional = getTotems().parallelStream().filter(totem -> totem.isIn(player)).findAny();
		
		if (!optional.isPresent())
		{
			return null;
		}
		
		return optional.get();
	}
	
	public static TotemManager load()
	{
		return new TotemManager();
	}

}
