package fr.badblock.bukkit.games.uhc.doublerun.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkUnloadEvent;

import fr.badblock.bukkit.games.uhc.doublerun.runnables.game.ChunkLoaderRunnable;
import fr.badblock.gameapi.BadListener;

public class ChunkUnloadListener extends BadListener {
	
	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event)
	{
		if (!ChunkLoaderRunnable.loadedChunks.contains(event.getChunk()))
		{
			return;
		}
		
		event.setCancelled(true);
	}
}
