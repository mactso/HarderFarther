package com.mactso.harderfarther.events;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.harderfarther.config.HarderFartherManager;
import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.utility.Utility;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.coremod.api.ASMAPI;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;


public class SpawnEventHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	private static Field fieldXpReward = null;

	static {
		// FD: net/minecraft/world/entity/Mob/f_21364_
		// net/minecraft/world/entity/Mob/xpReward
		try {
			String name = ASMAPI.mapField("f_21364_");
			fieldXpReward = Mob.class.getDeclaredField(name);
			fieldXpReward.setAccessible(true);
		} catch (Exception e) {
			LOGGER.error("XXX Unexpected Reflection Failure xpReward in Mob");
		}
	}

	private static void boostAtkDmg(LivingEntity entity, BlockPos pos, String eDsc, float distanceModifier) {
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

	private static void boostHealth(LivingEntity entity, BlockPos pos, String eDsc, float distanceModifier) {
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

	// note KnockBack Resistance ranges from 0 to 100% (0.0f to 1.0f)
	private static void boostKnockbackResistance(LivingEntity entity, BlockPos pos, String eDsc,
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

	private static void boostSpeed(LivingEntity entity, BlockPos pos, String eDsc, float distanceModifier) {

		if (MyConfig.isSpeedBoosted()) {
			if (entity.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
				float baseSpeed = (float) entity.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
				float speedModifier = (MyConfig.getSpeedPercent() * distanceModifier);
				if (entity instanceof Zombie) {
					Zombie z = (Zombie) entity;
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

	private static boolean boostXp(LivingEntity entity, BlockPos ePos, String eDsc, float distanceModifier) {
		try {
			int preXp = fieldXpReward.getInt(entity);
			fieldXpReward.setInt(entity, (int) (fieldXpReward.getInt(entity) * (1.0f + distanceModifier)));
			Utility.debugMsg(2, ePos, "--Boost " + eDsc + " Xp increased from ("+preXp+") to ("+fieldXpReward.getInt(entity)+")");
		} catch (Exception e) {
			LOGGER.error("XXX Unexpected Reflection Failure getting xpReward");
			return false;
		}
		return true;
	}

	private static void doBoostAbilities(LivingEntity entity, BlockPos ePos, String eDsc, float difficultyPct) {
		boostHealth(entity, ePos, eDsc, difficultyPct);
		boostSpeed(entity, ePos, eDsc, difficultyPct);
		boostAtkDmg(entity, ePos, eDsc, difficultyPct);
		boostKnockbackResistance(entity, ePos, eDsc, difficultyPct);
		boostXp(entity, ePos, eDsc, difficultyPct);
	}

	private static float getKBRBoostByMob(LivingEntity entity) {
		float kbrBoost = 0;
		// give some mobs more bonus hit points.
		if (entity instanceof Zombie) {
			kbrBoost = .45f;
		} else if (entity instanceof CaveSpider) {
			kbrBoost = 0.05f; // lower boost
		} else if (entity instanceof Spider) {
			kbrBoost = .6f; // higher boost
		} else if (entity instanceof Creeper) {
			kbrBoost = 0.2f; // lower boost
		} else if (entity.getType().getRegistryName().toString().equals("nasty:skeleton")) {
			kbrBoost = 0.2f;
		} else if (entity instanceof AbstractSkeleton) {
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

	private static float limitHealthBoostByMob(float healthBoost, LivingEntity entity) {

		// give some mobs more bonus hit points.
		if (entity instanceof Zombie) {
			Zombie z = (Zombie) entity;
			if (z.isBaby()) {
				healthBoost *= 0.6f;
			}
		} else if (entity instanceof CaveSpider) {
			healthBoost *= 0.5f; // lower boost
		} else if (entity instanceof Spider) {
			healthBoost *= 1.10f; // higher boost
		} else if (entity instanceof Creeper) {
			healthBoost *= 0.85f; // lower boost
		} else if (entity.getType().getRegistryName().toString().equals("nasty:skeleton")) {
			healthBoost *= 0.1f; // much lower boost they are self boosted.
		} else if (entity instanceof AbstractSkeleton) {
			healthBoost *= 0.9f;
		}
		return healthBoost;
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onSpawnEvent(LivingSpawnEvent.CheckSpawn event) {

		// note may need to put this in "EntityJoinWorld" instead. But be careful to restrict
		// to mobs since that method includes all entities like xp orbs and so on.
		// EntityJoinWorld applies on reloading from save too tho.
		// So would need to check attributes before applying them.

		if (!(MyConfig.isMakeMonstersHarderFarther()) 
				&& (!MyConfig.isMakeHarderOverTime())
				&& (!MyConfig.isUseGrimCitadels())
				) 
			return;

		if (!(event.getWorld() instanceof ServerLevel)) {
			return;
		}

		if (fieldXpReward == null) { // should not fail except when developing a new version or if someone removed
										// this field.
			return;
		}
		ServerLevel slevel = (ServerLevel) event.getWorld();

		LivingEntity entity = event.getEntityLiving();

			
		EntityType<?> type = entity.getType();
		if (type.getCategory().isFriendly()) {
			return;
		}

		if (MyConfig.isOnlyOverworld() && (slevel.dimension() != Level.OVERWORLD)) {
			return;
		}

		String dimensionName = slevel.dimension().location().toString();
		if (MyConfig.isDimensionOmitted(dimensionName)) {
			return;
		}

		BlockPos ePos = entity.blockPosition();
		String eDsc = entity.getType().getRegistryName().toString();

		Utility.debugMsg(2, ePos,
				entity.getName().getString() + " : Hostile Spawn Event. " + entity.getType().toString());

		// no spawns closer to worldspawn than safe distance

		LevelData winfo = slevel.getLevelData();
		double xzf = slevel.dimensionType().coordinateScale();
		if (xzf == 0.0) {
			xzf = 1.0d;
		}
		
		Vec3 spawnVec = new Vec3(winfo.getXSpawn() / xzf, winfo.getYSpawn(), winfo.getZSpawn() / xzf);
		Vec3 eventVec = new Vec3(event.getX(), event.getY(), event.getZ());

		if (slevel.dimension() == Level.OVERWORLD) {
			if (eventVec.distanceTo(spawnVec) < MyConfig.getSafeDistance()) {
				event.setResult(Result.DENY);
				BlockPos sPos = new BlockPos(event.getX(), event.getY(), event.getZ());
				Utility.debugMsg(2, sPos, "Safe Blocked " + entity.getType().getRegistryName().toString());
				return;
			}
		}

		float difficultyPct = HarderFartherManager.getDifficultyPct(entity, slevel, winfo, ePos);
		
		doBoostAbilities(entity, ePos, eDsc, difficultyPct);

	}



}
