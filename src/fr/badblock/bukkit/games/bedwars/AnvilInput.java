package fr.badblock.bukkit.games.bedwars;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Objet permettant de créer des fausses enclumes
 * où l'on peut saisir un texte en Reflection
 *
 * @author RedSpri
 */
@SuppressWarnings("ALL")
@Getter
public class AnvilInput {

    @AllArgsConstructor@Getter
    public enum AnvilInputSlot {
        SLOT_LEFT(0), SLOT_MIDDLE(1), SLOT_RIGHT(2);
        private int slot;
        public static AnvilInputSlot bySlot(int slot) {
            for (AnvilInputSlot anvilSlot : values()) if (anvilSlot.getSlot() == slot) return anvilSlot;
            return null;
        }
    }

    @SuppressWarnings("WeakerAccess")
    @Getter@AllArgsConstructor
    public class AnvilInputClickEvent {
        private AnvilInputSlot slot;
        private String itemname;
        private InventoryClickEvent event;
        private Player player;
        @Setter
        private boolean closing;
    }

    @Getter@AllArgsConstructor
    public class AnvilInputCloseEvent {
        private InventoryCloseEvent event;
        private boolean planned;
        private Player player;
    }

    private final Player player;
    private Map<AnvilInputSlot, ItemStack> items = Maps.newHashMap();
    private Inventory inventory;
    private Listener listener;
    private boolean closing = false;

    private Consumer<AnvilInputClickEvent> clickConsumer;
    public AnvilInput setClickConsumer(Consumer<AnvilInputClickEvent> clickConsumer) {
        this.clickConsumer = clickConsumer;
        return this;
    }

    private Consumer<AnvilInputCloseEvent> closeConsumer;
    public AnvilInput setCloseConsumer(Consumer<AnvilInputCloseEvent> closeConsumer) {
        this.closeConsumer = closeConsumer;
        return this;
    }

    public AnvilInput(Plugin plugin, Player player) {
        this.player = player;
        this.listener = new Listener() {

            @EventHandler
            public void onInventoryClick(final InventoryClickEvent e) {
                if (e.getWhoClicked() instanceof Player) {
                    Player clicker = (Player) e.getWhoClicked();
                    if (e.getInventory().equals(inventory)) {
                        e.setCancelled(true);
                        ItemStack item = e.getCurrentItem();
                        int slot = e.getRawSlot();
                        String name = "";
                        if (item != null) {
                            if (item.hasItemMeta()) {
                                ItemMeta meta = item.getItemMeta();
                                if (meta.hasDisplayName()) name = meta.getDisplayName();
                            }
                        }
                        AnvilInputClickEvent clickEvent = new AnvilInputClickEvent(AnvilInputSlot.bySlot(slot), name, e, clicker, true);
                        if (clickConsumer != null) clickConsumer.accept(clickEvent);
                        if (clickEvent.isClosing()) {
                            closing = true;
                            Bukkit.getScheduler().runTask(plugin, () -> e.getWhoClicked().closeInventory());
                        }
                        if (clickEvent.isClosing()) destroy();
                    }
                }
            }

            @EventHandler(priority = EventPriority.HIGHEST)
            public void onInventoryClose(InventoryCloseEvent e) {
                if (e.getPlayer() instanceof Player) {
                    Player closer = (Player) e.getPlayer();
                    Inventory inventory = e.getInventory();
                    if (inventory.equals(getInventory())) {
                        AnvilInputCloseEvent closeEvent = new AnvilInputCloseEvent(e, closing, closer);
                        if (closeConsumer != null) closeConsumer.accept(closeEvent);
                        destroy();
                    }
                }
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent e) {
                if (e.getPlayer().equals(player)) destroy();
            }
        };
        Bukkit.getPluginManager().registerEvents(getListener(), plugin);
    }

    private void destroy() {
        inventory.clear();
        HandlerList.unregisterAll(listener);
    }

    public AnvilInput setItem(AnvilInputSlot slot, ItemStack item) {
        items.put(slot, item);
        return this;
    }
    private String getBukkitVersion() {
        String name = Bukkit.getServer().getClass().getPackage().getName();
        return name.substring(name.lastIndexOf(46) + 1) + ".";
    }

