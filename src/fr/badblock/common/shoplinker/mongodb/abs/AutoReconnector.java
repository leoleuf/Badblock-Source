package fr.badblock.common.shoplinker.mongodb.abs;

import java.util.TimerTask;

import fr.badblock.api.common.utils.threading.TimerUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public abstract class AutoReconnector extends Service
{
	
	private TimerTask task;

	// 	public RedisService(String name, RedisSettings settings) 
	public AutoReconnector(String name, Settings settings) 
	{
		super(name, settings);
		task = run();
		TimerUtils.getTimer().schedule(task, 1000, 1000);
	}

	public abstract boolean	isConnected();

	public abstract void	reconnect();

	public TimerTask run() 
	{
		return new TimerTask()
		{
			@Override
			public void run()
			{
				reconnect();
			}
		};
	}

}
