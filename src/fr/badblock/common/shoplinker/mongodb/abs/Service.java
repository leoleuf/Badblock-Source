package fr.badblock.common.shoplinker.mongodb.abs;

import fr.badblock.api.common.utils.logs.Logger;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public abstract class Service extends Logger
{
	
	private String	name;
	private Settings settings;
	private boolean	dead;
	
	public Service(String name, Settings settings)
	{
		setName(name);
		setSettings(settings);
	}
	
	public abstract void remove();
	
	public boolean isAlive()
	{
		return !isDead();
	}
	
}