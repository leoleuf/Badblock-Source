package fr.badblock.bukkit.hub.v2.games.gladiators.events;

import fr.badblock.bukkit.hub.v2.games.gladiators.GladiatorManager;
import fr.badblock.bukkit.hub.v2.games.gladiators.maps.Map;
import fr.badblock.bukkit.hub.v2.games.gladiators.maps.MapManager;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class GladiatorInteract implements Listener {

    @EventHandler
    public void onInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block block = event.getClickedBlock();

        if(action == Action.RIGHT_CLICK_BLOCK && (block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN)){
            Sign sign = (Sign) block.getState();

            if(sign.getLine(0).equals("§c[Gladiators]")){
                Map map = MapManager.get().getMap(ChatColor.stripColor(sign.getLine(1)));

                if(!map.giveKit(player)){
                    player.sendMessage(GladiatorManager.GLADIATOR_PREFIX+"Le kit n'a pas été configuré !");
                    return;
                }

                if(!map.getSignLocations().contains(sign)){
                    map.getSignLocations().add(sign);
                }

                player.sendMessage(GladiatorManager.GLADIATOR_PREFIX+"Vous entrez dans l'arène §c"+map.getName());
                map.getPlayers().add(player);
                player.teleport(map.getRandomLoc());

                TextComponent tc = new TextComponent(GladiatorManager.GLADIATOR_PREFIX + "§c[Quittez le gladiator]");
                tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/quitgladiator"));
                player.spigot().sendMessage(tc);

                for(Sign sign1 : map.getSignLocations()){
                    sign1.setLine(3, "§8"+map.getPlayers().size());
                    sign1.update();
                }
            }
        }

    }

}