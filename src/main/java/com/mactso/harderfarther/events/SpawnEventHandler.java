package com.mactso.harderfarther.events;

import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.config.MyConfig;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.coremod.api.ASMAPI;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
public class SpawnEventHandler {
	private static final Logger LOGGER = LogManager.getLogger();
	
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onCheckSpawnerSpawn(LivingSpawnEvent.CheckSpawn event) {
    	
    	// note may need to put this in "EntityJoinWorld" instead.  But be careful to restrict
    	// to mobs since that method includes all entities like xp orbs and so on.
    	
    	if (!(MyConfig.isMakeMonstersHarderFarther())) return;
    	
    	if (!(event.getWorld() instanceof ServerLevel)) {
    		return;
    	}
    	
    	ServerLevel serverWorld = (ServerLevel) event.getWorld();
    	LivingEntity entity = event.getEntityLiving();
        
		EntityType<?> type = entity.getType();
		if (type.getCategory().isFriendly()) {  
			return;
		}
 
		Field field = null;  // TODO Put this at top and then use static.
		//FD: net/minecraft/world/entity/Mob/f_21364_ net/minecraft/world/entity/Mob/xpReward
		try {
			String name = ASMAPI.mapField("f_21364_");
			field = Mob.class.getDeclaredField(name);
			field.setAccessible(true);
		} catch (Exception e) {
			LOGGER.error("XXX Unexpected Reflection Failure xpReward in Mob");
			return;
		}

		
		if (MyConfig.getaDebugLevel() > 0) {
			System.out.println(Main.MODID + "-" + entity.getName().getString() + " : Hostile Spawn Event.("+event.getX()+" "+event.getY()+ " "+event.getZ()+ ")  " + entity.getType().toString());
		}
    	// no spawns closer to worldspawn than safe distance
    	if (Math.abs(event.getX()) < MyConfig.getSafeDistance()) {
    		if (Math.abs(event.getZ()) < MyConfig.getSafeDistance()) {
    			event.setResult(Result.DENY);
    			return ;
    		}
    	}

    	float distanceModifier = calcDistanceModifier(event, serverWorld);
    	float pctModifier = 1.0f + (distanceModifier/100) * 2;

		int xpReward;
		try {
			xpReward = (int) (field.getInt(entity) * pctModifier);
			field.setInt(entity, xpReward);
		} catch (Exception e) {
			System.out.println("XXX Unexpected Reflection Failure getting xpReward");
			return;
		}    	

    	
    	if (MyConfig.isHpMaxModified()) {
            if (entity.getAttribute(Attributes.MAX_HEALTH) != null) {
	        	float baseMaxHealth = entity.getMaxHealth();
	        	float totalMaxHealth = entity.getMaxHealth();
	        	float healthModifier = (totalMaxHealth * distanceModifier) / 100.0f;
	        	float maxHealthMultiplier = calcMaxHealthMultiplier(entity);
	            totalMaxHealth = baseMaxHealth + (maxHealthMultiplier * healthModifier);    
	            float maxHealthModifier = (((totalMaxHealth/baseMaxHealth) - 1.0f));
	            entity.getAttribute(Attributes.MAX_HEALTH);
	            entity.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("maxhealthboost", maxHealthModifier, Operation.MULTIPLY_TOTAL));

	            entity.setHealth(entity.getMaxHealth());
	    		if (MyConfig.getaDebugLevel() > 1) {
	    			System.out.println(Main.MODID + " : HSP : " + entity.getType().toString() + " Boost HP from "+baseMaxHealth+" to "+ totalMaxHealth + ".");
	    		}
            } else {
	    		if (MyConfig.getaDebugLevel() > 1) {
	    			System.out.println(Main.MODID + " : HSP : " + entity.getType().toString() + " Max Hit Point Value Null .");
	    		}
            }
    	}
    	

    	if (MyConfig.isSpeedModified()) {
            if (entity.getAttribute(Attributes.MOVEMENT_SPEED) != null) {
                float baseSpeed = (float) entity.getAttribute(Attributes.MOVEMENT_SPEED).getValue();
                float newSpeed = baseSpeed * pctModifier;
                entity.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(newSpeed);    		
        		if (MyConfig.getaDebugLevel() > 1) {
        			System.out.println(Main.MODID + " : HSP : " + entity.getType().toString() + " Boost speed from "+baseSpeed+" to "+ newSpeed+ ".");
        		} 
        		
            }else {
	    		if (MyConfig.getaDebugLevel() > 1) {
	    			System.out.println(Main.MODID + " : HSP : " + entity.getType().toString() + " Speed Value Null .");
	    		}        			
    		}
    	}

