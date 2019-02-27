package fr.badblock.bukkit.games.pvpbox.totems;

import org.bukkit.Location;
import org.bukkit.potion.PotionEffectType;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;

import fr.badblock.bukkit.games.pvpbox.config.BoxLocation;
import fr.badblock.bukkit.games.pvpbox.utils.JsonUtil;
import fr.badblock.gameapi.players.BadblockPlayer;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Totem
{

	private double							radius;
	private Location							location;
	private PotionEffectType			potionEffectType;
	private int									level;	
	private int									ticks;
	
	public Totem(BasicDBObject dbObject)
	{
		JsonElement jsonLocationElement = JsonUtil.getJsonElement(dbObject, "location");
		JsonObject jsonLocationObject = jsonLocationElement.getAsJsonObject();
		BoxLocation boxLocation = new BoxLocation(jsonLocationObject);
		this.location = boxLocation.getBukkitLocation();
		this.radius = dbObject.getDouble("radius");
		
		String potionEffectRawType = dbObject.getString("potionEffectType");
		this.potionEffectType = PotionEffectType.getByName(potionEffectRawType);

		this.level = dbObject.getInt("level");
		this.ticks = dbObject.getInt("ticks");
	}
	
	public boolean isIn(BadblockPlayer player)
	{
		Location playerLocation = player.getLocation();
		
		double xDiff = Math.abs(playerLocation.getX() - location.getX());
		double yDiff = Math.abs(playerLocation.getY() - location.getY());
		double zDiff = Math.abs(playerLocation.getZ() - location.getZ());
		
		return xDiff <= radius && yDiff <= radius && zDiff <= radius;
	}
	
}
