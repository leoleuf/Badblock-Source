package fr.badblock.common.shoplinker.workers.objects;

import java.util.UUID;

import fr.badblock.common.shoplinker.bukkit.utils.Callback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@AllArgsConstructor
@EqualsAndHashCode (callSuper = false)
@Data
public class GetPlayerShopPoints extends WorkerObject
{
	
	private UUID								player;
	private Callback<Integer>		callback;
	
}