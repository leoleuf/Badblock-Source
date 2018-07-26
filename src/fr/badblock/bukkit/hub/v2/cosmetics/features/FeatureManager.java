package fr.badblock.bukkit.hub.v2.cosmetics.features;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.google.gson.Gson;

import fr.badblock.bukkit.hub.v2.config.ConfigLoader;
import fr.badblock.bukkit.hub.v2.config.configs.FeaturesConfig;
import fr.badblock.bukkit.hub.v2.cosmetics.features.types.HatsFeatures;
import fr.badblock.bukkit.hub.v2.players.HubStoredPlayer;
import lombok.Getter;
import lombok.Setter;

public class FeatureManager
{

	@Getter @Setter
	private static FeatureManager	instance	= new FeatureManager();

	public void addFeature(HubStoredPlayer hubStoredPlayer, Feature feature)
	{
		long start = System.currentTimeMillis();

		// Expire set
		long expire = -1;
		if (feature.getExpire() > 0)
		{
			expire = start + (feature.getExpire() * 1000L);
		}

		System.out.println(expire);

		// Add owned feature
		OwnedFeature ownedFeature = new OwnedFeature(feature, start, expire);
		List<OwnedFeature> ownedFeatures = hubStoredPlayer.getFeatures().get(feature.getType());
		if (ownedFeatures == null)
		{
			ownedFeatures = new ArrayList<>();
		}
		ownedFeatures.add(ownedFeature);
		hubStoredPlayer.getFeatures().put(feature.getType(), ownedFeatures);
		System.out.println(new Gson().toJson(hubStoredPlayer.getFeatures()));
	}

	public boolean hasFeature(Player player, HubStoredPlayer hubStoredPlayer, String featureRawName)
	{
		String[] splitter = featureRawName.split("_");
		if (splitter.length != 2)
		{	
			System.out.println("[BadBlockHub] A feature must have this pattern : type_name (" + featureRawName + ")");
			return false;
		}

		FeatureType featureType = FeatureType.get(splitter[0]);
		if (featureType == null)
		{
			System.out.println("[BadBlockHub] Unknown feature type for " + featureRawName);
			return false;
		}

		List<OwnedFeature> features = hubStoredPlayer.getFeatures().get(featureType);
		if (features == null)
		{
			features = new ArrayList<>();
		}
		Feature feature = ConfigLoader.getFeatures().getFeatures().get(featureRawName);
		// Unknown feature
		if (feature == null)
		{
			return false;
		}

		FeatureNeeded featureNeeded = feature.getNeeded();
		if (featureNeeded == null)
		{
			return false;
		}

		if (featureNeeded.isEveryoneHaveThis())
		{
			return true;
		}

		if (featureNeeded.getPermissions() != null)
		{
			for (String permission : featureNeeded.getPermissions())
			{
				if (player.hasPermission(permission))
				{
					return true;
				}
			}
		}

		// Count available features
		long count = features.parallelStream().filter(f -> 
		f.getType().getName().toLowerCase().equals(featureRawName) && ((f.getExpire() == -1) || (f.getExpire() != 1 && f.getExpire() > System.currentTimeMillis()))).count();
		return count > 0;
	}

	public static void generateAll()
	{
		HatsFeatures.generateAll();
	}

	public void generate(String rawName)
	{
		FeaturesConfig config = ConfigLoader.getFeatures();
		if (config.getFeatures().containsKey(rawName))
		{
			return;
		}
		System.out.println("[BadBlockHub] Generating " + rawName);
		config.getConfig().set(rawName + ".name", rawName);
		config.getConfig().set(rawName + ".type", FeatureType.get(rawName.split("_")[0]).name());
		config.getConfig().set(rawName + ".badcoinsNeeded", 0);
		config.getConfig().set(rawName + ".shopPointsNeeded", 0);
		config.getConfig().set(rawName + ".levelNeeded", 0);
		config.getConfig().set(rawName + ".expire", -1);
		// Feature needeed
		config.getConfig().set(rawName + ".needed.buyable", false);
		config.getConfig().set(rawName + ".needed.everyoneHaveThis", false);
		config.getConfig().set(rawName + ".needed.permissions", new ArrayList<>());
		try
		{
			config.getConfig().save(config.getFile());
		}
		catch (Exception error)
		{
			error.printStackTrace();
		}
	}

}