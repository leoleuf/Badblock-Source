package fr.badblock.bukkit.hub.v2.inventories.actions;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import fr.badblock.bukkit.hub.v2.inventories.objects.CustomItemAction;
import fr.badblock.bukkit.hub.v2.inventories.objects.CustomItemActionType;
import fr.badblock.bukkit.hub.v2.players.HubStoredPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.players.BadblockPlayer.BadblockMode;
import fr.badblock.gameapi.utils.BukkitUtils;

public class ItemActionTogglePlayers extends CustomItemAction
{

	@Override
	public void execute(BadblockPlayer player, CustomItemActionType action, String actionData)
	{
		HubStoredPlayer hubStoredPlayer = HubStoredPlayer.get(player);
		
		if (hubStoredPlayer == null)
		{
			return;
		}
		
		player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 9));
		player.playSound(Sound.EXPLODE);
		
		if (hubStoredPlayer.isHidePlayers())
		{
			for (BadblockPlayer otherPlayer : BukkitUtils.getAllPlayers())
			{
				if (!otherPlayer.getBadblockMode().equals(BadblockMode.PLAYER))
				{
					continue;
				}
				
				if (otherPlayer.getGameMode().equals(GameMode.SPECTATOR))
				{
					continue;
				}
				
				player.showPlayer(otherPlayer);
			}
			
			hubStoredPlayer.setHidePlayers(false);
			player.sendTranslatedMessage("hub.toggleplayers.show");
		}
		else
		{
			for (BadblockPlayer otherPlayer : BukkitUtils.getAllPlayers())
			{
				player.hidePlayer(otherPlayer);
			}
			
			hubStoredPlayer.setHidePlayers(true);
			player.sendTranslatedMessage("hub.toggleplayers.hide");
		}
	}

}