    private Class<?> getNMSClass(String className) {
        try {
            return Class.forName("net.minecraft.server." + getBukkitVersion() + className);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void open() {
        try {
            Object CraftPlayerCast = Class.forName("org.bukkit.craftbukkit." + getBukkitVersion() + "entity.CraftPlayer").cast(getPlayer());
            Object EntityPlayerReflect = CraftPlayerCast.getClass().getMethod("getHandle").invoke(CraftPlayerCast);
            Field EntityPlayerInventoryField = EntityPlayerReflect.getClass().getField("inventory");
            if (!EntityPlayerInventoryField.isAccessible()) EntityPlayerInventoryField.setAccessible(true);
            Object EntityPlayerInventoryReflect = EntityPlayerInventoryField.get(EntityPlayerReflect);
            Field EntityPlayerWorldField = EntityPlayerReflect.getClass().getField("world");
            if (!EntityPlayerWorldField.isAccessible()) EntityPlayerWorldField.setAccessible(true);
            Object EntityPlayerWorldReflect = EntityPlayerWorldField.get(EntityPlayerReflect);
            Object BlockPositionReflect = getNMSClass("BlockPosition").getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(0, 1, 0);
            Object ContainerAnvilReflect = getNMSClass("ContainerAnvil").getConstructor(EntityPlayerInventoryReflect.getClass(), getNMSClass("World"), BlockPositionReflect.getClass(), getNMSClass("EntityHuman")).newInstance(EntityPlayerInventoryReflect, EntityPlayerWorldReflect, BlockPositionReflect, EntityPlayerReflect);
            Field CheckReachableField = getNMSClass("Container").getField("checkReachable");
            if (!CheckReachableField.isAccessible()) CheckReachableField.setAccessible(true);
            CheckReachableField.set(ContainerAnvilReflect, false);
            Object BukkitViewReflect = ContainerAnvilReflect.getClass().getMethod("getBukkitView").invoke(ContainerAnvilReflect);
            inventory = (Inventory) BukkitViewReflect.getClass().getMethod("getTopInventory").invoke(BukkitViewReflect);
            items.keySet().forEach(slot -> inventory.setItem(slot.getSlot(), items.get(slot)));
            int ContainerIdReflect = (int) EntityPlayerReflect.getClass().getMethod("nextContainerCounter").invoke(EntityPlayerReflect);
            Object ChatMessageReflect = getNMSClass("ChatMessage").getConstructor(String.class, Object[].class).newInstance("Repairing", new Object[]{});
            Object PacketPlayOutOpenWindowReflect = getNMSClass("PacketPlayOutOpenWindow").getConstructor(Integer.TYPE, String.class, getNMSClass("IChatBaseComponent"), Integer.TYPE).newInstance(ContainerIdReflect, "minecraft:anvil", ChatMessageReflect, 0);
            Field PlayerConnectionField = EntityPlayerReflect.getClass().getField("playerConnection");
            if (!PlayerConnectionField.isAccessible()) PlayerConnectionField.setAccessible(true);
            Object PlayerConnectionReflect = PlayerConnectionField.get(EntityPlayerReflect);
            Method SendPacketMethod = getNMSClass("PlayerConnection").getMethod("sendPacket", getNMSClass("Packet"));
            if (!SendPacketMethod.isAccessible()) SendPacketMethod.setAccessible(true);
            SendPacketMethod.invoke(PlayerConnectionReflect, PacketPlayOutOpenWindowReflect);
            Field ActiveContainerField = getNMSClass("EntityHuman").getDeclaredField("activeContainer");
            if (!ActiveContainerField.isAccessible()) ActiveContainerField.setAccessible(true);
            ActiveContainerField.set(EntityPlayerReflect, ContainerAnvilReflect);
            Field WindowIdField = getNMSClass("Container").getField("windowId");
            if (!WindowIdField.isAccessible()) WindowIdField.setAccessible(true);
            WindowIdField.set(ActiveContainerField.get(EntityPlayerReflect), ContainerIdReflect);
            Method AddSlotListenerMethod = getNMSClass("Container").getMethod("addSlotListener", getNMSClass("ICrafting"));
            if (!AddSlotListenerMethod.isAccessible()) AddSlotListenerMethod.setAccessible(true);
            AddSlotListenerMethod.invoke(ContainerAnvilReflect, EntityPlayerReflect);
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException | NoSuchFieldException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
