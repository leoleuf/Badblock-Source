package fr.badblock.bukkit.hub.v2.cosmetics.features;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Data
public class OwnedFeature
{
	
	private Feature		type;
	private long		start;
	private long		expire;

}
