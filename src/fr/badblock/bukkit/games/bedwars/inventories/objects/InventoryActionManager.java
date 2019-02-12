package fr.badblock.bukkit.games.bedwars.inventories.objects;

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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import fr.badblock.bukkit.games.bedwars.entities.BedWarsTeamData;
import fr.badblock.bukkit.games.bedwars.inventories.BukkitInventories;
import fr.badblock.bukkit.games.bedwars.inventories.config.ItemLoader;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockTeam;
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
			case SHARPNESS:
				sharpness(player, action, actionData);
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
			case STRENGTHEN_ARMOR:
				strengthenArmor(player, action, actionData);
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
				Bukkit.getScheduler().runTask(PluginBedWars.getInstance(), new Runnable()
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

		player.getTeam().getOnlinePlayers().forEach(op -> op.playSound(Sound.ANVIL_USE));
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

		String rawExchanger = levelSplitter[teamData.heal];

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

		player.getTeam().getOnlinePlayers().forEach(op -> op.playSound(Sound.ANVIL_USE));
		player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.improveheal", player.getName(), teamData.heal));
	}	

	private static void strengthenArmor(BadblockPlayer player, CustomItemAction action, String actionData) {
		if (player.getTeam() == null)
		{
			return;
		}

		BedWarsTeamData teamData = player.getTeam().teamData(BedWarsTeamData.class);
		if (teamData.strengthenArmor >= 4)
		{
			teamData.strengthenArmor = 4;
			return;
		}

		String[] levelSplitter = actionData.split(";");

		String rawExchanger = levelSplitter[teamData.strengthenArmor];

		ExchangeObject exchanger = ExchangeObject.toExchange(rawExchanger);

		// If the player has the needed items
		if (!player.getInventory().contains(exchanger.getMaterial(), exchanger.getAmount()))
		{
			TranslatableWord word = GameMessages.material(exchanger.getMaterial(), exchanger.getAmount() > 1, WordDeterminant.UNDEFINED);
			player.sendTranslatedMessage("bedwars.youmusthavetoimprovearmor", exchanger.getAmount(), word.getWord(player.getPlayerData().getLocale()));
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

		teamData.strengthenArmor++;

		player.getTeam().getOnlinePlayers().forEach(op ->
		{
			ItemStack helmet = op.getInventory().getHelmet();
			if (helmet != null && !helmet.getType().equals(Material.AIR))
			{
				helmet.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, teamData.strengthenArmor);
				op.getInventory().setHelmet(helmet);
			}

			ItemStack chestplate = op.getInventory().getChestplate();
			if (chestplate != null && !chestplate.getType().equals(Material.AIR))
			{
				chestplate.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, teamData.strengthenArmor);
				op.getInventory().setChestplate(chestplate);
			}

			ItemStack leggings = op.getInventory().getLeggings();
			if (leggings != null && !leggings.getType().equals(Material.AIR))
			{
				leggings.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, teamData.strengthenArmor);
				op.getInventory().setLeggings(leggings);
			}

			ItemStack boots = op.getInventory().getBoots();
			if (boots != null && !boots.getType().equals(Material.AIR))
			{
				boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, teamData.strengthenArmor);
				op.getInventory().setBoots(boots);
			}

			op.updateInventory();
		});

		player.getTeam().getOnlinePlayers().forEach(op -> op.playSound(Sound.ANVIL_USE));
		player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.improvearmor", player.getName(), teamData.strengthenArmor));
	}	

	private static void speedMining(BadblockPlayer player, CustomItemAction action, String actionData) {
		if (player.getTeam() == null)
		{
			return;
		}

		BedWarsTeamData teamData = player.getTeam().teamData(BedWarsTeamData.class);
		if (teamData.speedMining >= 3)
		{
			player.sendTranslatedMessage("bedwars.alreadymaxmininglevel");
			return;
		}

		String[] levelSplitter = actionData.split(";");

		String rawExchanger = levelSplitter[teamData.speedMining];

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

		player.getTeam().getOnlinePlayers().forEach(op -> op.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, teamData.speedMining - 1)));
		player.getTeam().getOnlinePlayers().forEach(op -> op.playSound(Sound.ANVIL_USE));
		player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.improvespeedmining", player.getName(), teamData.speedMining));
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
		player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.improvetrespassing", player.getName()));
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

		String rawExchanger = levelSplitter[0];

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

		player.getTeam().getOnlinePlayers().forEach(op -> op.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, teamData.speed - 1)));
		player.getTeam().getOnlinePlayers().forEach(op -> op.playSound(Sound.ANVIL_USE));
		player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.improvespeed", player.getName(), teamData.speed));
	}

	private static void slowDig(BadblockPlayer player, CustomItemAction action, String actionData) {
		if (player.getTeam() == null)
		{
			return;
		}

		BedWarsTeamData teamData = player.getTeam().teamData(BedWarsTeamData.class);
		if (teamData.slowDig > System.currentTimeMillis())
		{
			player.sendTranslatedMessage("bedwars.alreadymaxslowdiglevel");
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

	private static void sharpness(BadblockPlayer player, CustomItemAction action, String actionData) {
		if (player.getTeam() == null)
		{
			return;
		}

		BedWarsTeamData teamData = player.getTeam().teamData(BedWarsTeamData.class);
		if (teamData.sharpness >= 1)
		{
			player.sendTranslatedMessage("bedwars.alreadymaxsharpnesslevel");
			return;
		}

		String[] levelSplitter = actionData.split(";");

		String rawExchanger = levelSplitter[0];

		ExchangeObject exchanger = ExchangeObject.toExchange(rawExchanger);

		// If the player has the needed items
		if (!player.getInventory().contains(exchanger.getMaterial(), exchanger.getAmount()))
		{
			TranslatableWord word = GameMessages.material(exchanger.getMaterial(), exchanger.getAmount() > 1, WordDeterminant.UNDEFINED);
			player.sendTranslatedMessage("bedwars.youmusthavetoimprovesharpness", exchanger.getAmount(), word.getWord(player.getPlayerData().getLocale()));
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

		teamData.sharpness++;

		player.getTeam().getOnlinePlayers().forEach(op ->
		{
			for (int i = 0; i < op.getInventory().getContents().length; i++)
			{
				ItemStack content = op.getInventory().getContents()[i];
				if (content.getType().name().equalsIgnoreCase("sword"))
				{
					content.removeEnchantment(Enchantment.DAMAGE_ALL);
					content.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, teamData.sharpness);
				}
			}
		});

		player.getTeam().getOnlinePlayers().forEach(op -> op.playSound(Sound.ANVIL_USE));
		player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.improvesharpness", player.getName(), teamData.strength));
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

		String rawExchanger = levelSplitter[0];

		ExchangeObject exchanger = ExchangeObject.toExchange(rawExchanger);

		// If the player has the needed items
		if (!player.getInventory().contains(exchanger.getMaterial(), exchanger.getAmount()))
		{
			TranslatableWord word = GameMessages.material(exchanger.getMaterial(), exchanger.getAmount() > 1, WordDeterminant.UNDEFINED);
			player.sendTranslatedMessage("bedwars.youmusthavetoimproveprotection", exchanger.getAmount(), word.getWord(player.getPlayerData().getLocale()));
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

		player.getTeam().getOnlinePlayers().forEach(op -> op.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, teamData.protection - 1)));
		player.getTeam().getOnlinePlayers().forEach(op -> op.playSound(Sound.ANVIL_USE));
		player.getTeam().getOnlinePlayers().forEach(op -> op.sendTranslatedMessage("bedwars.improveprotection", player.getName(), teamData.protection));
	}

	@SuppressWarnings("deprecation")
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

			if (Material.COMPASS.equals(itemStack.getType()))
			{
				itemStack = ItemLoader.fakeEnchant(itemStack);
				ItemMeta itemMeta = itemStack.getItemMeta();
				
				itemMeta.setDisplayName("§aTracker");
				itemStack.setItemMeta(itemMeta);
			}
			
			if (itemStack.getType() != null && itemStack.getType().equals(Material.WOOL))
			{
				BadblockTeam team = player.getTeam();
				if (team != null)
				{
					itemStack = new ItemStack(exchangeObject.getMaterial(), exchangeObject.getAmount(), team.getDyeColor().getWoolData());
				}
			}

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

				if (player.getTeam() != null)
				{
					BadblockTeam team = player.getTeam();
					BedWarsTeamData td = team.teamData(BedWarsTeamData.class);
					if (td != null)
					{
						if (td.strengthenArmor > 0)
						{
							itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, td.strengthenArmor - 1);
						}
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

				if (player.getTeam() != null)
				{
					BadblockTeam team = player.getTeam();
					BedWarsTeamData td = team.teamData(BedWarsTeamData.class);
					if (td != null)
					{
						if (td.strengthenArmor > 0)
						{
							itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, td.strengthenArmor - 1);
						}
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

				if (player.getTeam() != null)
				{
					BadblockTeam team = player.getTeam();
					BedWarsTeamData td = team.teamData(BedWarsTeamData.class);
					if (td != null)
					{
						if (td.strengthenArmor > 0)
						{
							itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, td.strengthenArmor - 1);
						}
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

				if (player.getTeam() != null)
				{
					BadblockTeam team = player.getTeam();
					BedWarsTeamData td = team.teamData(BedWarsTeamData.class);
					if (td != null)
					{
						if (td.strengthenArmor > 0)
						{
							itemStack.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, td.strengthenArmor);
						}
					}
				}

				player.getInventory().setBoots(itemStack);
			}
			else if (itemStack.getType().name().contains("SWORD"))
			{

				if (player.getTeam() != null)
				{
					BadblockTeam team = player.getTeam();
					BedWarsTeamData td = team.teamData(BedWarsTeamData.class);
					if (td != null)
					{
						if (td.sharpness > 0)
						{
							itemStack.addUnsafeEnchantment(Enchantment.DAMAGE_ALL, td.sharpness);
						}
					}
				}

				player.getInventory().addItem(itemStack);
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
