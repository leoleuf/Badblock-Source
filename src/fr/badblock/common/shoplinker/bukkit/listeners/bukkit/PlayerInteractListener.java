package fr.badblock.common.shoplinker.bukkit.listeners.bukkit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import fr.badblock.common.shoplinker.bukkit.inventories.BukkitInventories;
import fr.badblock.common.shoplinker.bukkit.signs.SignManager;
import fr.badblock.common.shoplinker.bukkit.signs.SignObject;

public class PlayerInteractListener implements Listener {

	private Action[]       		allowedActions              = new Action[] {
			Action.LEFT_CLICK_BLOCK,
			Action.RIGHT_CLICK_BLOCK
	};
	
	private static Material[]   supportedMaterials          = new Material[] {
			Material.SIGN,
			Material.SIGN_POST,
			Material.WALL_SIGN
	};

	private List<Action>         allowedActionList     = Arrays.asList(allowedActions);
	public static Set<Material>  supportedMaterialList = new HashSet<>(Arrays.asList(supportedMaterials));
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Action action = event.getAction();
		// Unsupported action
		if (!allowedActionList.contains(action)) return;
		Block clickedBlock = event.getClickedBlock();
		// Not clicked block
		if (clickedBlock == null) return;
		// Unsupported material
		if (!supportedMaterialList.contains(clickedBlock.getType())) return;
		Sign sign = (Sign) clickedBlock.getState();
		Location signLocation = sign.getLocation();
		SignManager signManager = SignManager.getInstance();
		SignObject signObject = signManager.getSign(signLocation);
		// Not registered sign
		if (signObject == null) return;
		// Open inventory
		BukkitInventories.openInventory(player, signObject.getInventoryName());
	}

}