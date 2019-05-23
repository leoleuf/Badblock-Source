package fr.badblock.bukkit.games.survivalgames.players;

public interface TimeProvider {
	public String getId(int num);
	
	public int getTime(int num);

	public int getProvidedCount();
}