        if (MyConfig.isAtkDmgModified()) {
            if (entity.getAttribute(Attributes.ATTACK_DAMAGE) != null) {
            	float baseAttackDamage = (float) entity.getAttribute(Attributes.ATTACK_DAMAGE).getValue();
            	
                float newAttackDamage = baseAttackDamage * pctModifier;
                entity.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(newAttackDamage);        	
     
                if (MyConfig.getaDebugLevel() > 1) {
        			System.out.println(Main.MODID + " : HSP : " + entity.getType().toString() + " Boost attack damage from "+baseAttackDamage+" to "+ newAttackDamage+ ".");
        		} 
            } else {
	    		if (MyConfig.getaDebugLevel() > 1) {
	    			System.out.println(Main.MODID + " : HSP : " + entity.getType().toString() + " Attack Damage Null .");
	    		}        			
    		}
        }


        if(MyConfig.isKnockBackModified()) {
            if (entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE) != null) {
            	float baseKnockBackResistance = (float) entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).getValue();
                float newKnockBackResistance = ((1.0f + baseKnockBackResistance) * pctModifier) - 1.0f;
                entity.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(newKnockBackResistance);
                if (MyConfig.getaDebugLevel() > 1) {
        			System.out.println(Main.MODID + " : HSP : " + entity.getType().toString() + " Boost KB Resist from "+baseKnockBackResistance+" to "+ newKnockBackResistance+ ".");
        		}
            	
            }else {
	    		if (MyConfig.getaDebugLevel() > 1) {
	    			System.out.println(Main.MODID + " : HSP : " + entity.getType().toString() + " KB Resist Null .");
	    		}        			
    		}
        }
        
    }



	private float calcDistanceModifier(LivingSpawnEvent.CheckSpawn event, ServerLevel serverLevel) {
		float distanceFromSpawn = (float) Math.abs(serverLevel.getLevelData().getXSpawn()-event.getX()) / 1000.00f;
		
		if (distanceFromSpawn > MyConfig.getModifierMaxDistance()) {
    		distanceFromSpawn = MyConfig.getModifierMaxDistance(); 
    	}
    	if (distanceFromSpawn < MyConfig.getModifierMaxDistance()) {
    		distanceFromSpawn = (float) Math.abs(serverLevel.getLevelData().getZSpawn()-event.getZ()) / 1000.00f;    		
        	if (distanceFromSpawn > MyConfig.getModifierMaxDistance()) {
        		distanceFromSpawn = MyConfig.getModifierMaxDistance(); 
        	}
    	}
    	if (distanceFromSpawn < MyConfig.getModifierMaxDistance()) {
        	Vec3 spawnVec = new Vec3 (serverLevel.getLevelData().getXSpawn(),serverLevel.getLevelData().getYSpawn(),serverLevel.getLevelData().getZSpawn());
        	Vec3 eventVec = new Vec3 (event.getX(),event.getY(),event.getZ());
        	distanceFromSpawn = (float) (eventVec.distanceTo(spawnVec)) ;
    	} 

    	if (distanceFromSpawn > MyConfig.getModifierMaxDistance()) {
    		distanceFromSpawn = MyConfig.getModifierMaxDistance();
    	}
    	
//    	int safe = MyConfig.getSafeDistance();
//    	int mdist = MyConfig.getModifierMaxDistance();
//    	int mval = MyConfig.getModifierValue();
    	float maxModifierDistance = MyConfig.getModifierMaxDistance();
    	float modifierValue = MyConfig.getModifierValue();
    	float pctDistanceToMax = distanceFromSpawn/maxModifierDistance ;
    	float distanceModifier = modifierValue * pctDistanceToMax;

    	double spawnHeight = event.getY();
//    	int minSafeAltitude = MyConfig.getMinimumSafeAltitude();
//    	int maxSafeAltitude = MyConfig.getMaximumSafeAltitude();
    	
		if (spawnHeight < MyConfig.getMinimumSafeAltitude()) {
			distanceModifier = distanceModifier + 2.0f;
		}

		if (spawnHeight > MyConfig.getMaximumSafeAltitude()) {
			distanceModifier = distanceModifier + 3.0f;
		}

		return distanceModifier;
	}
    
    
    
	private float calcMaxHealthMultiplier(LivingEntity entity) {
		float maxHealthMultiplier = 1.0f;

		// give some mobs more bonus hit points.
		if (entity instanceof Zombie) {
			maxHealthMultiplier = 2.0f;
		} else if (entity instanceof CaveSpider) {
			// no mod
		} else if (entity instanceof Spider) {
			maxHealthMultiplier = 2.1f;
		} else if (entity instanceof Creeper) {
			// no mod
		} else if (entity.getType().getRegistryName().toString().equals("nasty:skeleton")) {
			// no mod
		} else if (entity instanceof Skeleton) {
			maxHealthMultiplier = 1.8f;
		}
		return maxHealthMultiplier;
	}
    
}
