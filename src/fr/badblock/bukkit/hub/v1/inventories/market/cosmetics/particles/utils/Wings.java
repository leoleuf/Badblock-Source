package fr.badblock.bukkit.hub.v1.inventories.market.cosmetics.particles.utils;

import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import fr.badblock.bukkit.hub.v1.objects.HubPlayer;
import fr.badblock.bukkit.hub.v1.utils.ParticleEffect;
import fr.badblock.gameapi.players.BadblockPlayer;

public class Wings {

	public static ParticleEffect.OrdinaryColor getClr(DyeColor dyeColor) {
		Color color = dyeColor.getColor();
		return new ParticleEffect.OrdinaryColor(color.getRed(), color.getGreen(), color.getBlue());
	}

	public static void SpawnWings2(Entity p, HubPlayer hubPlayer, BadblockPlayer player) {
		if (hubPlayer.getDyeColor() == null)
			hubPlayer.setDyeColor(DyeColor.ORANGE);
		Location loc = p.getLocation().clone();
		loc.setPitch(0.0F);
		loc.add(0.0D, 1.8D, 0.0D);
		loc.add(loc.getDirection().multiply(-0.2D));
		ParticleEffect.OrdinaryColor color = getClr(hubPlayer.getDyeColor());
		ParticleEffect.OrdinaryColor color2 = getClr(hubPlayer.getDyeColor());
		ParticleEffect.OrdinaryColor color4 = getClr(hubPlayer.getDyeColor());
		Location loc1R = loc.clone();
		loc1R.setYaw(loc1R.getYaw() + 110.0F);
		Location loc2R = loc1R.clone().add(loc1R.getDirection().multiply(1));
		ParticleEffect.REDSTONE.display(color2, loc2R.add(0.0D, 0.8D, 0.0D), 30.0D);

		Location loc3R = loc1R.clone().add(loc1R.getDirection().multiply(0.8D));
		ParticleEffect.REDSTONE.display(color2, loc3R.add(0.0D, 0.6D, 0.0D), 30.0D);
		Location loc4R = loc1R.clone().add(loc1R.getDirection().multiply(0.6D));
		ParticleEffect.REDSTONE.display(color2, loc4R.add(0.0D, 0.4D, 0.0D), 30.0D);
		Location loc5R = loc1R.clone().add(loc1R.getDirection().multiply(0.4D));
		ParticleEffect.REDSTONE.display(color2, loc5R.clone().add(0.0D, -0.2D, 0.0D), 30.0D);
		Location loc6R = loc1R.clone().add(loc1R.getDirection().multiply(0.2D));
		ParticleEffect.REDSTONE.display(color2, loc6R.add(0.0D, -0.2D, 0.0D), 30.0D);

		int zu = 0;
		while (zu <= 3) {
			zu++;
			ParticleEffect.OrdinaryColor color3;
			if (zu == 4) {
				color3 = color2;
			} else {
				color3 = color;
			}
			if ((color4 != null) && ((zu == 4) || (zu == 3))) {
				color3 = color4;
			}
			ParticleEffect.REDSTONE.display(color2, loc2R.add(0.0D, -0.2D, 0.0D), 30.0D);
			ParticleEffect.REDSTONE.display(color3, loc3R.add(0.0D, -0.2D, 0.0D), 30.0D);
			ParticleEffect.REDSTONE.display(color3, loc4R.add(0.0D, -0.2D, 0.0D), 30.0D);
			ParticleEffect.REDSTONE.display(color3, loc5R.add(0.0D, -0.2D, 0.0D), 30.0D);
			ParticleEffect.REDSTONE.display(color3, loc6R.add(0.0D, -0.2D, 0.0D), 30.0D);
		}
		Location loc1L = loc.clone();
		loc1L.setYaw(loc1L.getYaw() - 110.0F);
		Location loc2L = loc1L.clone().add(loc1L.getDirection().multiply(1));
		ParticleEffect.REDSTONE.display(color2, loc2L.add(0.0D, 0.8D, 0.0D), 30.0D);

		Location loc3L = loc1L.clone().add(loc1L.getDirection().multiply(0.8D));
		ParticleEffect.REDSTONE.display(color2, loc3L.add(0.0D, 0.6D, 0.0D), 30.0D);
		Location loc4L = loc1L.clone().add(loc1L.getDirection().multiply(0.6D));
		ParticleEffect.REDSTONE.display(color2, loc4L.add(0.0D, 0.4D, 0.0D), 30.0D);
		Location loc5L = loc1L.clone().add(loc1L.getDirection().multiply(0.4D));
		ParticleEffect.REDSTONE.display(color2, loc5L.clone().add(0.0D, -0.2D, 0.0D), 30.0D);
		Location loc6L = loc1L.clone().add(loc1L.getDirection().multiply(0.2D));
		ParticleEffect.REDSTONE.display(color2, loc6L.add(0.0D, -0.2D, 0.0D), 30.0D);

		zu = 0;
		while (zu <= 3) {
			zu++;
			ParticleEffect.OrdinaryColor color3;
			if (zu == 4) {
				color3 = color2;
			} else {
				color3 = color;
			}
			if ((color4 != null) && ((zu == 4) || (zu == 3))) {
				color3 = color4;
			}
			ParticleEffect.REDSTONE.display(color2, loc2L.add(0.0D, -0.2D, 0.0D), 30.0D);
			ParticleEffect.REDSTONE.display(color3, loc3L.add(0.0D, -0.2D, 0.0D), 30.0D);
			ParticleEffect.REDSTONE.display(color3, loc4L.add(0.0D, -0.2D, 0.0D), 30.0D);
			ParticleEffect.REDSTONE.display(color3, loc5L.add(0.0D, -0.2D, 0.0D), 30.0D);
			ParticleEffect.REDSTONE.display(color3, loc6L.add(0.0D, -0.2D, 0.0D), 30.0D);
		}

	}

}
