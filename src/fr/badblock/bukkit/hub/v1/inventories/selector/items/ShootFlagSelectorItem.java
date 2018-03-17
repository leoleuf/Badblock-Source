package fr.badblock.bukkit.hub.v1.inventories.selector.items;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.gson.Gson;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.inventories.abstracts.actions.ItemAction;
import fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.boosters.inventories.RealTimeBoosterManager;
import fr.badblock.bukkit.hub.v1.inventories.settings.settings.LightBlueStainedGlassPaneItem;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.data.PlayerData;
import fr.badblock.gameapi.players.data.boosters.PlayerBooster;
import fr.badblock.gameapi.run.BadblockGame;
import fr.badblock.gameapi.utils.general.TimeUnit;
import fr.badblock.gameapi.utils.i18n.Locale;
import fr.badblock.gameapi.utils.itemstack.ItemStackUtils;
import fr.badblock.rabbitconnector.RabbitPacketType;
import fr.badblock.rabbitconnector.RabbitService;
import fr.badblock.sentry.SEntry;
import fr.badblock.utils.Encodage;

public class ShootFlagSelectorItem extends GameSelectorItem
{

	public ShootFlagSelectorItem()
	{
		super("hub.items.shootflagselectoritem", Material.CHEST, "hub.items.shootflagselectoritem.lore");
		this.setFakeEnchantment(true);
	}

	/**
	 * Temp access
	 */
	@Override
	public ItemStack toItemStack(BadblockPlayer player)
	{
		PlayerData playerData = player.getPlayerData();
		Locale locale = playerData.getLocale();
		
		// ShootFlag access
		if (!playerData.isTempAccess())
		{
			return new LightBlueStainedGlassPaneItem().toItemStack(player);
		}
		
		// All the work
		
		ItemStack itemStack = new ItemStack(this.getMaterial(), this.getAmount(), this.getData());
		if (this.isFakeEnchantment()) {
			itemStack = ItemStackUtils.fakeEnchant(itemStack);
		}
		ItemMeta itemMeta = itemStack.getItemMeta();
		
		String addedString = "";
		
		List<Entry<String, Integer>> list = entriesSortedByValues(inGamePlayers);
		Iterator<Entry<String, Integer>> iterator = list.iterator();
		
		for (int i = 0; i < 3; i++)
		{
			if (!iterator.hasNext())
			{
				break;
			}
			
			Entry<String, Integer> entry = iterator.next();
			if (entry.getKey().equalsIgnoreCase(this.getGamePrefix()))
			{
				addedString = GameAPI.i18n().get(locale, "hub.items.populargame")[0];
				break;
			}
		}
		
		itemMeta.setDisplayName(GameAPI.i18n().get(locale, this.getName(), addedString)[0]);
		
		if (this.getLore() != null && !this.getLore().isEmpty()) {
			String boosterLore = GameAPI.i18n().get(locale, "hub.items.booster.nobooster")[0]/*"§cAucun booster activé, on en a pas parlé avant."*/;
			if (RealTimeBoosterManager.stockage.containsKey(this.getGamePrefix())) {
				PlayerBooster playerBooster = RealTimeBoosterManager.stockage.get(this.getGamePrefix());
				if (playerBooster.isEnabled() && playerBooster.isValid()) {
					boosterLore = GameAPI.i18n().get(locale, "hub.items.booster.boost", playerBooster.getUsername(), (int) ((playerBooster.getBooster().getCoinsMultiplier() - 1) * 100), (int) ((playerBooster.getBooster().getXpMultiplier() - 1) * 100), TimeUnit.SECOND.toShort((playerBooster.getExpire() / 1000L) - (System.currentTimeMillis() / 1000L)))[0]; 
				}
			}
			itemMeta.setLore(Arrays.asList(GameAPI.i18n().get(locale, this.getLore(), inGamePlayers.get(getGamePrefix()), waitingLinePlayers.get(getGamePrefix()),
					(this.getGame() != null ? this.getGame().getDeveloper() : ""), boosterLore)));
		}
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}
	
	@Override
	public List<ItemAction> getActions() {
		return Arrays.asList(ItemAction.INVENTORY_DROP, ItemAction.INVENTORY_LEFT_CLICK, ItemAction.INVENTORY_RIGHT_CLICK, ItemAction.INVENTORY_WHEEL_CLICK);
	}

	@Override
	public List<String> getGames() {
		return Arrays.asList("shootflag4x4");
	}

	@Override
	public void onClick(BadblockPlayer player, ItemAction itemAction, Block clickedBlock)
	{
		PlayerData playerData = player.getPlayerData();
		
		// ShootFlag access
		if (!playerData.isTempAccess())
		{
			return;
		}
		
		player.closeInventory();
		BadBlockHub instance = BadBlockHub.getInstance();
		RabbitService service = instance.getRabbitService();
		Gson gson = instance.getGson();
		service.sendAsyncPacket("networkdocker.sentry.join", gson.toJson(new SEntry(HubPlayer.getRealName(player), getGames().get(0), false)), Encodage.UTF8, RabbitPacketType.PUBLISHER, 5000, false);
	}

	@Override
	public BadblockGame getGame() {
		return BadblockGame.SHOOTFLAG;
	}

	@Override
	public boolean isMiniGame() {
		return true;
	}

	@Override
	public String getGamePrefix() {
		return "shootflag4x4";
	}

}
