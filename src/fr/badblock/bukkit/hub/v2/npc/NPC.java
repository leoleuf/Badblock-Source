package fr.badblock.bukkit.hub.v2.npc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.EntityType;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import fr.badblock.bukkit.hub.v2.utils.EntityUtils;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.fakeentities.FakeEntity;
import fr.badblock.gameapi.packets.watchers.WatcherZombie;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@AllArgsConstructor
public class NPC
{

	@Getter@Setter private static Map<String, NPC> npcs = new HashMap<>();

	private String					uniqueId;
	private String					displayName;
	private EntityType				entityType;
	private DBObject[]				actions;
	private boolean					vip;
	private boolean					staff;
	private FakeLocation			location;
	private List<String>			permissions;

	private transient FakeEntity<?>	fakeEntity;

	public NPC(String uniqueId, String displayName, EntityType entityType, DBObject[] actions, boolean vip, boolean staff, FakeLocation location, List<String> permissions)
	{
		this.uniqueId = uniqueId;
		this.displayName = displayName;
		this.entityType = entityType;
		this.actions = actions;
		this.vip = vip;
		this.staff = staff;
		this.location = location;
		this.permissions = permissions;
	}
	
	public DBObject toObject()
	{
		BasicDBObject result = new BasicDBObject();
		result.append("uniqueId", uniqueId);
		result.append("displayName", displayName);
		result.append("entityType", entityType.name());
		result.append("actions", actions);
		result.append("vip", vip);
		result.append("staff", staff);
		result.append("location", location.toObject());
		result.append("permissions", permissions);
		return result;
	}

	public boolean isAlive()
	{
		return getFakeEntity() != null && !getFakeEntity().isRemoved();
	}
	
	public void spawn()
	{
		if (isAlive())
		{
			// Teleport
			if (!getFakeEntity().getLocation().equals(location.toLocation()))
			{
				getFakeEntity().teleport(location.toLocation());
			}
			// Change type
			if (!getFakeEntity().getType().equals(getEntityType()))
			{
				setFakeEntity(null);
				spawn();
			}
			return;
		}
		setFakeEntity(EntityUtils.spawn(getLocation().toLocation(), getEntityType(), WatcherZombie.class, false, false, false, false, getDisplayName()));
	}

	public void despawn()
	{
		if (!isAlive())
		{
			return;
		}
		getFakeEntity().remove();
		getFakeEntity().destroy();
	}
	
	public static NPC toNPC(DBObject object)
	{
		return GameAPI.getGson().fromJson(object.toString(), NPC.class);
	}

	
	public static String generateUniqueId()
	{
		return UUID.randomUUID().toString().split("-")[0];
	}

}