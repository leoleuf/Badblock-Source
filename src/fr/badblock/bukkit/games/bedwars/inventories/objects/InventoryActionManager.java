package fr.badblock.bukkit.games.bedwars.inventories.objects;

import java.util.ArrayList;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
import fr.badblock.bukkit.games.bedwars.inventories.BukkitInventories;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.BukkitUtils;
import fr.badblock.gameapi.utils.general.Callback;
import fr.badblock.gameapi.utils.i18n.TranslatableWord;
import fr.badblock.gameapi.utils.i18n.Word.WordDeterminant;
import fr.badblock.gameapi.utils.i18n.messages.GameMessages;

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
			case CUSTOM_EFFECT:
				customEffect(player, action, actionData);
				break;
			case OPEN_INV:
				openInventory(player, action, actionData);
				break;
			case CLOSE_INV:
				closeInventory(player, action, actionData);
				break;
			case EXCHANGE:
				exchange(player, action, actionData);
				break;
			case RESOURCE_SPEED:
				resourceSpeed(player, action, actionData);
				break;
			case SPEED_MINING:
				speedMining(player, action, actionData);
				break;
			case SPEED:
				speed(player, action, actionData);
				break;
			case STRENGTH:
				strength(player, action, actionData);
				break;
			case PROTECTION:
				protection(player, action, actionData);
				break;
			case TRESPASSING:
				trespassing(player, action, actionData);
				break;
			case SLOW_DIG:
				slowDig(player, action, actionData);
				break;
			case HEAL:
				heal(player, action, actionData);
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
		System.out.println("E");
		BukkitInventories.getInventory(player, inventoryName, new Callback<Inventory>()
		{

			@Override
			public void done(Inventory inventory, Throwable error) {
				System.out.println("F");
				if (inventory == null) {
					closeInventory(player, action, null);
					return;
				}
				System.out.println("G");
				Bukkit.getScheduler().runTask(PluginBedWars.getInstance(), new Runnable()
				{
					@Override
					public void run()
					{
						System.out.println("H");
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

	private static void resourceSpeed(BadblockPlayer player, CustomItemAction action, String actionData) {
		if (player.getTeam() == null)
		{
			return;
		}

		BedWarsTeamData teamData = player.getTeam().teamData(BedWarsTeamData.class);
		if (teamData.resourceSpeedLevel >= 3)
		{
			player.sendTranslatedMessage("bedwars.alreadymaxspeedresource");
			return;
		}

		String[] levelSplitter = actionData.split(";");

		String rawExchanger = levelSplitter[teamData.resourceSpeedLevel - 1];

		ExchangeObject exchanger = ExchangeObject.toExchange(rawExchanger);

		// If the player has the needed items
		if (!player.getInventory().contains(exchanger.getMaterial(), exchanger.getAmount()))
		{
			TranslatableWord word = GameMessages.material(exchanger.getMaterial(), exchanger.getAmount() > 1, WordDeterminant.UNDEFINED);
			player.sendTranslatedMessage("bedwars.youmusthavetoimproveresourcespeed", exchanger.getAmount(), word.getWord(player.getPlayerData().getLocale()));
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

		player.updateInventory();

		teamData.resourceSpeedLevel++;

		player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.improvespeedresource", player.getName(), teamData.resourceSpeedLevel));
	}	

	private static void heal(BadblockPlayer player, CustomItemAction action, String actionData) {
		if (player.getTeam() == null)
		{
			return;
		}

		BedWarsTeamData teamData = player.getTeam().teamData(BedWarsTeamData.class);
		if (teamData.heal >= 1)
		{
			player.sendTranslatedMessage("bedwars.alreadymaxheal");
			return;
		}

		String[] levelSplitter = actionData.split(";");

		String rawExchanger = levelSplitter[teamData.heal - 1];

		ExchangeObject exchanger = ExchangeObject.toExchange(rawExchanger);

		// If the player has the needed items
		if (!player.getInventory().contains(exchanger.getMaterial(), exchanger.getAmount()))
		{
			TranslatableWord word = GameMessages.material(exchanger.getMaterial(), exchanger.getAmount() > 1, WordDeterminant.UNDEFINED);
			player.sendTranslatedMessage("bedwars.youmusthavetoimproveheal", exchanger.getAmount(), word.getWord(player.getPlayerData().getLocale()));
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

		player.updateInventory();

		teamData.heal++;

		player.getTeam().getOnlinePlayers().forEach(op -> op.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, teamData.heal)));
		player.getTeam().getOnlinePlayers().forEach(op -> op.playSound(Sound.ANVIL_USE));
		player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.improveheal", player.getName(), teamData.heal));
	}	

	private static void speedMining(BadblockPlayer player, CustomItemAction action, String actionData) {
		if (player.getTeam() == null)
		{
			return;
		}

		BedWarsTeamData teamData = player.getTeam().teamData(BedWarsTeamData.class);
		if (teamData.speedMining >= 1)
		{
			player.sendTranslatedMessage("bedwars.alreadymaxmininglevel");
			return;
		}

		String[] levelSplitter = actionData.split(";");

		String rawExchanger = levelSplitter[teamData.speedMining - 1];

		ExchangeObject exchanger = ExchangeObject.toExchange(rawExchanger);

		// If the player has the needed items
		if (!player.getInventory().contains(exchanger.getMaterial(), exchanger.getAmount()))
		{
			TranslatableWord word = GameMessages.material(exchanger.getMaterial(), exchanger.getAmount() > 1, WordDeterminant.UNDEFINED);
			player.sendTranslatedMessage("bedwars.youmusthavetoimprovespeedmining", exchanger.getAmount(), word.getWord(player.getPlayerData().getLocale()));
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

		player.updateInventory();

		teamData.speedMining++;

		player.getTeam().getOnlinePlayers().forEach(op -> op.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, teamData.resourceSpeedLevel)));
		player.getTeam().getOnlinePlayers().forEach(op -> op.playSound(Sound.ANVIL_USE));
		player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.improvespeedresource", player.getName(), teamData.resourceSpeedLevel));
	}


	private static void trespassing(BadblockPlayer player, CustomItemAction action, String actionData) {
		if (player.getTeam() == null)
		{
			return;
		}

		BedWarsTeamData teamData = player.getTeam().teamData(BedWarsTeamData.class);
		if (teamData.trespassing)
		{
			player.sendTranslatedMessage("bedwars.alreadytrespassing");
			return;
		}

		String[] levelSplitter = actionData.split(";");

		String rawExchanger = levelSplitter[0];

		ExchangeObject exchanger = ExchangeObject.toExchange(rawExchanger);

		// If the player has the needed items
		if (!player.getInventory().contains(exchanger.getMaterial(), exchanger.getAmount()))
		{
			TranslatableWord word = GameMessages.material(exchanger.getMaterial(), exchanger.getAmount() > 1, WordDeterminant.UNDEFINED);
			player.sendTranslatedMessage("bedwars.youmusthavetoimprovetrespassing", exchanger.getAmount(), word.getWord(player.getPlayerData().getLocale()));
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

		player.updateInventory();

		teamData.trespassing = true;

		player.getTeam().getOnlinePlayers().forEach(op -> op.playSound(Sound.ANVIL_USE));
		player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.improvetrespassingresource", player.getName()));
	}

	private static void speed(BadblockPlayer player, CustomItemAction action, String actionData) {
		if (player.getTeam() == null)
		{
			return;
		}

		BedWarsTeamData teamData = player.getTeam().teamData(BedWarsTeamData.class);
		if (teamData.speed >= 1)
		{
			player.sendTranslatedMessage("bedwars.alreadymaxspeedlevel");
			return;
		}

		String[] levelSplitter = actionData.split(";");

		String rawExchanger = levelSplitter[teamData.speed - 1];

		ExchangeObject exchanger = ExchangeObject.toExchange(rawExchanger);

		// If the player has the needed items
		if (!player.getInventory().contains(exchanger.getMaterial(), exchanger.getAmount()))
		{
			TranslatableWord word = GameMessages.material(exchanger.getMaterial(), exchanger.getAmount() > 1, WordDeterminant.UNDEFINED);
			player.sendTranslatedMessage("bedwars.youmusthavetoimprovespeed", exchanger.getAmount(), word.getWord(player.getPlayerData().getLocale()));
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

		player.updateInventory();

		teamData.speed++;

		player.getTeam().getOnlinePlayers().forEach(op -> op.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, teamData.speed)));
		player.getTeam().getOnlinePlayers().forEach(op -> op.playSound(Sound.ANVIL_USE));
		player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.improvespeedresource", player.getName(), teamData.speed));
	}

	private static void slowDig(BadblockPlayer player, CustomItemAction action, String actionData) {
		if (player.getTeam() == null)
		{
			return;
		}

		BedWarsTeamData teamData = player.getTeam().teamData(BedWarsTeamData.class);
		if (teamData.slowDig > System.currentTimeMillis())
		{
			player.sendTranslatedMessage("bedwars.alreadymaxslowDiglevel");
			return;
		}

		String[] levelSplitter = actionData.split(";");

		String rawExchanger = levelSplitter[0];

		ExchangeObject exchanger = ExchangeObject.toExchange(rawExchanger);

		// If the player has the needed items
		if (!player.getInventory().contains(exchanger.getMaterial(), exchanger.getAmount()))
		{
			TranslatableWord word = GameMessages.material(exchanger.getMaterial(), exchanger.getAmount() > 1, WordDeterminant.UNDEFINED);
			player.sendTranslatedMessage("bedwars.youmusthavetoslowdig", exchanger.getAmount(), word.getWord(player.getPlayerData().getLocale()));
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

		player.updateInventory();

		teamData.slowDig = System.currentTimeMillis() + 10_000L;

		for (BadblockPlayer plo : BukkitUtils.getPlayers())
		{
			if (plo.getTeam() == null)
			{
				continue;
			}
			
			if (plo.getTeam().equals(player.getTeam()))
			{
				continue;
			}
			
			plo.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, 20 * 10, 2));
			plo.playSound(Sound.ENDERMAN_TELEPORT);
			plo.sendTranslatedMessage("bedwars.slowdig", player.getTeam().getChatPrefix().getAsLine(plo), player.getName());
		}
	
		player.getTeam().getOnlinePlayers().forEach(op -> op.playSound(Sound.ANVIL_USE));
		player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.improveslowdig", player.getName()));
	}

	private static void strength(BadblockPlayer player, CustomItemAction action, String actionData) {
		if (player.getTeam() == null)
		{
			return;
		}

		BedWarsTeamData teamData = player.getTeam().teamData(BedWarsTeamData.class);
		if (teamData.strength >= 1)
		{
			player.sendTranslatedMessage("bedwars.alreadymaxstrengthlevel");
			return;
		}

		String[] levelSplitter = actionData.split(";");

		String rawExchanger = levelSplitter[teamData.strength - 1];

		ExchangeObject exchanger = ExchangeObject.toExchange(rawExchanger);

		// If the player has the needed items
		if (!player.getInventory().contains(exchanger.getMaterial(), exchanger.getAmount()))
		{
			TranslatableWord word = GameMessages.material(exchanger.getMaterial(), exchanger.getAmount() > 1, WordDeterminant.UNDEFINED);
			player.sendTranslatedMessage("bedwars.youmusthavetoimprovespeed", exchanger.getAmount(), word.getWord(player.getPlayerData().getLocale()));
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

		player.updateInventory();

		teamData.strength++;

		player.getTeam().getOnlinePlayers().forEach(op -> op.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, Integer.MAX_VALUE, teamData.strength)));
		player.getTeam().getOnlinePlayers().forEach(op -> op.playSound(Sound.ANVIL_USE));
		player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.improvestrengthresource", player.getName(), teamData.strength));
	}

	private static void protection(BadblockPlayer player, CustomItemAction action, String actionData) {
		if (player.getTeam() == null)
		{
			return;
		}

		BedWarsTeamData teamData = player.getTeam().teamData(BedWarsTeamData.class);
		if (teamData.protection >= 1)
		{
			player.sendTranslatedMessage("bedwars.alreadymaxprotectionlevel");
			return;
		}

		String[] levelSplitter = actionData.split(";");

		String rawExchanger = levelSplitter[teamData.protection - 1];

		ExchangeObject exchanger = ExchangeObject.toExchange(rawExchanger);

		// If the player has the needed items
		if (!player.getInventory().contains(exchanger.getMaterial(), exchanger.getAmount()))
		{
			TranslatableWord word = GameMessages.material(exchanger.getMaterial(), exchanger.getAmount() > 1, WordDeterminant.UNDEFINED);
			player.sendTranslatedMessage("bedwars.youmusthavetoimprovespeed", exchanger.getAmount(), word.getWord(player.getPlayerData().getLocale()));
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

		player.updateInventory();

		teamData.protection++;

		player.getTeam().getOnlinePlayers().forEach(op -> op.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, teamData.protection)));
		player.getTeam().getOnlinePlayers().forEach(op -> op.playSound(Sound.ANVIL_USE));
		player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.improveprotectionresource", player.getName(), teamData.protection));
	}

	private static void exchange(BadblockPlayer player, CustomItemAction action, String actionData) {	
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
			player.sendTranslatedMessage("bedwars.youmusthave", exchanger.getAmount(), word.getWord(player.getPlayerData().getLocale()));
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

			boolean egg = false;
			boolean silverfish = false;
			boolean golem = false;

			if (exchangeObject.getData() == 2 && exchangeObject.getMaterial().equals(Material.EGG))
			{
				exchangeObject.setData((byte) 0);
				egg = true;
			}
			else if (exchangeObject.getData() == 3 && exchangeObject.getMaterial().equals(Material.EGG))
			{
				exchangeObject.setData((byte) 0);
				silverfish = true;
			}
			else if (exchangeObject.getData() == 4 && exchangeObject.getMaterial().equals(Material.EGG))
			{
				exchangeObject.setData((byte) 0);
				golem = true;
			}

			ItemStack itemStack = new ItemStack(exchangeObject.getMaterial(), exchangeObject.getAmount(), exchangeObject.getData());

			if (egg)
			{
				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.setDisplayName("Auto-Bridge");
				itemStack.setItemMeta(itemMeta);
			}
			else if (silverfish)
			{
				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.setDisplayName("Silverfish");
				itemStack.setItemMeta(itemMeta);
			}
			else if (golem)
			{
				ItemMeta itemMeta = itemStack.getItemMeta();
				itemMeta.setDisplayName("Golem");
				itemStack.setItemMeta(itemMeta);
			}

			for (EnchantmentObject enchantmentObject : exchangeObject.getEnchantments())
			{
				itemStack.addUnsafeEnchantment(enchantmentObject.getEnchantment(), enchantmentObject.getLevel());
			}

			if (itemStack.getType().name().contains("HELMET"))
			{
				player.getInventory().setHelmet(itemStack);
			}
			else if (itemStack.getType().name().contains("CHESTPLATE"))
			{
				player.getInventory().setChestplate(itemStack);
			}
			else if (itemStack.getType().name().contains("LEGGINGS"))
			{
				player.getInventory().setLeggings(itemStack);
			}
			else if (itemStack.getType().name().contains("BOOTS"))
			{
				player.getInventory().setBoots(itemStack);
			}
			else
			{
				player.getInventory().addItem(itemStack);
			}
		}

		player.updateInventory();
	}

	private static void customEffect(BadblockPlayer player, CustomItemAction action, String actionData) {
		// TODO
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
