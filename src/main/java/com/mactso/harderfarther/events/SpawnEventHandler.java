package com.mactso.harderfarther.events;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.monster.AbstractSkeletonEntity;
import net.minecraft.entity.monster.CaveSpiderEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.SpiderEntity;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.IWorldInfo;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SpawnEventHandler {
	private static final Logger LOGGER = LogManager.getLogger();

	private void boostEntityAtkDmg(LivingEntity entity, BlockPos pos, String eDsc, float distanceModifier) {
		if (MyConfig.isAtkDmgBoosted()) {
			if (entity.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
				float baseAttackDamage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
				float damageBoost = (MyConfig.getAtkPercent() * distanceModifier);
				float newAttackDamage = baseAttackDamage + baseAttackDamage * damageBoost;
				entity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(newAttackDamage);

				Utility.debugMsg(2, pos,
						"--Boost " + eDsc + " attack damage from " + baseAttackDamage + " to " + newAttackDamage + ".");
			} else {
				Utility.debugMsg(2, pos, "erBoost " + eDsc + " Attack Damage Null  .");
			}
		}
	}

	private void boostEntityHealth(LivingEntity entity, BlockPos pos, String eDsc, float distanceModifier) {
		if (MyConfig.isHpMaxBoosted()) {
			if (entity.getAttribute(Attributes.MAX_HEALTH) != null) {
				float startHealth = entity.getHealth();
				float healthBoost = (MyConfig.getHpMaxPercent() * distanceModifier);
				healthBoost = limitHealthBoostByMob(healthBoost, entity);
				entity.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(
						new AttributeModifier("maxhealthboost", healthBoost, Operation.MULTIPLY_TOTAL));
				entity.setHealth(entity.getMaxHealth());
				Utility.debugMsg(2, pos, "--Boost " + eDsc + " " + startHealth + " health to " + entity.getHealth());

			} else {
				Utility.debugMsg(1, pos, "erBoost: " + eDsc + " " + entity.getHealth() + " MaxHealth attribute null.");
			}
		}
	}

	private void boostEntityKnockbackResistance(LivingEntity entity, BlockPos pos, String eDsc,
			float distanceModifier) {
		if (MyConfig.isKnockBackBoosted()) {
			if (entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
				float baseKnockBackResistance = (float) entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue();
				if (baseKnockBackResistance == 0) {
					baseKnockBackResistance = getKBRBoostByMob(entity);
				}
				float kbrBoost = (MyConfig.getKnockBackPercent() * distanceModifier);
				float newKnockBackResistance = baseKnockBackResistance + baseKnockBackResistance * kbrBoost;
				entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(newKnockBackResistance);

				Utility.debugMsg(2, pos, "--Boost " + eDsc + " Boost KB Resist from " + baseKnockBackResistance + " to "
						+ newKnockBackResistance + ".");

			} else {
				Utility.debugMsg(2, pos, "erBoost " + entity.getType().toString() + " KB Resist Null .");

			}
		}
	}

	private void boostEntitySpeed(LivingEntity entity, BlockPos pos, String eDsc, float distanceModifier) {

		if (MyConfig.isSpeedBoosted()) {
			if (entity.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
				float baseSpeed = (float) entity.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
				float speedModifier = (MyConfig.getSpeedPercent() * distanceModifier);
				if (entity instanceof ZombieEntity) {
					ZombieEntity z = (ZombieEntity) entity;
					if (z.isBaby()) {
						speedModifier *= 0.5f;
					}
				}
				float newSpeed = baseSpeed + (baseSpeed * speedModifier);
				entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(newSpeed);
				Utility.debugMsg(2, pos, "--Boost " + eDsc + " speed from " + baseSpeed + " to " + newSpeed + ".");
			} else {
				Utility.debugMsg(2, pos, "erBoost : " + eDsc + " Speed Value Null .");
			}
		}
	}

	private float calcDistanceModifier(float distanceFromSpawn, int y) {

		float pctMax = (float) Math.min(1.0, distanceFromSpawn / MyConfig.getModifierMaxDistance());
		if (y < MyConfig.getMinimumSafeAltitude()) {
			pctMax *= 1.06f;
		} else if (y > MyConfig.getMaximumSafeAltitude()) {
			pctMax *= 1.09f;
		}
		return pctMax;

	}

	private float getKBRBoostByMob(LivingEntity entity) {
		float kbrBoost = 0;
		// give some mobs more bonus hit points.
		if (entity instanceof ZombieEntity) {
			kbrBoost = .45f;
		} else if (entity instanceof CaveSpiderEntity) {
			kbrBoost = 0.05f; // lower boost
		} else if (entity instanceof SpiderEntity) {
			kbrBoost = .6f; // higher boost
		} else if (entity instanceof CreeperEntity) {
			kbrBoost = 0.2f; // lower boost
		} else if (entity.getType().getRegistryName().toString().equals("nasty:skeleton")) {
			kbrBoost = 0.2f;
		} else if (entity instanceof AbstractSkeletonEntity) {
			kbrBoost = 0.3f;
		} else if (entity.getMaxHealth() < 10) {
			kbrBoost = 0.05f;
		} else if (entity.getMaxHealth() < 40) {
			kbrBoost = 0.2f;
		} else {
			kbrBoost = 0.35f;
		}
		return kbrBoost * 0.7f;
	}

	private float limitHealthBoostByMob(float healthBoost, LivingEntity entity) {

		// give some mobs more bonus hit points.
		if (entity instanceof ZombieEntity) {
			ZombieEntity z = (ZombieEntity) entity;
			if (z.isBaby()) {
				healthBoost *= 0.6f;
			}
		} else if (entity instanceof CaveSpiderEntity) {
			healthBoost *= 0.5f; // lower boost
		} else if (entity instanceof SpiderEntity) {
			healthBoost *= 1.10f; // higher boost
		} else if (entity instanceof CreeperEntity) {
			healthBoost *= 0.85f; // lower boost
		} else if (entity.getType().getRegistryName().toString().equals("nasty:skeleton")) {
			healthBoost *= 0.1f; // much lower boost they are self boosted.
		} else if (entity instanceof AbstractSkeletonEntity) {
			healthBoost *= 0.9f;
		}
		return healthBoost;
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void onCheckSpawnerSpawn(LivingSpawnEvent.CheckSpawn event) {

		// note may need to put this in "EntityJoinWorld" instead. But be careful to
		// restrict
		// to mobs since that method includes all entities like xp orbs and so on.
		// TODO check nasty mobs and check for non overworld dimension.

		if (!(MyConfig.isMakeMonstersHarderFarther()))
			return;

		if (!(event.getWorld() instanceof ServerWorld)) {
			return;
		}


		ServerWorld level = (ServerWorld) event.getWorld();

		LivingEntity entity = event.getEntityLiving();

		EntityType<?> type = entity.getType();
		if (type.getCategory().isFriendly()) {
			return;
		}

		if (MyConfig.isOnlyOverworld() && (level.dimension() != World.OVERWORLD)) {
			return;
		}

		String dimensionName = level.dimension().location().toString();
		if (MyConfig.isDimensionOmitted(dimensionName)) {
			return;
		}

		BlockPos ePos = entity.blockPosition();
		String eDsc = entity.getType().getRegistryName().toString();

		Utility.debugMsg(2, ePos,
				entity.getName().getString() + " : Hostile Spawn Event. " + entity.getType().toString());

		// no spawns closer to worldspawn than safe distance

		IWorldInfo winfo = level.getLevelData();
		double xzf = level.dimensionType().coordinateScale();
		if (xzf == 0.0) {
			xzf = 1.0d;
		}
		Vector3i spawnVec = new Vector3i(winfo.getXSpawn() / xzf, winfo.getYSpawn(), winfo.getZSpawn() / xzf);
		Vector3i eventVec = new Vector3i(event.getX(), event.getY(), event.getZ());

		if (level.dimension() == World.OVERWORLD) {
			if (Math.sqrt(eventVec.distSqr(spawnVec)) < MyConfig.getSafeDistance()) {
				event.setResult(Result.DENY);
				BlockPos sPos = new BlockPos(event.getX(), event.getY(), event.getZ());
				Utility.debugMsg(2, sPos, "Safe Blocked " + entity.getType().getRegistryName().toString());
				return;
			}
		}

		float distanceFromSpawn = (float) Math.sqrt(eventVec.distSqr(spawnVec));
		float distanceModifier = calcDistanceModifier(distanceFromSpawn, (int) event.getY());

		// TODO boostXp?  Here or in drop?  Is this the bug?  Am I increasingXP twice?

		boostEntityHealth(entity, ePos, eDsc, distanceModifier);
		boostEntitySpeed(entity, ePos, eDsc, distanceModifier);
		boostEntityAtkDmg(entity, ePos, eDsc, distanceModifier);
		boostEntityKnockbackResistance(entity, ePos, eDsc, distanceModifier);

	}

}
