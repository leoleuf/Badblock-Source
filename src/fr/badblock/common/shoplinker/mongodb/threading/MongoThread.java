package fr.badblock.common.shoplinker.mongodb.threading;

import fr.badblock.common.shoplinker.mongodb.MongoService;
import fr.badblock.common.shoplinker.mongodb.abs.TechThread;
import fr.badblock.common.shoplinker.mongodb.methods.MongoMethod;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
public class MongoThread extends TechThread<MongoMethod> 
{

	private MongoService mongoService;

	public MongoThread(MongoService mongoService, int id)
	{
		super("MongoThread", mongoService.getQueue(), id);
		setMongoService(mongoService);
	}

	@Override
	public void work(MongoMethod mongoMethod) throws Exception
	{
		mongoMethod.run(getMongoService());
	}

	@Override
	public String getErrorMessage()
	{
		return "[MongoConnector] An error occurred while trying to send packet.";
	}

	@Override
	public boolean isServiceAlive()
	{
		return getMongoService().isAlive();
	}

}
