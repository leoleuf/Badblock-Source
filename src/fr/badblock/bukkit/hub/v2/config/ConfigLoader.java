package fr.badblock.bukkit.hub.v2.config;

import fr.badblock.bukkit.hub.v2.config.configs.ActionBarsConfig;
import fr.badblock.bukkit.hub.v2.config.configs.BossBarsConfig;
import fr.badblock.bukkit.hub.v2.config.configs.FeaturesConfig;
import fr.badblock.bukkit.hub.v2.config.configs.HubLocationsConfig;
import fr.badblock.gameapi.BadblockPlugin;
import lombok.Getter;
import lombok.Setter;

public class ConfigLoader 
{

	@Getter @Setter public static HubLocationsConfig	loc;
	@Getter @Setter public static FeaturesConfig		features;
	@Getter @Setter public static ActionBarsConfig		actionBars;
	@Getter @Setter public static BossBarsConfig		bossBars;

	public static void load(BadblockPlugin plugin)
	{
		setLoc(new HubLocationsConfig(plugin, "locations"));
		setFeatures(new FeaturesConfig(plugin, "features"));
		setActionBars(new ActionBarsConfig(plugin, "actionBars"));
		setBossBars(new BossBarsConfig(plugin, "bossBars"));
	}

}
