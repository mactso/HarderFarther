package com.mactso.harderfarther.events;

import java.lang.reflect.Field;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.mactso.harderfarther.Main;
import com.mactso.harderfarther.config.MyConfig;

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
import net.minecraft.world.entity.monster.Skeleton;
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
		//FD: net/minecraft/world/entity/Mob/f_21364_ net/minecraft/world/entity/Mob/xpReward
		try {
			String name = ASMAPI.mapField("f_21364_");
			fieldXpReward = Mob.class.getDeclaredField(name);
			fieldXpReward.setAccessible(true);
		} catch (Exception e) {
			LOGGER.error("XXX Unexpected Reflection Failure xpReward in Mob");
		}
	}
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onCheckSpawnerSpawn(LivingSpawnEvent.CheckSpawn event) {
    	
    	// note may need to put this in "EntityJoinWorld" instead.  But be careful to restrict
    	// to mobs since that method includes all entities like xp orbs and so on.
    	// TODO check nasty mobs and check for non overworld dimension.
    	
    	if (!(MyConfig.isMakeMonstersHarderFarther())) return;
    	
    	if (!(event.getWorld() instanceof ServerLevel)) {
    		return;
    	}
    	
    	if (fieldXpReward == null) { // should not fail except when developing a new version or if someone removed this field.
    		return;
    	}
    	ServerLevel serverWorld = (ServerLevel) event.getWorld();

    	LivingEntity entity = event.getEntityLiving();
        
		EntityType<?> type = entity.getType();
		if (type.getCategory().isFriendly()) {  
			return;
		}
 
		if (MyConfig.getaDebugLevel() > 0) {
			System.out.println(Main.MODID + "-" + entity.getName().getString() + " : Hostile Spawn Event.("+event.getX()+" "+event.getY()+ " "+event.getZ()+ ")  " + entity.getType().toString());
		}

		// no spawns closer to worldspawn than safe distance
		
		LevelData winfo = serverWorld.getLevelData();
		double xzf = serverWorld.dimensionType().coordinateScale();
		if (xzf == 0.0)	{
			xzf = 1.0d;
		}
    	Vec3 spawnVec = new Vec3 (winfo.getXSpawn()/xzf,winfo.getYSpawn(),winfo.getZSpawn()/xzf);
    	Vec3 eventVec = new Vec3 (event.getX(),event.getY(),event.getZ());
    	float distanceFromSpawn = (float) (eventVec.distanceTo(spawnVec));
    	if ( serverWorld.dimension() ==  Level.OVERWORLD ) {
        	if (eventVec.distanceTo(spawnVec) < MyConfig.getSafeDistance()) {
    			event.setResult(Result.DENY);
    			return;
        	}
    	}

    	float distanceModifier = calcDistanceModifier(distanceFromSpawn, (int) event.getY());
    	float pctModifier = 1.0f + (distanceModifier/100) * 2;

		try {
			fieldXpReward.setInt(entity, (int) (fieldXpReward.getInt(entity) * pctModifier));
		} catch (Exception e) {
			System.out.println("XXX Unexpected Reflection Failure getting xpReward");
			return;
		}    	

    	if (MyConfig.isHpMaxModified()) {
            if (entity.getAttribute(Attributes.MAX_HEALTH) != null) {
                float maxHealthModifier = calcMaxHealthMultiplier(entity) * (distanceModifier / 100.0f);
	            entity.getAttribute(Attributes.MAX_HEALTH).addPermanentModifier(new AttributeModifier("maxhealthboost", maxHealthModifier, Operation.MULTIPLY_TOTAL));
	    		if (MyConfig.getaDebugLevel() > 1) {
		            System.out.println("Adjust " +entity.getType().toShortString() + entity.getHealth() + " health to " + entity.getMaxHealth());
	    		}
	            entity.setHealth(entity.getMaxHealth());
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
            } else {
	    		if (MyConfig.getaDebugLevel() > 1) {
	    			System.out.println(Main.MODID + " : HSP : " + entity.getType().toString() + " KB Resist Null .");
	    		}        			
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
    	return pctMax * MyConfig.getModifierValue();

	}
    
    
    
	private float calcMaxHealthMultiplier(LivingEntity entity) {
		float maxHealthMultiplier = 1.0f;
		
		// give some mobs more bonus hit points.
		if (entity instanceof Zombie) {
			maxHealthMultiplier = 1.75f;
		} else if (entity instanceof CaveSpider) {
			// no mod
		} else if (entity instanceof Spider) {
			maxHealthMultiplier = 2.1f;
		} else if (entity instanceof Creeper) {
			// no mod
		} else if (entity.getType().getRegistryName().toString().equals("nasty:skeleton")) {
			// no mod
		} else if (entity instanceof AbstractSkeleton) {
			maxHealthMultiplier = 1.5f;
		}
		return maxHealthMultiplier;
	}
    
}
