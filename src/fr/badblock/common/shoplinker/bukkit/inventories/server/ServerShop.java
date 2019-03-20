package fr.badblock.common.shoplinker.bukkit.inventories.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

import fr.badblock.common.shoplinker.bukkit.ShopLinker;
import fr.badblock.common.shoplinker.bukkit.inventories.InventoriesLoader;
import fr.badblock.common.shoplinker.mongodb.MongoService;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ServerShop
{

	private String			  				_id;
	private String			   				name;
	private List<CategoryShop>		categories;
	private int								power;
	private boolean						visibility;

	private String							ig_material;
	private byte								ig_data;

	private ItemStack					itemStack;

	public ServerShop(BasicDBObject object)
	{
		this._id = object.getObjectId("_id").toString();
		this.name = object.getString("name");
		this.power = object.getInt("power");
		this.visibility = object.getBoolean("visibility");
		ShopLinker.getConsole().sendMessage("Loaded server: " + name);
		MongoService mongo = ShopLinker.getInstance().getMongoService();
		DB db = mongo.getDb();
		DBCollection collection = db.getCollection("category_list");
		BasicDBObject query = new BasicDBObject();
		query.put("server_id", object.getObjectId("_id"));

		this.categories = new ArrayList<>();
		DBCursor cursor = collection.find(query);
		while (cursor.hasNext())
		{
			this.categories.add(new CategoryShop((BasicDBObject) cursor.next()));
		}

		Collections.sort(this.categories, new Comparator<CategoryShop>() {
			@Override
			public int compare(CategoryShop o1, CategoryShop o2) {
				return Integer.compare(o2.getPower(), o1.getPower());
			}
		});

		if (object.containsField("ig_material"))
		{
			this.ig_material = object.getString("ig_material");
		}

		if (object.containsField("ig_data"))
		{
			this.ig_data = (byte) object.getInt("ig_data");
		}
	}

	public ItemStack toItemStack()
	{
		if (itemStack == null)
		{
			itemStack = new ItemStack(InventoriesLoader.getFrom(ig_material), 1, ig_data);

			ItemMeta itemMeta = itemStack.getItemMeta();
			itemMeta.setDisplayName(ChatColor.YELLOW + this.name);

			List<String> lore = new ArrayList<>();
			if (!this.categories.isEmpty())
			{
				lore.add("");
				lore.add(ChatColor.WHITE + "Obtenez :");
				for (CategoryShop category : this.categories)
				{
					lore.add(ChatColor.WHITE + " - " + ChatColor.AQUA + category.getName());
				}
			}
			else
			{
				lore.add("");
			}
			
			itemMeta.setLore(lore);
			itemStack.setItemMeta(itemMeta);
		}

		return itemStack;
	}

}