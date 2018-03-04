package fr.badblock.bukkit.games.shootflag.players;

import fr.badblock.gameapi.players.data.InGameData;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public class ShootFlagData implements InGameData
{
	
	public int  kills 		= 0;
	public int  deaths		= 0;
	public int  marks	    = 0;
	public long nextMark    = 0;
	public long cooldown	= 0;

	private long lastItemFrameFlag;
	@Getter@Setter
	private long lastShootFlag;
	
	public long canHurt;
	
	public void itemFrameFlag()
	{
		lastItemFrameFlag = time() + 64;
	}
	
	public boolean isItemFrameFlagged()
	{
		return lastItemFrameFlag != 0 && lastItemFrameFlag > time();
	}
	
	private long time()
	{
		return System.currentTimeMillis();
	}
	
	public int getScore()
	{
		return (kills * 20 + marks * 20) / (deaths == 0 ? 1 : (deaths));
	}
	
}