package fr.badblock.bukkit.games.pvpbox.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

import fr.badblock.api.common.utils.GsonUtils;

@SuppressWarnings("deprecation")
public class JsonUtil
{

	/**
	 * Get JsonElement from the database object
	 * 
	 * @param part
	 * @return
	 */
	public static JsonElement getJsonElement(DBObject dbObject, String part) {
		// If the database object contains the key
		if (dbObject.containsField(part)) {
			// Serialize the data
			String serialize = JSON.serialize(dbObject.get(part));
			// Deserialize as a JsonElement
			JsonElement jsonElement = GsonUtils.toJsonElement(serialize);
			// Returns the element
			return jsonElement;
		} else {
			// Returns a new JsonObject
			return new JsonObject();
		}
	}

}