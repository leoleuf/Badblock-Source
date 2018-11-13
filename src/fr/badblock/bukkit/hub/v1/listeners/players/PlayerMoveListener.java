package fr.badblock.bukkit.hub.v1.listeners.players;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import fr.badblock.bukkit.hub.v1.BadBlockHub;
import fr.badblock.bukkit.hub.v1.listeners._HubListener;
import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.bukkit.hub.v1.objects.HubStoredPlayer;
import fr.badblock.gameapi.players.BadblockPlayer;
import fr.badblock.gameapi.utils.ConfigUtils;

public class PlayerMoveListener extends _HubListener {

	public static Location spawn = ConfigUtils.getLocation(BadBlockHub.getInstance(), "worldspawn");

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		BadblockPlayer player = (BadblockPlayer) event.getPlayer();
		HubPlayer hubPlayer = HubPlayer.get(player);
		HubStoredPlayer hubStoredPlayer = HubStoredPlayer.get(player);
		hubStoredPlayer.lastLocation = ConfigUtils.convertLocationToString(event.getTo());
		hubStoredPlayer.maxLastLocationTime = System.currentTimeMillis() + 600_000L;
		// Freeze des coffres
		if (hubPlayer.isChestFreeze()) {
			event.setTo(hubPlayer.getChestFreezeLocation());
			return;
		}
		Location from = event.getFrom();
		Location to = event.getTo();
		if ((from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ()) && (from.getPitch() != to.getPitch() || from.getYaw() != to.getYaw())) {
			hubPlayer.lastMove = Integer.MAX_VALUE;
		}
		Location underOne = to.clone().add(0, -1, 0);
		Location underTwo = to.clone().add(0, -2, 0);
		Block blockOne = underOne.getBlock();
		Block blockTwo = underTwo.getBlock();
		BadBlockHub hub = BadBlockHub.getInstance();
		if ((blockOne.getType().equals(Material.WOOL) && blockOne.getData() == 10)
				|| (blockTwo.getType().equals(Material.WOOL) && blockTwo.getData() == 10)) {
			if (hubPlayer.getTimeBetweenEachVelocityUsage() <= System.currentTimeMillis()) {
				if (hubPlayer.mountEntity != null && hubPlayer.mountEntity.isValid()) {
					try {
						hubPlayer.mountEntity.setVelocity(player.getLocation().getDirection().multiply(9));
						hubPlayer.mountEntity
						.setVelocity(new Vector(player.getVelocity().getX(), 3.0D, player.getVelocity().getZ()));
					}catch(Exception error) {

					}
				}
				player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 100F, 1F);

				try {
					player.setVelocity(player.getLocation().getDirection().multiply(9));
				} catch(Exception e){
					try {
						player.setVelocity(player.getLocation().getDirection().multiply(4));
					}catch(Exception error) {

					}
				}

				player.setVelocity(new Vector(player.getVelocity().getX(), 3.0D, player.getVelocity().getZ()));
				hubPlayer.justGetVelocity();
			}
		}else{
			if (hubPlayer.isVelocity()) {
				hubPlayer.setVelocity(false);
				hubPlayer.setTimeBetweenEachVelocityUsage(System.currentTimeMillis() + 15000L);
			}
		}

		if (hub.getCuboid() != null && !hub.getCuboid().isInSelection(to) && !player.hasAdminMode()) {
			Location playerCenterLocation = ConfigUtils.getLocation(hub, "worldspawn");
			double x = playerCenterLocation.getX() - to.getX();
			double y = playerCenterLocation.getY() - to.getY();
			double z = playerCenterLocation.getZ() - to.getZ();
			try {
				Vector throwVector = new Vector(x, y, z);
				throwVector.normalize();
				throwVector.multiply(2);
				player.setVelocity(throwVector);
			}catch(Exception error) {

			}
			player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 100F, 2F);
			if (hubPlayer.getLastGetAwaySendMessage() < System.currentTimeMillis()) {
				player.sendTranslatedMessage("hub.error.youcantgetawayfromthemap");
				hubPlayer.setLastGetAwaySendMessage(System.currentTimeMillis() + 30_000L);
			}
		}
		if (hub.getVipPortalCuboid() != null && hub.getVipPortalCuboid().isInSelection(to)
				&& !player.hasPermission("hub.vipzone")) {
			player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 100F, 2F);
			hub.getNpcxMalware().setHeadYaw(hub.getNpcxMalwareTurnBack().getYaw());
			hub.getNpcLeLanN().setHeadYaw(hub.getNpcLeLanNTurnBack().getYaw());
			hub.getVipPortalCuboid().getBlocks().parallelStream().filter(block -> block.getType().equals(Material.AIR))
			.forEach(block -> player.sendBlockChange(block.getLocation(), Material.getMaterial(17), (byte) 1));
			hubPlayer.setLastVipCuboid(System.currentTimeMillis() + 2500);
			event.setTo(ConfigUtils.getLocation(BadBlockHub.getInstance(), "vipzone"));
			if (hubPlayer.getLastSendVipMessage() < System.currentTimeMillis()) {
				player.sendTranslatedMessage("hub.items.vipzoneselectoritem.neededpermission");
				hubPlayer.setLastSendVipMessage(System.currentTimeMillis() + 30_000L);
			}
		}
		if (hub.getVipPushCuboid() != null && !hub.getVipPushCuboid().isInSelection(event.getFrom()) && hub.getVipPushCuboid().isInSelection(to)
				&& !player.hasPermission("hub.vipzone")) {
			Location playerCenterLocation = spawn;
			double x = playerCenterLocation.getX() - to.getX();
			double y = playerCenterLocation.getY() - to.getY();
			double z = playerCenterLocation.getZ() - to.getZ();
			try {
				Vector throwVector = new Vector(x, y, z);
				throwVector.normalize();
				throwVector.multiply(2);
				player.setVelocity(throwVector);
			}catch(Exception error) {

			}
			player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 100F, 2F);
			hub.getNpcxMalware().setHeadYaw(hub.getNpcxMalwareTurnBack().getYaw());
			hub.getNpcLeLanN().setHeadYaw(hub.getNpcLeLanNTurnBack().getYaw());
			HubPlayer.get(player).setLastVipCuboid(System.currentTimeMillis() + 2500);
			hub.getVipPortalCuboid().getBlocks().parallelStream().filter(block -> block.getType().equals(Material.AIR))
			.forEach(block -> player.sendBlockChange(block.getLocation(), Material.getMaterial(17), (byte) 1));
			if (hubPlayer.getLastSendVipMessage() < System.currentTimeMillis()) {
				player.sendTranslatedMessage("hub.items.vipzoneselectoritem.neededpermission");
				hubPlayer.setLastSendVipMessage(System.currentTimeMillis() + 30_000L);
			}
		}else if (hub.getVipPushCuboid() != null && hub.getVipPushCuboid().isInSelection(event.getFrom()) && hub.getVipPushCuboid().isInSelection(to)
				&& !player.hasPermission("hub.vipzone")) {
			event.setTo(ConfigUtils.getLocation(BadBlockHub.getInstance(), "vipzone"));
			player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 100F, 2F);
			HubPlayer.get(player).setLastVipCuboid(System.currentTimeMillis() + 2500);
			hub.getVipPortalCuboid().getBlocks().parallelStream().filter(block -> block.getType().equals(Material.AIR))
			.forEach(block -> player.sendBlockChange(block.getLocation(), Material.getMaterial(17), (byte) 1));
			if (hubPlayer.getLastSendVipMessage() < System.currentTimeMillis()) {
				player.sendTranslatedMessage("hub.items.vipzoneselectoritem.neededpermission");
				hubPlayer.setLastSendVipMessage(System.currentTimeMillis() + 30_000L);
			}
		}

		if (to.getY() <= 0)
			event.setTo(spawn);
	}

}
