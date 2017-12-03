package fr.badblock.bukkit.games.bedwars;

import com.google.common.collect.Maps;
import fr.badblock.gameapi.BadListener;
import fr.badblock.gameapi.GameAPI;
import fr.badblock.gameapi.utils.reflection.ReflectionUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public class AnvilInput {

    class AnvilInputEvent {}

    @AllArgsConstructor@Getter
    public enum AnvilInputSlot {
        SLOT_LEFT(0), SLOT_MIDDLE(1), SLOT_RIGHT(2);
        private int slot;
        public static AnvilInputSlot bySlot(int slot) {
            for (AnvilInputSlot anvilSlot : values()) if (anvilSlot.getSlot() == slot) return anvilSlot;
            return null;
        }
    }

    interface AnvilInputConsumer<T extends AnvilInputEvent> {
        void work(T event);
    }

    @Getter@AllArgsConstructor
    class AnvilInputClickEvent extends AnvilInputEvent {
        private AnvilInputSlot slot;
        private String name;
        private InventoryClickEvent event;
        @Setter
        private boolean close;
    }

    @Getter@AllArgsConstructor
    class AnvilInputCloseEvent extends AnvilInputEvent {
        private InventoryCloseEvent event;
        @Setter
        private boolean planned;
    }

    private final Player player;
    private Map<AnvilInputSlot, ItemStack> items = Maps.newHashMap();
    private Inventory inventory;
    private BadListener listener;
    private boolean closing = false;

    @Setter
    private AnvilInputConsumer<AnvilInputClickEvent> clickConsumer;
    @Setter
    private AnvilInputConsumer<AnvilInputCloseEvent> closeConsumer;

    public AnvilInput(Player player) {
        this.player = player;
        this.listener = new BadListener() {

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
                        AnvilInputClickEvent clickEvent = new AnvilInputClickEvent(AnvilInputSlot.bySlot(slot), name, e, true);
                        if (clickConsumer != null) clickConsumer.work(clickEvent);
                        if (clickEvent.isClose()) {
                            closing = true;
                            Bukkit.getScheduler().runTask(GameAPI.getAPI(), () -> e.getWhoClicked().closeInventory());
                        }
                        if (clickEvent.isClose()) destroy();
                    }
                }
            }

            @EventHandler(priority = EventPriority.HIGHEST)
            public void onInventoryClose(InventoryCloseEvent e) {
                if (e.getPlayer() instanceof Player) {
                    Inventory inventory = e.getInventory();
                    if (inventory.equals(getInventory())) {
                        AnvilInputCloseEvent closeEvent = new AnvilInputCloseEvent(e, closing);
                        if (closeConsumer != null) closeConsumer.work(closeEvent);
                        destroy();
                    }
                }
            }

            @EventHandler
            public void onPlayerQuit(PlayerQuitEvent e) {
                if (e.getPlayer().equals(player)) destroy();
            }
        };
    }

    private void destroy() {
        inventory.clear();
        HandlerList.unregisterAll(listener);
    }

    public AnvilInput setItem(AnvilInputSlot slot, ItemStack item) {
        items.put(slot, item);
        return this;
    }

    public void open() {
        try {
            Object CraftPlayerCast = ReflectionUtils.getOBCClass("entity.CraftPlayer").cast(getPlayer());
            Object EntityPlayerReflect = CraftPlayerCast.getClass().getMethod("getHandle").invoke(CraftPlayerCast);
            Field PlayerConnectionField = EntityPlayerReflect.getClass().getField("playerConnection");
            if (!PlayerConnectionField.isAccessible()) PlayerConnectionField.setAccessible(true);
            Object PlayerConnectionReflect = PlayerConnectionField.get(EntityPlayerReflect);
            Method SendPacketMethod = ReflectionUtils.getNMSClass("PlayerConnection").getMethod("sendPacket", ReflectionUtils.getNMSClass("Packet"));
            Field EntityPlayerInventoryField = EntityPlayerReflect.getClass().getField("inventory");
            if (!EntityPlayerInventoryField.isAccessible()) EntityPlayerInventoryField.setAccessible(true);
            Object EntityPlayerInventoryReflect = EntityPlayerInventoryField.get(EntityPlayerReflect);
            Field EntityPlayerWorldField = EntityPlayerReflect.getClass().getField("world");
            if (!EntityPlayerWorldField.isAccessible()) EntityPlayerWorldField.setAccessible(true);
            Object EntityPlayerWorldReflect = EntityPlayerWorldField.get(EntityPlayerReflect);
            Object BlockPositionReflect = ReflectionUtils.getNMSClass("BlockPosition").getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(0, 0, 0);
            Object ContainerAnvilReflect = ReflectionUtils.getNMSClass("ContainerAnvil").getConstructor(EntityPlayerInventoryReflect.getClass(), ReflectionUtils.getNMSClass("World"), BlockPositionReflect.getClass(), ReflectionUtils.getNMSClass("EntityHuman")).newInstance(EntityPlayerInventoryReflect, EntityPlayerWorldReflect, BlockPositionReflect, EntityPlayerReflect);
            Object BukkitViewReflect = ContainerAnvilReflect.getClass().getMethod("getBukkitView").invoke(ContainerAnvilReflect);
            inventory = (Inventory) BukkitViewReflect.getClass().getMethod("getTopInventory").invoke(BukkitViewReflect);
            items.keySet().forEach(slot -> inventory.setItem(slot.getSlot(), items.get(slot)));
            int ContainerIdReflect = (int) EntityPlayerReflect.getClass().getMethod("nextContainerCounter").invoke( EntityPlayerReflect);
            Field WindowIdField = ReflectionUtils.getNMSClass("Container").getField("windowId");
            if (!WindowIdField.isAccessible()) WindowIdField.setAccessible(true);
            WindowIdField.set(ContainerAnvilReflect, Integer.valueOf(Integer.toString(ContainerIdReflect)));
            ReflectionUtils.getNMSClass("Container").getMethod("addSlotListener", ReflectionUtils.getNMSClass("ICrafting")).invoke(ContainerAnvilReflect, EntityPlayerReflect);
            Field ActiveContainerField = ReflectionUtils.getNMSClass("EntityHuman").getDeclaredField("activeContainer");
            if (!ActiveContainerField.isAccessible()) ActiveContainerField.setAccessible(true);
            ActiveContainerField.set(EntityPlayerReflect, ContainerAnvilReflect);
            Object ChatMessageReflect = ReflectionUtils.getNMSClass("ChatMessage").getConstructor(String.class, Object[].class).newInstance("Repairing", new Object[]{});
            Object PacketPlayOutOpenWindowReflect = ReflectionUtils.getNMSClass("PacketPlayOutOpenWindow").getConstructor(Integer.TYPE, String.class, ReflectionUtils.getNMSClass("IChatBaseComponent"), Integer.TYPE).newInstance(ContainerIdReflect, "minecraft:anvil", ChatMessageReflect, 0);
            SendPacketMethod.invoke(PlayerConnectionReflect, PacketPlayOutOpenWindowReflect);

             } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
