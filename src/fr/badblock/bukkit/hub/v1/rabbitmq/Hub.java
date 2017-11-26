package fr.badblock.bukkit.hub.v1.rabbitmq;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Hub implements Comparator<Hub> {

	private static Map<Integer, Hub> hubs = new HashMap<>();

	public static void create(Hub hub) {
		hubs.put(hub.getId(), hub);
	}

	public static Hub get(int hubId) {
		return hubs.get(hubId);
	}

	public static Collection<Hub> getHubs() {
		return hubs.values();
	}

	private String hubName;
	private int id;
	private ItemStack itemStack;
	private long keepAlive;
	private boolean opened;

	private int players;

	private Map<String, Integer> ranks;

	private int slots;

	public Hub(int id, String hubName) {
		this.setId(id);
		this.setHubName(hubName);
	}

	public void create() {
		create(this);
	}

	public boolean isOnline() {
		return this.isOpened() && this.getKeepAlive() > System.currentTimeMillis();
	}

	public void keepAlive(int players, int slots, boolean opened, Map<String, Integer> ranks) {
		this.setPlayers(players);
		this.setSlots(slots);
		this.setOpened(opened);
		this.setRanks(ranks);
		this.setKeepAlive(System.currentTimeMillis() + 120000L);
	}

	@Override
	public int compare(Hub o1, Hub o2) {
		return ((Integer) o1.getId()).compareTo(o2.getId());
	}

}
