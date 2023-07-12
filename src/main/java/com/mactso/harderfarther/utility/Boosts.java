package com.mactso.harderfarther.utility;

import java.lang.reflect.Field;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mactso.harderfarther.config.MyConfig;
import com.mactso.harderfarther.manager.HarderFartherManager;

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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.coremod.api.ASMAPI;

public class Boosts {

	private static Field fieldXpReward = null;
	private static final Logger LOGGER = LogManager.getLogger();
	static UUID HF_HEALTH_BOOST = UUID.fromString("9ea04686-ff0c-4252-8de7-19b973c8567e");
	private static final float NO_CHANGE = 1.0f;
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

	UUID MAX_SPEED = UUID.fromString("5d5d890a-7e40-4214-b86a-34640749c334");
	UUID MAX_ATTACK = UUID.fromString("18781eb6-da89-4b2e-b47b-a961ab7fafac");
	UUID MAX_KNOCKBACK = UUID.fromString("834d99ba-f10c-4cdf-ab86-580a08b8ac55");

	private static void boostAtkDmg(LivingEntity le, String eDsc, float difficulty) {
		if (MyConfig.isAtkDmgBoosted()) {
			if (le.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
				float baseAttackDamage = (float) le.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue();
				float damageBoost = (MyConfig.getAtkPercent() * difficulty);
				float newAttackDamage = baseAttackDamage + baseAttackDamage * damageBoost;
				le.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(newAttackDamage);
				Utility.debugMsg(2, le,
						"--Boost " + eDsc + " attack damage from " + baseAttackDamage + " to " + newAttackDamage + ".");
			} else {
				Utility.debugMsg(1, le, "erBoost " + eDsc + " Attack Damage Null  .");
			}
		}
	}

	private static void boostHealth(LivingEntity le, String eDsc, float difficulty) {

		if (MyConfig.isHpMaxBoosted()) {
			if (le.getAttribute(Attributes.MAX_HEALTH) != null) {

				float startHealth = le.getHealth();
				float healthBoost = (MyConfig.getHpMaxPercent() * difficulty);
				healthBoost = limitHealthBoostByMob(healthBoost, le);
				le.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier(HF_HEALTH_BOOST,
						"hf_health_boost", healthBoost, Operation.MULTIPLY_TOTAL));
				le.setHealth(le.getMaxHealth());

				Utility.debugMsg(2, le, "--Boost " + eDsc + " " + startHealth + " health to " + le.getHealth()
						+ " with hb= " + healthBoost + "at diff=" + difficulty);

			} else {
				Utility.debugMsg(1, le, "erBoost: " + eDsc + " " + le.getHealth() + " MaxHealth attribute null.");
			}
		} else {
			if (le.getAttribute(Attributes.MAX_HEALTH) != null) {
				le.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(
						new AttributeModifier(HF_HEALTH_BOOST, "hf_health_boost", NO_CHANGE, Operation.MULTIPLY_TOTAL));
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
			Utility.debugMsg(2, le,
					"--Boost " + eDsc + " Xp increased from (" + preXp + ") to (" + fieldXpReward.getInt(le) + ")");
		} catch (Exception e) {
			LOGGER.error("XXX Unexpected Reflection Failure getting xpReward");
			return false;
		}
		return true;
	}

	public static boolean isBoostable(LivingEntity le) {

		Utility.debugMsg(2, "is entity boostable? (" + le.tickCount + " ticks old)");

		if ((le instanceof Monster) && (le.tickCount > 0) && le.tickCount < 120) {
			if (le.getAttribute(Attributes.MAX_HEALTH) == null) {
				Utility.debugMsg(2, "entity can't be boosted.");
				return false;
			}
			if (le.getAttribute(Attributes.MAX_HEALTH).getModifier(HF_HEALTH_BOOST) != null) {
				Utility.debugMsg(2, "entity is already boosted.");
				return false;
			} 
			Utility.debugMsg(2, "entity can be boosted.");
			
			return true;
			
		}
		Utility.debugMsg(2, "entity not a monster or 0 ticks old.");
		
		return false;
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
		} else if (EntityType.getKey((entity.getType())).toString().equals("nasty:skeleton")) {
			healthBoost *= 0.1f; // much lower boost they are self boosted.
		} else if (entity instanceof AbstractSkeleton) {
			healthBoost *= 0.9f;
		}
		return healthBoost;
	}

	// UUID MAX_SPEED = UUID.fromString("5d5d890a-7e40-4214-b86a-34640749c334");
	// UUID MAX_ATTACK = UUID.fromString("18781eb6-da89-4b2e-b47b-a961ab7fafac");
	// UUID MAX_KNOCKBACK = UUID.fromString("834d99ba-f10c-4cdf-ab86-580a08b8ac55");

	public static void doBoostAbilities(LivingEntity le, String eDsc ){

		Utility.debugMsg(2, "doBoosts");

		if (!isBoostable(le))  
			return;

		float difficulty = HarderFartherManager.getDifficultyHere((ServerLevel)le.level(), le);
		
		if (fieldXpReward == null) { // should not fail except when developing a new version or if someone removed
			// this field.
			return;
		}
		Utility.debugMsg(2, "doHealth");
		boostHealth(le, eDsc, difficulty);
		Utility.debugMsg(2, "doSpeed");
		boostSpeed(le, eDsc, difficulty);
		Utility.debugMsg(2, "doAtk");
		boostAtkDmg(le, eDsc, difficulty);
		Utility.debugMsg(2, "doKB");
		boostKnockbackResistance(le, eDsc, difficulty);
		Utility.debugMsg(2, "doXp");
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
		} else if (EntityType.getKey(le.getType()).toString().equals("nasty:skeleton")) {
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

}
