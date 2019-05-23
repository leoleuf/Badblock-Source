package fr.badblock.bukkit.games.rush.inventories.objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import fr.badblock.bukkit.games.rush.PluginRush;
import fr.badblock.bukkit.games.rush.inventories.BukkitInventories;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
import fr.badblock.gameapi.utils.general.Callback;
import fr.badblock.gameapi.utils.i18n.TranslatableWord;
import fr.badblock.gameapi.utils.i18n.Word.WordDeterminant;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;
import net.md_5.bungee.api.ChatColor;

public class InventoryActionManager
{

	private static Map<UUID, String> playerInventories = new HashMap<>();
	private static Map<UUID, TempInventoryObject> playerInv = new HashMap<>();

	public static void handle(BadblockPlayer player, String inventoryName, InventoryItemObject object, InventoryActionType type, ItemStack offerStack) {
		// No defined type
		if (type == null) return;
		for (InventoryAction inventoryAction : object.getActions()) {
			if (!inventoryAction.getActionType().equals(type)) continue;
			CustomItemAction action = inventoryAction.getAction();
			if (action == null) {
				continue;
			}
			if (action.equals(CustomItemAction.NOTHING)) continue;
			// TODO: do antispam
			String actionData = inventoryAction.getActionData();
			switch (action) {
			case OPEN_INV:
				openInventory(player, action, actionData);
				break;
			case CLOSE_INV:
				closeInventory(player, action, actionData);
				break;
			case EXCHANGE:
				exchange(player, action, actionData, object.getName());
				break;
			default:
				break;
			}
			break;
		}
	}

	public static void openInventory(BadblockPlayer player, TempInventoryObject tempInventoryObject)
	{
		openInventory(player, tempInventoryObject.getAction(), tempInventoryObject.getActionData());
	}

	public static void openInventory(BadblockPlayer player, CustomItemAction action, String actionData)
	{
		// Save last inventory
		playerInv.put(player.getUniqueId(), new TempInventoryObject(action, actionData));

		// Inventory open
		String inventoryName = actionData;
		BukkitInventories.getInventory(player, inventoryName, new Callback<Inventory>()
		{

			@Override
			public void done(Inventory inventory, Throwable error) {
				if (inventory == null) {
					closeInventory(player, action, null);
					return;
				}
				Bukkit.getScheduler().runTask(PluginRush.getInstance(), new Runnable()
				{
					@Override
					public void run()
					{
						player.closeInventory(); // standby
						player.openInventory(inventory);
						setInventory(player, inventoryName);
					}
				});
			}

		});
	}

	private static void closeInventory(BadblockPlayer player, CustomItemAction action, String actionData) {
		if (actionData != null && !actionData.isEmpty()) {
			// TODO: do another action ?
		}
		setInventory(player, null);
		player.closeInventory();
	}

