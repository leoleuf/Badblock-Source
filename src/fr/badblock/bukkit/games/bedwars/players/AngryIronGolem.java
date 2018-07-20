package fr.badblock.bukkit.games.bedwars.players;

import java.lang.reflect.Field;
import java.util.List;

import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftIronGolem;

import fr.badblock.bukkit.games.bedwars.PluginBedWars;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.PathfinderGoal;
import net.minecraft.server.v1_8_R3.PathfinderGoalDefendVillage;
import net.minecraft.server.v1_8_R3.PathfinderGoalHurtByTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalLookAtPlayer;
import net.minecraft.server.v1_8_R3.PathfinderGoalMeleeAttack;
import net.minecraft.server.v1_8_R3.PathfinderGoalMoveThroughVillage;
import net.minecraft.server.v1_8_R3.PathfinderGoalMoveTowardsRestriction;
import net.minecraft.server.v1_8_R3.PathfinderGoalMoveTowardsTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalNearestAttackableTarget;
import net.minecraft.server.v1_8_R3.PathfinderGoalOfferFlower;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomLookaround;
import net.minecraft.server.v1_8_R3.PathfinderGoalRandomStroll;
import net.minecraft.server.v1_8_R3.World;

public class AngryIronGolem  extends net.minecraft.server.v1_8_R3.EntityIronGolem {

	@SuppressWarnings("unchecked")
	public AngryIronGolem(World world) {
		super(world);

		this.bukkitEntity = new CraftIronGolem((CraftServer) PluginBedWars.getInstance().getServer(), this);

		try {
			Field goala = this.goalSelector.getClass().getDeclaredField("goalSelector");
			goala.setAccessible(true);
			((List<PathfinderGoal>) goala.get(this.goalSelector)).clear();
			Field targeta = this.targetSelector.getClass().getDeclaredField("targetSelector");
			targeta.setAccessible(true);
			((List<PathfinderGoal>) targeta.get(this.targetSelector)).clear();
			this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 0.25F, true));
			this.goalSelector.a(2, new PathfinderGoalMoveTowardsTarget(this, 0.22F, 32.0F));
			this.goalSelector.a(3, new PathfinderGoalMoveThroughVillage(this, 0.16F, true));
			this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, 0.16F));
			this.goalSelector.a(5, new PathfinderGoalOfferFlower(this));
			this.goalSelector.a(6, new PathfinderGoalRandomStroll(this, 0.16F));
			this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
			this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
			this.targetSelector.a(1, new PathfinderGoalDefendVillage(this));
			this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, false));
			anger();
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void anger() {
		this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, true));
	}

}