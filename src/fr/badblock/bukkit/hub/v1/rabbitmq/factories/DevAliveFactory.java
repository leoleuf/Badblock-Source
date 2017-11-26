package fr.badblock.bukkit.hub.v1.rabbitmq.factories;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class DevAliveFactory {
	public String name;
	public boolean open;
	public int players;
	public int slots;
	public boolean openStaff;
}