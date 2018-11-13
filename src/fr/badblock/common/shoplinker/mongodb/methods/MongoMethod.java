package fr.badblock.common.shoplinker.mongodb.methods;

import fr.badblock.common.shoplinker.mongodb.MongoService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Data
public abstract class MongoMethod 
{
	
	private MongoService mongoService;
	
	public abstract void run(MongoService mongoService2);
	
}
