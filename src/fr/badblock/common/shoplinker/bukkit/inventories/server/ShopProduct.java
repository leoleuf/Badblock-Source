package fr.badblock.common.shoplinker.bukkit.inventories.server;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mongodb.BasicDBObject;

import fr.badblock.common.shoplinker.bukkit.inventories.InventoriesLoader;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ShopProduct
{

	private String 		id;
	private String		name;
	private String		description;
	private int			price;
	private int			power;

	private String		ig_material;
	private byte			ig_data;

	private ItemStack	itemStack;

	public ShopProduct(BasicDBObject object)
	{
		this.id = object.getObjectId("_id").toString();
		this.name = object.getString("name");

		if (object.containsField("power"))
		{
			this.power = object.getInt("power");
		}

		this.description = object.getString("description");
		this.price = Integer.parseInt(object.getString("price"));

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
			lore.add(ChatColor.WHITE + "Prix : " + ChatColor.YELLOW + price + " points boutique");
			lore.add("");

			boolean added = false;
			if (this.description != null && !this.description.isEmpty())
			{
				String[] spaceSplitter = this.description.split(" ");
				String sentence = "";

				for (String word : spaceSplitter)
				{
					if (sentence.length() + word.length() > 30)
					{
						added = true;
						lore.add(ChatColor.YELLOW + InventoriesLoader.removeTags(sentence));

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
					lore.add(ChatColor.YELLOW + InventoriesLoader.removeTags(sentence));
				}
			}

			if (added)
			{
				lore.add("");
			}
			
			lore.add(ChatColor.LIGHT_PURPLE + " > Obtenir <");

			itemMeta.setLore(lore);

			itemStack.setItemMeta(itemMeta);
		}

		return itemStack;
	}

}