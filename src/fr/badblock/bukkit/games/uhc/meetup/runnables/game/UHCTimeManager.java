package fr.badblock.bukkit.games.uhc.meetup.runnables.game;

import fr.badblock.bukkit.games.uhc.meetup.PluginUHC;
import fr.badblock.bukkit.games.uhc.meetup.players.TimeProvider;

public class UHCTimeManager implements TimeProvider {

	@Override
	public String getId(int num) {
		return "pvp";
	}

	@Override
	public int getTime(int num) {
		return GameRunnable.ins == null ? 0 : (PluginUHC.getInstance().getConfiguration().time.totalTime * 60) - GameRunnable.ins.pastTime;
	}

	@Override
	public int getProvidedCount() {
		return 1;
	}

	@Override
	public boolean displayed() {
		return true;
	}

}
