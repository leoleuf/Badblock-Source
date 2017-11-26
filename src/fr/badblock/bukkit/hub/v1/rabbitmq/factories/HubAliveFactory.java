package fr.badblock.bukkit.hub.v1.rabbitmq.factories;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HubAliveFactory {

	private int id;
	private String name;
	private boolean opened;
	private int players;
	private Map<String, Integer> ranks;
	private int slots;

	public HubAliveFactory(String name, int players, int slots, boolean opened, Map<String, Integer> ranks) {
		this.setName(name);
		String idString = "";
		for (Character character : name.toCharArray())
			if (Character.isDigit(character))
				idString += character.toString();
		if (idString.equals(""))
			idString = "0";
		try {
			id = Integer.parseInt(idString);
		} catch (Exception error) {
			error.printStackTrace();
		}
		this.setPlayers(players);
		this.setSlots(slots);
		this.setOpened(opened);
		this.setRanks(ranks);
	}

}
