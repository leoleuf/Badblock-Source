package fr.badblock.bukkit.hub.utils.pnj.inventories;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import fr.badblock.bukkit.hub.inventories.abstracts.inventories.CustomInventory;
import fr.badblock.bukkit.hub.inventories.selector.items.special.BookSelectorChooserItem;
import fr.badblock.bukkit.hub.inventories.selector.items.special.DescSelectorItem;
import fr.badblock.bukkit.hub.inventories.selector.items.special.GameSelectorChooserItem;
import fr.badblock.bukkit.hub.inventories.selector.items.special.QuitInventoryItem;
public class SEntryDataInventory extends CustomInventory {
	public SEntryDataInventory(String gameName, Material highlightedItem, DyeColor dyeColor, String server) {
		super("hub.items." + gameName + ".submenutitle", 3);
		this.setItem(4, new DescSelectorItem(gameName, highlightedItem));
		this.setItem(13, new GameSelectorChooserItem(gameName, dyeColor, server));
		this.setItem(18, new BookSelectorChooserItem(gameName));
		this.setItem(26, new QuitInventoryItem());
	}
}