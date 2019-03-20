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
public class CategoryShop
{

	private String 		id;
	private String		name;
	private String		subname;
	private int			power;
	private boolean	visibility;

	private List<ShopProduct> products;

	private String		ig_material;
	private byte			ig_data;

	private ItemStack	itemStack;


	public CategoryShop(BasicDBObject object)
	{
		this.id = object.getObjectId("_id").toString();
		this.name = object.getString("name");
		this.power = object.getInt("power");
		this.subname = object.getString("subname");
		this.visibility = object.getBoolean("visibility");
		ShopLinker.getConsole().sendMessage("Loaded category: " + name);

		this.products = new ArrayList<>();

		MongoService mongo = ShopLinker.getInstance().getMongoService();
		DB db = mongo.getDb();
		DBCollection collection = db.getCollection("product_list");

		if (this.name.equalsIgnoreCase("Grades Globaux"))
		{
			BasicDBObject query = new BasicDBObject();
			query.put("cat_id", null);

			DBCursor cursor = collection.find(query);
			while (cursor.hasNext())
			{
				this.products.add(new ShopProduct((BasicDBObject) cursor.next()));
			}
		}
		else
		{
			BasicDBObject query = new BasicDBObject();
			query.put("cat_id", object.getObjectId("_id"));

			DBCursor cursor = collection.find(query);
			while (cursor.hasNext())
			{
				this.products.add(new ShopProduct((BasicDBObject) cursor.next()));
			}
		}

		Collections.sort(this.products, new Comparator<ShopProduct>() {
			@Override
			public int compare(ShopProduct o1, ShopProduct o2) {
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
			lore.add("");

			boolean added = false;
			if (this.subname != null && !this.subname.isEmpty())
			{
				String[] spaceSplitter = this.subname.split(" ");
				String sentence = "";

				for (String word : spaceSplitter)
				{
					if (sentence.length() + word.length() > 25)
					{
						added = true;
						lore.add(ChatColor.WHITE + InventoriesLoader.removeTags(sentence));

						if (word.length() > 1)
						{
							sentence = word + " ";
						}
						else
						{
							sentence = "";
						}
					}
					else
					{
						sentence += word + " ";
					}
				}

				if (sentence.length() > 3)
				{
					added = true;
					lore.add(ChatColor.WHITE + InventoriesLoader.removeTags(sentence));
				}
			}

			if (added)
			{
				lore.add("");
			}
			
			if (products.size() > 1)
			{
				lore.add(ChatColor.LIGHT_PURPLE + " > Accéder aux " + products.size() + " produits <");
			}
			else
			{
				lore.add(ChatColor.LIGHT_PURPLE + " > Accéder aux produits <");
			}

			itemMeta.setLore(lore);
			itemStack.setItemMeta(itemMeta);
		}

		return itemStack;
	}

}