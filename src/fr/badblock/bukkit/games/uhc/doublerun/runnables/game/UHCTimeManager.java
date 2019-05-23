package fr.badblock.bukkit.games.uhc.doublerun.runnables.game;

import fr.badblock.bukkit.games.uhc.doublerun.PluginUHC;
import fr.badblock.bukkit.games.uhc.doublerun.players.TimeProvider;

public class UHCTimeManager implements TimeProvider {

	@Override
	public String getId(int num) {
		if(!PvERunnable.pve)
			return "pve";
		
		if(!PvPRunnable.pvp)
			return PvERunnable.i == 2 ? "pvp" : "teleport";
		
		return "end";
	}

	@Override
	public int getTime(int num) {
		if(!PvERunnable.pve)
			return PvERunnable.ins == null ? 0 : PvERunnable.ins.time;
		
		if(PvPRunnable.ins != null && !PvPRunnable.pvp)
			return PvPRunnable.ins == null ? 0 : PvPRunnable.ins.time;
		
		return GameRunnable.ins == null ? 0 : (PluginUHC.getInstance().getConfiguration().time.prepTime * 60) - GameRunnable.ins.pastTime;
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
