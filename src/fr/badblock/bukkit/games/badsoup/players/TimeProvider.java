package fr.badblock.bukkit.games.badsoup.players;

public interface TimeProvider {
	public String getId(int num);
	
	public int getTime(int num);

	public int getProvidedCount();
}