	@SuppressWarnings("deprecation")
	private static void exchange(BadblockPlayer player, CustomItemAction action, String actionData, String name) {
		// 5-diamond;1-iron_chestplate;1-iron_helmet
		String[] splitter = actionData.split(";");

		if (splitter.length < 2)
		{
			return;
		}

		String rawExchanger = splitter[0];

		ExchangeObject exchanger = ExchangeObject.toExchange(rawExchanger);

		List<String> rawExchanges = new ArrayList<>();

		for (int i = 0; i < splitter.length; i++)
		{
			if (i == 0)
			{
				continue;
			}

			rawExchanges.add(splitter[i]);
		}

		List<ExchangeObject> exchanges = rawExchanges.stream().map(exchange -> ExchangeObject.toExchange(exchange)).collect(Collectors.toList());

		// If the player has the needed items
		if (!player.getInventory().contains(exchanger.getMaterial(), exchanger.getAmount()))
		{
			TranslatableWord word = GameMessages.material(exchanger.getMaterial(), exchanger.getAmount() > 1, WordDeterminant.UNDEFINED);
			player.sendTranslatedMessage("rush.youmusthave", exchanger.getAmount(), word.getWord(player.getPlayerData().getLocale()));
			return;
		}

		int amount = exchanger.getAmount();

		for (int i = 0; i < player.getInventory().getContents().length; i++)
		{
			ItemStack item = player.getInventory().getContents()[i];
			if (amount <= 0)
			{
				break;
			}

			if (item == null || item.getType() == null)
			{
				continue;
			}

			if (!item.getType().equals(exchanger.getMaterial()))
			{
				continue;
			}

			int a = item.getAmount();

			if (a > amount && amount > 0)
			{
				a -= amount;
				amount = 0;
				item.setAmount(a);
				player.getInventory().setItem(i, item);

			}
			else if (amount > 0)
			{
				amount -= a;
				player.getInventory().setItem(i, new ItemStack(Material.AIR, 1));
			}
			else
			{
				break;
			}
		}

		// Exchanger items
		for (ExchangeObject exchangeObject : exchanges)
		{	

			if (exchangeObject.getMaterial() != null && exchangeObject.getMaterial().name().toLowerCase().contains("sword"))
			{
				List<ItemStack> itr = Arrays.asList(player.getInventory().getContents());
				for (int i = 0; i < itr.size(); i++)
				{
					ItemStack st = itr.get(i);
					if (st != null && st.getType() != null && st.getType().name().toLowerCase().contains("sword"))
					{
						player.getInventory().remove(st);
					}
				}
			}
			
			if (exchangeObject.getMaterial() != null && exchangeObject.getMaterial().name().toLowerCase().contains("pickaxe"))
			{
				List<ItemStack> itr = Arrays.asList(player.getInventory().getContents());
				for (int i = 0; i < itr.size(); i++)
				{
					ItemStack st = itr.get(i);
					if (st != null && st.getType() != null && st.getType().name().toLowerCase().contains("pickaxe"))
					{
						player.getInventory().remove(st);
					}
				}
			}
			
			if (exchangeObject.getMaterial() != null && exchangeObject.getMaterial().name().toLowerCase().contains("_axe"))
			{
				List<ItemStack> itr = Arrays.asList(player.getInventory().getContents());
				for (int i = 0; i < itr.size(); i++)
				{
					ItemStack st = itr.get(i);
					if (st != null && st.getType() != null && st.getType().name().toLowerCase().contains("_axe"))
					{
						player.getInventory().remove(st);
					}
				}
			}
			
			player.updateInventory();
			
			ItemStack itemStack = new ItemStack(exchangeObject.getMaterial(), exchangeObject.getAmount(), exchangeObject.getData());
			
			if (itemStack.getType() != null && itemStack.getType().equals(Material.WOOL))
			{
				BadblockTeam team = player.getTeam();
				if (team != null)
				{
					itemStack = new ItemStack(exchangeObject.getMaterial(), exchangeObject.getAmount(), team.getDyeColor().getWoolData());
				}
			}

			for (EnchantmentObject enchantmentObject : exchangeObject.getEnchantments())
			{
				itemStack.addUnsafeEnchantment(enchantmentObject.getEnchantment(), enchantmentObject.getLevel());
			}

			if (itemStack.getType().name().contains("HELMET"))
			{
				if (itemStack.getType().equals(Material.LEATHER_HELMET))
				{
					if (player.getTeam() != null)
					{
						itemStack = new ItemStack(exchangeObject.getMaterial(), exchangeObject.getAmount(), exchangeObject.getData());;
						LeatherArmorMeta lam = (LeatherArmorMeta) itemStack.getItemMeta();
						lam.setColor(player.getTeam().getDyeColor().getColor());
						itemStack.setItemMeta(lam);
					}
				}
				
				player.getInventory().setHelmet(itemStack);
			}
			else if (itemStack.getType().name().contains("CHESTPLATE"))
			{
				if (itemStack.getType().equals(Material.LEATHER_CHESTPLATE))
				{
					if (player.getTeam() != null)
					{
						itemStack = new ItemStack(exchangeObject.getMaterial(), exchangeObject.getAmount(), exchangeObject.getData());;
						LeatherArmorMeta lam = (LeatherArmorMeta) itemStack.getItemMeta();
						lam.setColor(player.getTeam().getDyeColor().getColor());
						itemStack.setItemMeta(lam);
					}
				}

				player.getInventory().setChestplate(itemStack);
			}
			else if (itemStack.getType().name().contains("LEGGINGS"))
			{
				if (itemStack.getType().equals(Material.LEATHER_LEGGINGS))
				{
					if (player.getTeam() != null)
					{
						itemStack = new ItemStack(exchangeObject.getMaterial(), exchangeObject.getAmount(), exchangeObject.getData());;
						LeatherArmorMeta lam = (LeatherArmorMeta) itemStack.getItemMeta();
						lam.setColor(player.getTeam().getDyeColor().getColor());
						itemStack.setItemMeta(lam);
					}
				}

				player.getInventory().setLeggings(itemStack);
			}
			else if (itemStack.getType().name().contains("BOOTS"))
			{
				if (itemStack.getType().equals(Material.LEATHER_BOOTS))
				{
					if (player.getTeam() != null)
					{
						itemStack = new ItemStack(exchangeObject.getMaterial(), exchangeObject.getAmount(), exchangeObject.getData());;
						LeatherArmorMeta lam = (LeatherArmorMeta) itemStack.getItemMeta();
						lam.setColor(player.getTeam().getDyeColor().getColor());
						itemStack.setItemMeta(lam);
					}
				}

				player.getInventory().setBoots(itemStack);
			}
			else if (itemStack.getType().name().contains("SWORD"))
			{
				player.getInventory().addItem(itemStack);
			}
			else
			{
				player.getInventory().addItem(itemStack);
			}
		}

		player.playSound(Sound.ANVIL_USE);
		player.sendTranslatedMessage("rush.havegot", ChatColor.translateAlternateColorCodes('&', name));
		player.updateInventory();
	}

	public static void setInventory(BadblockPlayer player, String inventoryName) {
		UUID uniqueId = player.getUniqueId();
		playerInventories.put(uniqueId, inventoryName);
	}

	public static String getInventory(Player player) {
		UUID uniqueId = player.getUniqueId();
		return playerInventories.get(uniqueId);
	}

}
