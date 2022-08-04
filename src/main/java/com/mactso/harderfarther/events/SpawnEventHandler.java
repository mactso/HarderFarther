package com.mactso.harderfarther.events;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.manager.HarderFartherManager;
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

	private static void boostAtkDmg(LivingEntity le, String eDsc, float difficulty) {
		if (MyConfig.isAtkDmgBoosted()) {
			if (le.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
				float baseAttackDamage = (float) le.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
				float damageBoost = (MyConfig.getAtkPercent() * difficulty);
				float newAttackDamage = baseAttackDamage + baseAttackDamage * damageBoost;
				le.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(newAttackDamage);
				Utility.debugMsg(2, le,
						"--Boost " + eDsc + " attack damage from " + baseAttackDamage + " to " + newAttackDamage + ".");
			} else {
				Utility.debugMsg(2, le, "erBoost " + eDsc + " Attack Damage Null  .");
			}
		}
	}

	private static void boostHealth(LivingEntity le, String eDsc, float difficulty) {
		if (MyConfig.isHpMaxBoosted()) {
			if (le.getAttribute(Attributes.MAX_HEALTH) != null) {
				float startHealth = le.getHealth();
				float healthBoost = (MyConfig.getHpMaxPercent() * difficulty);
				healthBoost = limitHealthBoostByMob(healthBoost, le);
				le.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(
						new AttributeModifier("maxhealthboost", healthBoost, Operation.MULTIPLY_TOTAL));
				le.setHealth(le.getMaxHealth());
				Utility.debugMsg(2, le, "--Boost " + eDsc + " " + startHealth + " health to " + le.getHealth());

			} else {
				Utility.debugMsg(1, le, "erBoost: " + eDsc + " " + le.getHealth() + " MaxHealth attribute null.");
			}
		}
	}

	// note KnockBack Resistance ranges from 0 to 100% (0.0f to 1.0f)
	private static void boostKnockbackResistance(LivingEntity le, String eDsc, float difficulty) {
		if (MyConfig.isKnockBackBoosted()) {
			if (le.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
				float baseKnockBackResistance = (float) le.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue();
				if (baseKnockBackResistance == 0) {
					baseKnockBackResistance = getKBRBoostByMob(le);
				}
				float kbrBoost = (MyConfig.getKnockBackPercent() * difficulty);
				float newKnockBackResistance = baseKnockBackResistance + baseKnockBackResistance * kbrBoost;
				le.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(newKnockBackResistance);

				Utility.debugMsg(2, le, "--Boost " + eDsc + " Boost KB Resist from " + baseKnockBackResistance + " to "
						+ newKnockBackResistance + ".");

			} else {
				Utility.debugMsg(2, le, "erBoost " + le.getType().toString() + " KB Resist Null .");

			}
		}
	}

	private static void boostSpeed(LivingEntity le, String eDsc, float difficulty) {

		if (MyConfig.isSpeedBoosted()) {
			if (le.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
				float baseSpeed = (float) le.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
				float speedModifier = (MyConfig.getSpeedPercent() * difficulty);
				if (le instanceof Zombie) {
					Zombie z = (Zombie) le;
					if (z.isBaby()) {
						speedModifier *= 0.5f;
					}
				}
				float newSpeed = baseSpeed + (baseSpeed * speedModifier);
				le.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(newSpeed);
				Utility.debugMsg(2, le, "--Boost " + eDsc + " speed from " + baseSpeed + " to " + newSpeed + ".");
			} else {
				Utility.debugMsg(2, le, "erBoost : " + eDsc + " Speed Value Null .");
			}
		}
	}

	private static boolean boostXp(LivingEntity le, String eDsc, float distanceModifier) {
		try {
			int preXp = fieldXpReward.getInt(le);
			fieldXpReward.setInt(le, (int) (fieldXpReward.getInt(le) * (1.0f + distanceModifier)));
			Utility.debugMsg(2, le, "--Boost " + eDsc + " Xp increased from ("+preXp+") to ("+fieldXpReward.getInt(le)+")");
		} catch (Exception e) {
			LOGGER.error("XXX Unexpected Reflection Failure getting xpReward");
			return false;
		}
		return true;
	}

	private static void doBoostAbilities(LivingEntity le, String eDsc, float difficulty) {
		boostHealth(le, eDsc, difficulty);
		boostSpeed(le, eDsc, difficulty);
		boostAtkDmg(le, eDsc, difficulty);
		boostKnockbackResistance(le, eDsc, difficulty);
		boostXp(le, eDsc, difficulty);
	}

	private static float getKBRBoostByMob(LivingEntity le) {
		float kbrBoost = 0;
		// give some mobs more bonus hit points.
		if (le instanceof Zombie) {
			kbrBoost = .45f;
		} else if (le instanceof CaveSpider) {
			kbrBoost = 0.05f; // lower boost
		} else if (le instanceof Spider) {
			kbrBoost = .6f; // higher boost
		} else if (le instanceof Creeper) {
			kbrBoost = 0.2f; // lower boost
		} else if (le.getType().getRegistryName().toString().equals("nasty:skeleton")) {
			kbrBoost = 0.2f;
		} else if (le instanceof AbstractSkeleton) {
			kbrBoost = 0.3f;
		} else if (le.getMaxHealth() < 10) {
			kbrBoost = 0.05f;
		} else if (le.getMaxHealth() < 40) {
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
	public void onSpawnEvent(LivingSpawnEvent.CheckSpawn event) {

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
		ServerLevel serverLevel = (ServerLevel) event.getWorld();

		LivingEntity le = event.getEntityLiving();

			
		EntityType<?> type = le.getType();
		if (type.getCategory().isFriendly()) {
			return;
		}

		if (MyConfig.isOnlyOverworld() && (serverLevel.dimension() != Level.OVERWORLD)) {
			return;
		}

		String dimensionName = serverLevel.dimension().location().toString();
		if (MyConfig.isDimensionOmitted(dimensionName)) {
			return;
		}

		float difficulty = HarderFartherManager.getDifficultyHere(le);
		if (difficulty == 0) 
			return;
		

		String eDsc = le.getType().getRegistryName().toString();

		Utility.debugMsg(1, le,
				le.getName().getString() + " : Hostile Spawn Event. " + le.getType().toString() + " difficulty = " + difficulty);

		// no spawns closer to worldspawn than safe distance

		LevelData winfo = serverLevel.getLevelData();
		double xzf = serverLevel.dimensionType().coordinateScale();
		if (xzf == 0.0) {
			xzf = 1.0d;
		}
		
		Vec3 spawnVec = new Vec3(winfo.getXSpawn() / xzf, winfo.getYSpawn(), winfo.getZSpawn() / xzf);
		Vec3 eventVec = new Vec3(event.getX(), event.getY(), event.getZ());

		if (serverLevel.dimension() == Level.OVERWORLD) {
			if (eventVec.distanceTo(spawnVec) < MyConfig.getSafeDistance()) {
				event.setResult(Result.DENY);
				Utility.debugMsg(2, le, "Safe Blocked " + le.getType().getRegistryName().toString());
				return;
			}
		}


			doBoostAbilities(le, eDsc, difficulty);


	}



}
